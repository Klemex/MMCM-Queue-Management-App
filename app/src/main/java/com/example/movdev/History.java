package com.example.movdev;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class History extends AppCompatActivity {

    private FirebaseFirestore db;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button backButton = findViewById(R.id.backtomaintwo2);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(History.this, AdminPage1.class);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();
        tableLayout = findViewById(R.id.tableLayout);

        loadHistoryData();
    }




    private void loadHistoryData() {
        db.collection("registrar_data")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int rowCount = 0;
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String studentId = document.getString("student_id");
                        String ticketNumber = document.getString("ticket_number");
                        String purpose = document.getString("purpose");

                        Timestamp timestamp = document.getTimestamp("timestamp");
                        String dateTime = (timestamp != null) ? formatTimestamp(timestamp) : "N/A";

                        addRowToTable(studentId, ticketNumber, purpose, dateTime);
                        rowCount++;
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(History.this, "Error loading history", Toast.LENGTH_SHORT).show());
    }


    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(10, 20, 10, 20);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.table_cell_border);
        textView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

        // Enable text wrapping
        textView.setSingleLine(false);
        textView.setMaxLines(3);  // Allow up to 3 lines
        textView.setEllipsize(null);

        // Set text alignment
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        return textView;
    }

    private void addRowToTable(String studentId, String ticketNumber, String purpose, String dateTime) {
        TableRow row = new TableRow(this);
        row.setClickable(true);
        row.setPadding(0, 10, 0, 10);

        TextView idTextView = createTextView(studentId);
        TextView ticketTextView = createTextView(ticketNumber);
        TextView purposeTextView = createTextView(purpose);
        TextView dateTextView = createTextView(dateTime);

        // Set layout weights to match header
        TableRow.LayoutParams idParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow.LayoutParams ticketParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow.LayoutParams purposeParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);
        TableRow.LayoutParams dateParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

        idTextView.setLayoutParams(idParams);
        ticketTextView.setLayoutParams(ticketParams);
        purposeTextView.setLayoutParams(purposeParams);
        dateTextView.setLayoutParams(dateParams);

        // Add views to row in the correct order (matching header)
        row.addView(idTextView);          // School ID
        row.addView(ticketTextView);      // Ticket Number
        row.addView(purposeTextView);     // Purpose (wider column)
        row.addView(dateTextView);        // Date and Time

        row.setOnClickListener(v -> showTicketDialog(History.this, studentId, ticketNumber, purpose, dateTime));

        tableLayout.addView(row);
    }

    private void adjustRowHeight(TableRow row) {
        int maxHeight = 0;
        for (int i = 0; i < row.getChildCount(); i++) {
            TextView textView = (TextView) row.getChildAt(i);
            textView.measure(0, 0);
            int height = textView.getMeasuredHeight();
            if (height > maxHeight) {
                maxHeight = height;
            }
        }
        for (int i = 0; i < row.getChildCount(); i++) {
            TextView textView = (TextView) row.getChildAt(i);
            textView.setHeight(maxHeight);
            textView.setGravity(Gravity.CENTER);
        }
    }

    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }

    private void showTicketDialog(Context context, String studentId, String ticketNumber, String purpose, String dateTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.setGravity(Gravity.CENTER);

        layout.addView(createDialogTextView("School ID: " + studentId));
        layout.addView(createDialogTextView("Ticket Number: " + ticketNumber));
        layout.addView(createDialogTextView("Purpose: " + purpose));
        layout.addView(createDialogTextView("Date & Time: " + dateTime));
        layout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        builder.setView(layout);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private TextView createDialogTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(20, 20, 20, 20);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        return textView;
    }

    private void wrapTableInScrollView() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(tableLayout);
        setContentView(scrollView);
    }
}
