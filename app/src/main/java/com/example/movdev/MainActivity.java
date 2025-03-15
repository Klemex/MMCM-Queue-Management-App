package com.example.movdev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button navigation to MainActivity2
        Button startButton = findViewById(R.id.start);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        });

        // ImageView navigation to AdminLogin
        ImageView logoImageView = findViewById(R.id.imageView);
        logoImageView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminLogin.class);
            startActivity(intent);
        });

        // Button navigation to RealTimeQueue
        Button realtimeButton = findViewById(R.id.realtime);
        realtimeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RealTimeQueue.class);
            startActivity(intent);
        });
    }
}
