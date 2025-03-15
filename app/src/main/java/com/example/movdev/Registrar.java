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

public class Registrar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        // Get references to UI components
        CheckBox checkBox1 = findViewById(R.id.checkBox); // Advising
        CheckBox checkBox2 = findViewById(R.id.checkBox3); // Recording academic progress
        CheckBox checkBox3 = findViewById(R.id.checkBox4); // Permanent record archival
        CheckBox checkBox4 = findViewById(R.id.checkBox5); // Distribution of grades
        EditText studentIdEditText = findViewById(R.id.editTextNumber); // Student ID
        EditText othersText = findViewById(R.id.othersText); // Others text input
        Button confirmQueueButton = findViewById(R.id.button);

        // Initialize Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference registrarCollection = db.collection("registrar_data"); // Firestore collection reference

        // Set OnClickListener for the confirm button
        confirmQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture data from the UI components
                String studentId = studentIdEditText.getText().toString().trim();
                String others = othersText.getText().toString().trim();

                // Validate student ID (must be exactly 10 digits)
                if (!studentId.matches("\\d{10}")) {
                    Toast.makeText(Registrar.this, "Invalid ID number. Please enter your School ID Number.", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if the ID is invalid
                }

                // Store the checkbox states and combine them into one field
                String advising = checkBox1.isChecked() ? "advising" : "";
                String recording = checkBox2.isChecked() ? "recording" : "";
                String archival = checkBox3.isChecked() ? "permanent" : "";
                String distribution = checkBox4.isChecked() ? "distribution" : "";

                // Combine all data into the 'purpose' field
                String purpose = advising + (advising.isEmpty() ? "" : ", ") +
                        recording + (recording.isEmpty() ? "" : ", ") +
                        archival + (archival.isEmpty() ? "" : ", ") +
                        distribution + (distribution.isEmpty() ? "" : ", ") +
                        others;

                // Create a map to hold the data
                Map<String, Object> registrarData = new HashMap<>();
                registrarData.put("student_id", studentId);
                registrarData.put("purpose", purpose); // Store the combined purpose
                registrarData.put("timestamp", FieldValue.serverTimestamp()); // Add timestamp for sorting

                // Save the data to Firestore
                registrarCollection.add(registrarData)
                        .addOnSuccessListener(documentReference -> {
                            // Data saved successfully
                            Toast.makeText(Registrar.this, "Ticket Secured!", Toast.LENGTH_SHORT).show();

                            // After saving, go to Queue Activity
                            Intent intent = new Intent(Registrar.this, Queue.class);
                            startActivity(intent);  // Start Queue Activity
                            finish(); // Close Registrar activity
                        })
                        .addOnFailureListener(e -> {
                            // Error saving data
                            Toast.makeText(Registrar.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }
}
