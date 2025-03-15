package com.example.movdev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminPage1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adminpage1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button transactionHistoryButton = findViewById(R.id.studentButton8);
        transactionHistoryButton.setOnClickListener(v -> {
                // Create an intent to go to MainActivity8
                Intent intent = new Intent(AdminPage1.this, History.class);

                // You can pass data if needed
                // intent.putExtra("KEY", value);

                // Start MainActivity8
                startActivity(intent);
        });

        Button counterButton = findViewById(R.id.counterButton);
        counterButton.setOnClickListener(v -> {
            // Create an intent to go to MainActivity8
            Intent intent = new Intent(AdminPage1.this, Counter.class);

            // You can pass data if needed
            // intent.putExtra("KEY", value);

            // Start MainActivity8
            startActivity(intent);
        });
    }
}