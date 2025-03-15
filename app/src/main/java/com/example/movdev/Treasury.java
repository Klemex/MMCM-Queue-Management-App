package com.example.movdev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class Treasury extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasury);

        // Get references to UI components
        CheckBox checkBox1 = findViewById(R.id.checkBox); // Payment of Tuition Fee
        CheckBox checkBox2 = findViewById(R.id.checkBox3); // Processing of Refunds
        CheckBox checkBox3 = findViewById(R.id.checkBox4); // Outstanding Balances/Dues
        CheckBox checkBox4 = findViewById(R.id.checkBox5); // Issuance of Official Receipts
        EditText studentIdEditText = findViewById(R.id.editTextNumber); // Student ID
        EditText othersText = findViewById(R.id.othersText); // Others text input
        Button confirmQueueButton = findViewById(R.id.button);

        // Initialize Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference treasuryCollection = db.collection("treasury_data"); // Firestore collection reference

        // Set OnClickListener for the confirm button
        confirmQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture data from the UI components
                String studentId = studentIdEditText.getText().toString().trim();
                String others = othersText.getText().toString().trim();

                // Validate student ID (must be exactly 10 digits)
                if (!studentId.matches("\\d{10}")) {
                    Toast.makeText(Treasury.this, "Invalid ID number. Please enter your School ID Number.", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if the ID is invalid
                }

                // Store the checkbox states and combine them into one field
                String payment = checkBox1.isChecked() ? "payment" : "";
                String refunds = checkBox2.isChecked() ? "refunds" : "";
                String balances = checkBox3.isChecked() ? "balances" : "";
                String receipts = checkBox4.isChecked() ? "receipts" : "";

                // Combine all data into the 'purpose' field
                String purpose = payment + (payment.isEmpty() ? "" : ", ") +
                        refunds + (refunds.isEmpty() ? "" : ", ") +
                        balances + (balances.isEmpty() ? "" : ", ") +
                        receipts + (receipts.isEmpty() ? "" : ", ") +
                        others;

                // Create a map to hold the data
                Map<String, Object> treasuryData = new HashMap<>();
                treasuryData.put("student_id", studentId);
                treasuryData.put("purpose", purpose); // Store the combined purpose
                treasuryData.put("timestamp", FieldValue.serverTimestamp()); // Add timestamp for sorting

                // Save the data to Firestore
                treasuryCollection.add(treasuryData)
                        .addOnSuccessListener(documentReference -> {
                            // Data saved successfully
                            Toast.makeText(Treasury.this, "Ticket Secured!", Toast.LENGTH_SHORT).show();

                            // After saving, go to Queue Activity
                            Intent intent = new Intent(Treasury.this, Queue.class);
                            startActivity(intent);  // Start Queue Activity
                            finish(); // Close Treasury activity
                        })
                        .addOnFailureListener(e -> {
                            // Error saving data
                            Toast.makeText(Treasury.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
