package com.example.task9_1p;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    private TextView postTypeValue;
    private TextView nameValue;
    private TextView phoneValue;
    private TextView descriptionValue;
    private TextView dateValue;
    private TextView locationValue;
    private TextView idTextView;
    private Button removeButton;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity); // Set the layout for the activity

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize the views
        idTextView = findViewById(R.id.idTextView);
        postTypeValue = findViewById(R.id.postTypeValue);
        nameValue = findViewById(R.id.nameValue);
        phoneValue = findViewById(R.id.phoneValue);
        descriptionValue = findViewById(R.id.descriptionValue);
        dateValue = findViewById(R.id.dateValue);
        locationValue = findViewById(R.id.locationValue);
        removeButton = findViewById(R.id.removebutton);

        // Get the item_id from the intent
        Intent intent = getIntent();
        int itemId = intent.getIntExtra("item_id", -1); // Default to -1 if not found

        // If item_id is invalid, show a message and exit
        if (itemId == -1) {
            Toast.makeText(this, "Invalid item ID", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
            return;
        }

        // Store the item_id in a hidden TextView
        idTextView.setText(String.valueOf(itemId));

        // Get the item from the database
        LostAndFoundItem item = dbHelper.getItemById(itemId);

        if (item == null) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the item is not found
            return;
        }

        // Display the item details
        postTypeValue.setText(item.getPostType());
        nameValue.setText(item.getName());
        phoneValue.setText(item.getPhone());
        descriptionValue.setText(item.getDescription());
        dateValue.setText(item.getDate());
        locationValue.setText(item.getLocation());

        // Set the click event for the remove button
        removeButton.setOnClickListener(v -> {
            // Get the item ID from the hidden TextView and delete the item from the database
            int idToDelete = Integer.parseInt(idTextView.getText().toString());
            dbHelper.deleteItem(idToDelete);

            Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();

            // After deletion, go back to the main activity
            Intent backToMain = new Intent(this, MainActivity.class);
            startActivity(backToMain); // Start the MainActivity
            finish(); // Close the current activity
        });
    }
}
