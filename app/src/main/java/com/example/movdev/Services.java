package com.example.movdev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Services extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_services);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back to MainActivity2
        Button backToMain2Button = findViewById(R.id.backtomaintwo);
        backToMain2Button.setOnClickListener(v -> {
            Intent intent = new Intent(Services.this, MainActivity2.class);
            startActivity(intent);
            finish();
        });

        // Go to Registrar
        Button registrarButton = findViewById(R.id.registrarButton);
        registrarButton.setOnClickListener(v -> {
            Intent intent = new Intent(Services.this, Registrar.class);
            startActivity(intent);
        });
        // Go to Treasury
        Button treasuryButton = findViewById(R.id.treasuryButton);
        treasuryButton.setOnClickListener(v -> {
            Intent intent = new Intent(Services.this, Treasury.class);
            startActivity(intent);
        });

        Button admissionButton = findViewById(R.id.admissionButton);
        admissionButton.setOnClickListener(v -> {
            Intent intent = new Intent(Services.this, Admission.class);
            startActivity(intent);
        });

        Button helpdeskButton = findViewById(R.id.helpdeskButton);
        helpdeskButton.setOnClickListener(v -> {
            Intent intent = new Intent(Services.this, Helpdesk.class);
            startActivity(intent);
        });

        Button scholarshipButton = findViewById(R.id.scholarshipButton);
        scholarshipButton.setOnClickListener(v -> {
            Intent intent = new Intent(Services.this, Scholarship.class);
            startActivity(intent);
        });


    }
}
