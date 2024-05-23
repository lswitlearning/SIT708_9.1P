package com.example.task9_1p;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enables edge-to-edge mode to provide an immersive experience
        EdgeToEdge.enable(this);

        // Sets the layout for this activity
        setContentView(R.layout.activity_main);

        // Adjusts the padding of the main view to accommodate system bars (like the status bar and navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                }
        );

        // Find the buttons in the layout
        Button createButton = findViewById(R.id.createbutton);
        Button showButton = findViewById(R.id.showbutton);
        Button showMapButton = findViewById(R.id.showMapButton); // Add this line to find the "Show ON MAP" button

        // Set an OnClickListener for the "Create" button to start CreateActivity
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start CreateActivity
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivity(intent); // Start the CreateActivity
            }
        });

        // Set an OnClickListener for the "Show" button to start ListActivity
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start ListActivity
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent); // Start the ListActivity
            }
        });
        // Set an OnClickListener for the "Show ON MAP" button to start MapActivity
        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start MapActivity
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                // Add any necessary data (such as location records) to the intent before starting the activity
                startActivity(intent); // Start the MapActivity
            }
        });
    }
}
