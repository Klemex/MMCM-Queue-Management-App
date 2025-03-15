package com.example.movdev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class Queue extends AppCompatActivity {

    private FirebaseFirestore db;
    private String documentId;
    private TextView ticketDisplay, purposeTextView, idNumberTextView;
    private CollectionReference ticketCollection;
    private String selectedDepartment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        db = FirebaseFirestore.getInstance();
        ticketCollection = db.collection("ticket_data");

        ticketDisplay = findViewById(R.id.ticketDisplay);
        purposeTextView = findViewById(R.id.purposecheck);
        idNumberTextView = findViewById(R.id.idnumber);
        Button cancelQueueButton = findViewById(R.id.cancel_button);

        // Get the selected department from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("DEPARTMENT")) {
            selectedDepartment = intent.getStringExtra("DEPARTMENT") + "_data";
        } else {
            // Default to registrar if no department is specified
            selectedDepartment = "registrar_data";
        }

        ImageView logoImageView = findViewById(R.id.imageView5);
        logoImageView.setOnClickListener(v -> {
            Intent intent1 = new Intent(Queue.this, AdminLogin.class);
            startActivity(intent1);
        });

        fetchUserData();

        cancelQueueButton.setOnClickListener(v -> cancelQueue());
    }

    private void fetchUserData() {
        if (selectedDepartment.isEmpty()) {
            Toast.makeText(this, "No department selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Query the specific department collection
        CollectionReference departmentCollection = db.collection(selectedDepartment);
        departmentCollection.orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        processDocumentSnapshot(queryDocumentSnapshots.getDocuments().get(0), departmentCollection);
                    } else {
                        // No data found in the selected department, show a message
                        Toast.makeText(Queue.this, "No data found for " + selectedDepartment, Toast.LENGTH_SHORT).show();

                        // Check other departments in order of priority
                        checkOtherDepartments();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Queue.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkOtherDepartments() {
        // List of all department collections to check
        String[] departments = {
                "registrar_data",
                "scholarship_data",
                "treasury_data",
                "helpdesk_data",
                "admission_data"
        };

        // Skip the already checked department
        for (String department : departments) {
            if (!department.equals(selectedDepartment)) {
                checkDepartment(department);
            }
        }
    }

    private void checkDepartment(String department) {
        CollectionReference departmentCollection = db.collection(department);
        departmentCollection.orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Found data in another department
                        selectedDepartment = department; // Update the selected department
                        processDocumentSnapshot(queryDocumentSnapshots.getDocuments().get(0), departmentCollection);
                    }
                    // If empty, the next department will be checked automatically
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Queue.this, "Error checking " + department + ": " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void processDocumentSnapshot(DocumentSnapshot document, CollectionReference departmentCollection) {
        documentId = document.getId();

        // Get the student data from the document
        String purpose = document.getString("purpose");
        String studentId = document.getString("student_id");

        // Get correct prefix for the department
        String prefix = getCollectionPrefix(selectedDepartment);

        // Generate ticket number and update UI
        generateTicketNumber(prefix, ticketNumber -> {
            // Update UI with ticket number
            ticketDisplay.setText(ticketNumber);

            // Update the document with the ticket number
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("ticket_number", ticketNumber);
            departmentCollection.document(documentId).update(updateData)
                    .addOnSuccessListener(aVoid -> {
                        // Successfully updated document
                        saveTicketToDatabase(studentId, purpose, ticketNumber);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(Queue.this, "Error updating ticket: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show()
                    );
        });

        // Update UI with student info
        purposeTextView.setText(purpose);
        idNumberTextView.setText(studentId);
    }

    private void cancelQueue() {
        if (documentId != null && !selectedDepartment.isEmpty()) {
            db.collection(selectedDepartment).document(documentId).delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Queue.this, "Queue Canceled", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(Queue.this, "Error canceling queue: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        } else {
            Toast.makeText(Queue.this, "No queue to cancel", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTicketToDatabase(String studentId, String purpose, String ticketNumber) {
        Map<String, Object> ticketData = new HashMap<>();
        ticketData.put("student_id", studentId);
        ticketData.put("purpose", purpose);
        ticketData.put("ticket_number", ticketNumber);
        ticketData.put("timestamp", Timestamp.now());
        ticketData.put("department", selectedDepartment);

        ticketCollection.add(ticketData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(Queue.this, "Ticket saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Queue.this, "Error saving ticket: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getCollectionPrefix(String collectionName) {
        Map<String, String> collectionPrefixes = new HashMap<>();
        collectionPrefixes.put("registrar_data", "R");
        collectionPrefixes.put("helpdesk_data", "H");
        collectionPrefixes.put("treasury_data", "T");
        collectionPrefixes.put("scholarship_data", "S");
        collectionPrefixes.put("admission_data", "A");
        return collectionPrefixes.getOrDefault(collectionName, "X");  // Default to X if unknown
    }

    private void generateTicketNumber(String prefix, TicketCallback callback) {
        // Find the highest ticket number for this prefix
        ticketCollection
                .whereGreaterThanOrEqualTo("ticket_number", prefix)
                .whereLessThan("ticket_number", prefix + "\uf8ff")  // Unicode trick for range queries
                .orderBy("ticket_number", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int nextNumber = 1;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot lastTicket = queryDocumentSnapshots.getDocuments().get(0);
                        String lastTicketNumber = lastTicket.getString("ticket_number");
                        if (lastTicketNumber != null && lastTicketNumber.startsWith(prefix)) {
                            try {
                                // Extract number part (e.g., "R001" -> 1)
                                int lastNumber = Integer.parseInt(lastTicketNumber.substring(1));
                                nextNumber = lastNumber + 1;
                            } catch (NumberFormatException ignored) {
                                // If parsing fails, start with 1
                            }
                        }
                    }
                    // Format with leading zeros (e.g., 1 -> "001")
                    String newTicketNumber = String.format("%s%03d", prefix, nextNumber);
                    callback.onTicketGenerated(newTicketNumber);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Queue.this, "Error generating ticket: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    interface TicketCallback {
        void onTicketGenerated(String ticketNumber);
    }
}