package com.example.movdev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adminlogin);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get references to UI components
        EditText usernameEditText = findViewById(R.id.editTextText3);
        EditText passwordEditText = findViewById(R.id.editTextText2);
        Button loginButton = findViewById(R.id.button3);

        // Set OnClickListener for the button
        loginButton.setOnClickListener(v -> {
            String enteredUsername = usernameEditText.getText().toString().trim();
            String enteredPassword = passwordEditText.getText().toString().trim();

            // Validate username and password
            if (enteredUsername.equals("admin123") && enteredPassword.equals("gwapoko123")) {
                // Username and password are correct, navigate to MainActivity7
                Intent intent = new Intent(AdminLogin.this, AdminPage1.class); // Navigate to MainActivity7
                startActivity(intent);
                Toast.makeText(AdminLogin.this, "Login Successful!", Toast.LENGTH_SHORT).show();
            } else {
                // Username or password is incorrect, show a toast message
                Toast.makeText(AdminLogin.this, "Incorrect Username or Password! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
