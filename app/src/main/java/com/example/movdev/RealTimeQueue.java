package com.example.movdev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class RealTimeQueue extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference registrarCollection, helpdeskCollection, treasuryCollection, scholarshipCollection, admissionCollection;
    private ListenerRegistration registrarListener, helpdeskListener, treasuryListener, scholarshipListener, admissionListener;

    private TextView ticket1, counter1;
    private TextView ticket2, counter2;
    private TextView ticket3, counter3;
    private TextView ticket4, counter4;
    private TextView ticket5, counter5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_realtimequeue);


        // Find the ImageView by ID
        ImageView imageView = findViewById(R.id.imageView11);

        // Set OnClickListener to navigate to Counter.java
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(RealTimeQueue.this, Counter.class);
            startActivity(intent);
        });
        // Corrected ID usage
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.realtimequeue), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;



        });

        db = FirebaseFirestore.getInstance();
        registrarCollection = db.collection("registrar_data");
        helpdeskCollection = db.collection("helpdesk_data");
        treasuryCollection = db.collection("treasury_data");
        scholarshipCollection = db.collection("scholarship_data");
        admissionCollection = db.collection("admission_data");

        ticket1 = findViewById(R.id.ticket1);
        counter1 = findViewById(R.id.counter1);

        ticket2 = findViewById(R.id.ticket2);
        counter2 = findViewById(R.id.counter2);

        ticket3 = findViewById(R.id.ticket3);
        counter3 = findViewById(R.id.counter3);

        ticket4 = findViewById(R.id.ticket4);
        counter4 = findViewById(R.id.counter4);

        ticket5 = findViewById(R.id.ticket5);
        counter5 = findViewById(R.id.counter5);

        // Initialize default values
        resetToDefaultValues();

        // Check for updates from Counter activity
        Intent intent = getIntent();
        if (intent != null) {
            String prefix = intent.getStringExtra("prefix");
            String ticketNumber = intent.getStringExtra("ticketNumber");
            String counterNumber = intent.getStringExtra("counterNumber");

            // Log the received values for debugging
            System.out.println("Received Intent Extras:");
            System.out.println("Prefix: " + prefix);
            System.out.println("Ticket Number: " + ticketNumber);
            System.out.println("Counter Number: " + counterNumber);

            // Check if all extras are present and not null
            if (prefix != null && ticketNumber != null && counterNumber != null) {
                updateUI(prefix, ticketNumber, counterNumber);
            } else {
                // Log or handle the case where extras are missing
                Toast.makeText(this, "No valid data received from Counter", Toast.LENGTH_SHORT).show();
            }
        }

        setupFirestoreListeners();
    }

    private void resetToDefaultValues() {
        // Set default values for tickets and counters
        ticket1.setText("R000");
        counter1.setText("Counter0");

        ticket2.setText("T000");
        counter2.setText("Counter0");

        ticket3.setText("H000");
        counter3.setText("Counter0");

        ticket4.setText("A000");
        counter4.setText("Counter0");

        ticket5.setText("S000");
        counter5.setText("Counter0");
    }

    private void updateUI(String prefix, String ticketNumber, String counterNumber) {
        switch (prefix) {
            case "R":
                ticket1.setText(ticketNumber);
                counter1.setText(counterNumber);
                break;
            case "T":
                ticket2.setText(ticketNumber);
                counter2.setText(counterNumber);
                break;
            case "H":
                ticket3.setText(ticketNumber);
                counter3.setText(counterNumber);
                break;
            case "A":
                ticket4.setText(ticketNumber);
                counter4.setText(counterNumber);
                break;
            case "S":
                ticket5.setText(ticketNumber);
                counter5.setText(counterNumber);
                break;
        }
    }

    private void setupFirestoreListeners() {
        registrarListener = registrarCollection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(1)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot doc : value) {
                            updateUIFromFirestore(doc, ticket1, counter1);
                        }
                    }
                });

        helpdeskListener = helpdeskCollection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(1)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot doc : value) {
                            updateUIFromFirestore(doc, ticket3, counter3);
                        }
                    }
                });

        treasuryListener = treasuryCollection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(1)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot doc : value) {
                            updateUIFromFirestore(doc, ticket2, counter2);
                        }
                    }
                });

        scholarshipListener = scholarshipCollection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(1)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot doc : value) {
                            updateUIFromFirestore(doc, ticket5, counter5);
                        }
                    }
                });

        admissionListener = admissionCollection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(1)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot doc : value) {
                            updateUIFromFirestore(doc, ticket4, counter4);
                        }
                    }
                });
    }

    private void updateUIFromFirestore(QueryDocumentSnapshot doc, TextView ticket, TextView counter) {
        String ticketNumber = doc.getString("ticket_number");
        String counterNumber = doc.getString("counter_number");

        if (ticketNumber != null && !ticketNumber.isEmpty()) {
            ticket.setText(ticketNumber);
        }
        if (counterNumber != null && !counterNumber.isEmpty()) {
            counter.setText(counterNumber);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registrarListener != null) registrarListener.remove();
        if (helpdeskListener != null) helpdeskListener.remove();
        if (treasuryListener != null) treasuryListener.remove();
        if (scholarshipListener != null) scholarshipListener.remove();
        if (admissionListener != null) admissionListener.remove();
    }
}

