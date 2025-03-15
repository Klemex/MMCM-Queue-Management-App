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

public class Helpdesk extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpdesk);

        // Get references to UI components
        CheckBox checkBox1 = findViewById(R.id.checkBox); // Assistance with account-related issues
        CheckBox checkBox2 = findViewById(R.id.checkBox3); // Password reset or account recovery
        CheckBox checkBox3 = findViewById(R.id.checkBox4); // Inquiries about system outages or maintenance
        CheckBox checkBox4 = findViewById(R.id.checkBox5); // Technical support for software or hardware
        EditText studentIdEditText = findViewById(R.id.editTextNumber); // Student ID
        EditText othersText = findViewById(R.id.othersText); // Others text input
        Button confirmQueueButton = findViewById(R.id.button);

        // Initialize Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference helpdeskCollection = db.collection("helpdesk_data"); // Firestore collection reference

        // Set OnClickListener for the confirm button
        confirmQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture data from the UI components
                String studentId = studentIdEditText.getText().toString().trim();
                String others = othersText.getText().toString().trim();

                // Validate student ID (must be exactly 10 digits)
                if (!studentId.matches("\\d{10}")) {
                    Toast.makeText(Helpdesk.this, "Invalid ID number. Please enter your School ID Number.", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if the ID is invalid
                }

                // Store the checkbox states and combine them into one field
                String accountIssues = checkBox1.isChecked() ? "account issues" : "";
                String passwordReset = checkBox2.isChecked() ? "password reset" : "";
                String systemOutages = checkBox3.isChecked() ? "system outages" : "";
                String techSupport = checkBox4.isChecked() ? "tech support" : "";

                // Combine all data into the 'purpose' field
                String purpose = accountIssues + (accountIssues.isEmpty() ? "" : ", ") +
                        passwordReset + (passwordReset.isEmpty() ? "" : ", ") +
                        systemOutages + (systemOutages.isEmpty() ? "" : ", ") +
                        techSupport + (techSupport.isEmpty() ? "" : ", ") +
                        others;

                // Create a map to hold the data
                Map<String, Object> helpdeskData = new HashMap<>();
                helpdeskData.put("student_id", studentId);
                helpdeskData.put("purpose", purpose); // Store the combined purpose
                helpdeskData.put("timestamp", FieldValue.serverTimestamp()); // Add timestamp for sorting

                // Save the data to Firestore
                helpdeskCollection.add(helpdeskData)
                        .addOnSuccessListener(documentReference -> {
                            // Data saved successfully
                            Toast.makeText(Helpdesk.this, "Ticket Secured!", Toast.LENGTH_SHORT).show();

                            // After saving, go to Queue Activity
                            Intent intent = new Intent(Helpdesk.this, Queue.class);
                            startActivity(intent);  // Start Queue Activity
                            finish(); // Close Helpdesk activity
                        })
                        .addOnFailureListener(e -> {
                            // Error saving data
                            Toast.makeText(Helpdesk.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
