package com.example.movdev;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button to navigate to MainActivity
        Button backToStartButton = findViewById(R.id.backtostart);
        backToStartButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Navigate to Services when studentButton is clicked
        Button studentButton = findViewById(R.id.studentButton);
        studentButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, Services.class);
            startActivity(intent);
        });

        // Show Service Selection Dialog on Priority Lane Button Click
        Button priorityLaneButton = findViewById(R.id.priority);
        priorityLaneButton.setOnClickListener(v -> showServiceSelectionDialog());
    }

    private void showServiceSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_service_selection, null);
        builder.setView(dialogView);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupServices);

        builder.setTitle("Choose a Service")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    if (selectedId == -1) {
                        Toast.makeText(MainActivity2.this, "Please select a service", Toast.LENGTH_SHORT).show();
                    } else {
                        RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
                        String selectedService = selectedRadioButton.getText().toString();
                        showCounterDialog(selectedService);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCounterDialog(String selectedService) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Counter Information")
                .setMessage("Go to Counter 03 at " + selectedService)
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                    intent.putExtra("selected_service", selectedService);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
