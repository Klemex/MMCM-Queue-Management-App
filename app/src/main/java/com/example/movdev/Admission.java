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

public class Admission extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admission);

        // Get references to UI components
        CheckBox checkBox1 = findViewById(R.id.checkBox); // Admission requirements and eligibility
        CheckBox checkBox2 = findViewById(R.id.checkBox3); // Enrollment procedures for returning students
        CheckBox checkBox3 = findViewById(R.id.checkBox4); // Inquiries about application deadlines
        CheckBox checkBox4 = findViewById(R.id.checkBox5); // Application process for new students
        EditText studentIdEditText = findViewById(R.id.editTextNumber); // Student ID
        EditText othersText = findViewById(R.id.othersText); // Others text input
        Button confirmQueueButton = findViewById(R.id.button);

        // Initialize Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference admissionCollection = db.collection("admission_data"); // Firestore collection reference

        // Set OnClickListener for the confirm button
        confirmQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture data from the UI components
                String studentId = studentIdEditText.getText().toString().trim();
                String others = othersText.getText().toString().trim();

                // Validate student ID (must be exactly 10 digits)
                if (!studentId.matches("\\d{10}")) {
                    Toast.makeText(Admission.this, "Invalid ID number. Please enter your School ID Number.", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if the ID is invalid
                }

                // Store the checkbox states and combine them into one field
                String requirements = checkBox1.isChecked() ? "requirements" : "";
                String procedures = checkBox2.isChecked() ? "procedures" : "";
                String deadlines = checkBox3.isChecked() ? "deadlines" : "";
                String application = checkBox4.isChecked() ? "application" : "";

                // Combine all data into the 'purpose' field
                String purpose = requirements + (requirements.isEmpty() ? "" : ", ") +
                        procedures + (procedures.isEmpty() ? "" : ", ") +
                        deadlines + (deadlines.isEmpty() ? "" : ", ") +
                        application + (application.isEmpty() ? "" : ", ") +
                        others;

                // Create a map to hold the data
                Map<String, Object> admissionData = new HashMap<>();
                admissionData.put("student_id", studentId);
                admissionData.put("purpose", purpose); // Store the combined purpose
                admissionData.put("timestamp", FieldValue.serverTimestamp()); // Add timestamp for sorting

                // Save the data to Firestore
                admissionCollection.add(admissionData)
                        .addOnSuccessListener(documentReference -> {
                            // Data saved successfully
                            Toast.makeText(Admission.this, "Ticket Secured!", Toast.LENGTH_SHORT).show();

                            // After saving, go to Queue Activity
                            Intent intent = new Intent(Admission.this, Queue.class);
                            startActivity(intent);  // Start Queue Activity
                            finish(); // Close Admission activity
                        })
                        .addOnFailureListener(e -> {
                            // Error saving data
                            Toast.makeText(Admission.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
