package com.example.movdev;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Counter extends AppCompatActivity {

    private FirebaseFirestore db;
    private Dialog ticketDialog;
    private TextView dialogTitle, dialogTitle2, purposeText, idText;
    private Button nextButton, callButton, resetButton;

    private String currentTicketId;
    private String currentPurpose;
    private int currentTicketNumber = 0;
    private String servicePrefix;
    private int counterNumber;

    // Collections
    private CollectionReference registrarCollection, helpdeskCollection, treasuryCollection,
            scholarshipCollection, admissionCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        registrarCollection = db.collection("registrar_data");
        helpdeskCollection = db.collection("helpdesk_data");
        treasuryCollection = db.collection("treasury_data");
        scholarshipCollection = db.collection("scholarship_data");
        admissionCollection = db.collection("admission_data");

        // Set up all counter buttons
        setupDepartmentButtons("Scholarship", R.id.btnScholarshipCounter1, R.id.btnScholarshipCounter2);
        setupDepartmentButtons("Helpdesk", R.id.btnHelpdeskCounter1, R.id.btnHelpdeskCounter2);
        setupDepartmentButtons("Registrar", R.id.btnRegistrarCounter1, R.id.btnRegistrarCounter2);
        setupDepartmentButtons("Treasury", R.id.btnTreasuryCounter1, R.id.btnTreasuryCounter2);
        setupDepartmentButtons("Admission", R.id.btnAdmissionCounter1, R.id.btnAdmissionCounter2);
    }

    private void setupDepartmentButtons(String serviceName, int button1Id, int button2Id) {
        Button btn1 = findViewById(button1Id);
        Button btn2 = findViewById(button2Id);

        btn1.setOnClickListener(v -> showTicketDialog(serviceName, 1));
        btn2.setOnClickListener(v -> showTicketDialog(serviceName, 2));
    }

    private void showTicketDialog(String serviceName, int counterNum) {
        // Save the counter number and service for this session
        this.counterNumber = counterNum;
        this.currentPurpose = serviceName;

        // Setup prefixes based on department
        switch (serviceName.toLowerCase()) {
            case "scholarship":
                servicePrefix = "S";
                break;
            case "helpdesk":
                servicePrefix = "H";
                break;
            case "registrar":
                servicePrefix = "R";
                break;
            case "treasury":
                servicePrefix = "T";
                break;
            case "admission":
                servicePrefix = "A";
                break;
        }

        // Create and show the dialog
        ticketDialog = new Dialog(this);
        ticketDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ticketDialog.setContentView(R.layout.dialog_ticket);
        ticketDialog.setCancelable(true); // Allow closing by tapping outside or pressing back

        // Initialize dialog views
        dialogTitle = ticketDialog.findViewById(R.id.dialogTitle);
        dialogTitle2 = ticketDialog.findViewById(R.id.dialogTitle2);
        purposeText = ticketDialog.findViewById(R.id.purposeText);
        idText = ticketDialog.findViewById(R.id.idText);
        nextButton = ticketDialog.findViewById(R.id.nextButton);
        callButton = ticketDialog.findViewById(R.id.callButton);
        resetButton = ticketDialog.findViewById(R.id.resetButton);

        // Set dialog title
        dialogTitle.setText(serviceName + " Counter " + counterNum);

        // Set default values for ticket and counter
        String defaultTicketNumber = servicePrefix + "000";
        String defaultCounterNumber = "Counter0";

        dialogTitle2.setText("Ticket #" + defaultTicketNumber);
        purposeText.setText(serviceName);
        idText.setText("Student ID: ");

        // Set button click listeners
        nextButton.setOnClickListener(v -> fetchNextTicket());
        resetButton.setOnClickListener(v -> resetCounter());
        callButton.setOnClickListener(v -> {
            // Call button functionality
            Toast.makeText(Counter.this, "Calling customer", Toast.LENGTH_SHORT).show();
            // Update RealTimeQueue activity
            updateRealTimeQueue(servicePrefix, defaultTicketNumber, defaultCounterNumber);
        });

        // Show the dialog
        ticketDialog.show();
    }

    private void fetchNextTicket() {
        // Determine which collection to query based on current purpose
        if (currentPurpose == null) {
            Toast.makeText(this, "Select a department first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Select the right collection
        CollectionReference collection;
        switch (currentPurpose.toLowerCase()) {
            case "registrar":
                collection = registrarCollection;
                break;
            case "helpdesk":
                collection = helpdeskCollection;
                break;
            case "treasury":
                collection = treasuryCollection;
                break;
            case "scholarship":
                collection = scholarshipCollection;
                break;
            case "admission":
                collection = admissionCollection;
                break;
            default:
                Toast.makeText(this, "Invalid department", Toast.LENGTH_SHORT).show();
                return;
        }

        fetchTicketFromCollection(collection, servicePrefix);
    }

    private void fetchTicketFromCollection(CollectionReference collection, String prefix) {
        // Query based on counter number (odd or even)
        collection.orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        boolean ticketFound = false;

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String ticketNumber = document.getString("ticket_number");

                            // If ticket number doesn't exist yet, skip
                            if (ticketNumber == null || ticketNumber.isEmpty()) {
                                continue;
                            }

                            // Check if ticket matches counter (odd/even)
                            if (ticketNumber.startsWith(prefix)) {
                                try {
                                    int numPart = Integer.parseInt(ticketNumber.substring(1));
                                    boolean isOdd = numPart % 2 == 1;

                                    // Counter 1 gets odd tickets, Counter 2 gets even tickets
                                    if ((counterNumber == 1 && isOdd) || (counterNumber == 2 && !isOdd)) {
                                        // Found matching ticket
                                        currentTicketId = document.getId();
                                        currentPurpose = document.getString("purpose");
                                        servicePrefix = prefix;
                                        currentTicketNumber = numPart;

                                        // Update UI
                                        updateTicketDisplay(
                                                ticketNumber,
                                                currentPurpose,
                                                document.getString("student_id")
                                        );

                                        // Update RealTimeQueue activity
                                        updateRealTimeQueue(servicePrefix, ticketNumber, "Counter" + counterNumber);

                                        ticketFound = true;
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    // Skip invalid ticket numbers
                                    continue;
                                }
                            }
                        }

                        if (!ticketFound) {
                            Toast.makeText(Counter.this,
                                    "No matching tickets found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Counter.this,
                                "No tickets in queue", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(Counter.this,
                        "Error retrieving tickets: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateTicketDisplay(String ticketNumber, String purpose, String studentId) {
        dialogTitle2.setText("Ticket #" + ticketNumber);
        purposeText.setText(purpose);
        idText.setText("Student ID: " + studentId);

        // Enable call button once we have a ticket
        callButton.setEnabled(true);
    }

    private void resetCounter() {
        if (currentPurpose == null || servicePrefix == null) {
            Toast.makeText(this, "No active ticket to reset", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reset to default values
        String defaultTicketNumber = servicePrefix + "000";
        String defaultCounterNumber = "Counter0";

        dialogTitle2.setText("Ticket #" + defaultTicketNumber);
        purposeText.setText(currentPurpose);
        idText.setText("Student ID: ");

        // Clear current ticket data but maintain the service
        currentTicketNumber = 0;

        // Update RealTimeQueue activity
        updateRealTimeQueue(servicePrefix, defaultTicketNumber, defaultCounterNumber);

        Toast.makeText(this, "Counter reset to default", Toast.LENGTH_SHORT).show();
    }

    private void updateRealTimeQueue(String prefix, String ticketNumber, String counterNumber) {
        // Use an Intent to update RealTimeQueue
        Intent intent = new Intent(Counter.this, RealTimeQueue.class);
        intent.putExtra("prefix", prefix);
        intent.putExtra("ticketNumber", ticketNumber);
        intent.putExtra("counterNumber", counterNumber);
        startActivity(intent);
    }
}