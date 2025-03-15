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

public class Scholarship extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scholarship);

        // Get references to UI components
        CheckBox checkBox1 = findViewById(R.id.checkBox); // Apply for Scholarship
        CheckBox checkBox2 = findViewById(R.id.checkBox3); // Applying for Multiple Scholarships
        CheckBox checkBox3 = findViewById(R.id.checkBox4); // Deadline for Scholarship Application
        CheckBox checkBox4 = findViewById(R.id.checkBox5); // Eligibility Criteria for Scholarships
        EditText studentIdEditText = findViewById(R.id.editTextNumber); // Student ID
        EditText othersText = findViewById(R.id.othersText); // Others text input
        Button confirmQueueButton = findViewById(R.id.button);

        // Initialize Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference scholarshipCollection = db.collection("scholarship_data"); // Firestore collection reference

        // Set OnClickListener for the confirm button
        confirmQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture data from the UI components
                String studentId = studentIdEditText.getText().toString().trim();
                String others = othersText.getText().toString().trim();

                // Validate student ID (must be exactly 10 digits)
                if (!studentId.matches("\\d{10}")) {
                    Toast.makeText(Scholarship.this, "Invalid ID number. Please enter your School ID Number.", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if the ID is invalid
                }

                // Store the checkbox states and combine them into one field
                String applyScholarship = checkBox1.isChecked() ? "apply_scholarship" : "";
                String multipleScholarships = checkBox2.isChecked() ? "multiple_scholarships" : "";
                String deadline = checkBox3.isChecked() ? "deadline" : "";
                String eligibility = checkBox4.isChecked() ? "eligibility" : "";

                // Combine all data into the 'purpose' field
                String purpose = applyScholarship + (applyScholarship.isEmpty() ? "" : ", ") +
                        multipleScholarships + (multipleScholarships.isEmpty() ? "" : ", ") +
                        deadline + (deadline.isEmpty() ? "" : ", ") +
                        eligibility + (eligibility.isEmpty() ? "" : ", ") +
                        others;

                // Create a map to hold the data
                Map<String, Object> scholarshipData = new HashMap<>();
                scholarshipData.put("student_id", studentId);
                scholarshipData.put("purpose", purpose); // Store the combined purpose
                scholarshipData.put("timestamp", FieldValue.serverTimestamp()); // Add timestamp for sorting

                // Save the data to Firestore
                scholarshipCollection.add(scholarshipData)
                        .addOnSuccessListener(documentReference -> {
                            // Data saved successfully
                            Toast.makeText(Scholarship.this, "Ticket Secured!", Toast.LENGTH_SHORT).show();

                            // After saving, go to Queue Activity
                            Intent intent = new Intent(Scholarship.this, Queue.class);
                            startActivity(intent);  // Start Queue Activity
                            finish(); // Close Scholarship activity
                        })
                        .addOnFailureListener(e -> {
                            // Error saving data
                            Toast.makeText(Scholarship.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
