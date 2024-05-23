package com.example.task9_1p;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        List<DatabaseHelper.ItemDescription> itemList = dbHelper.getAllItems(); // Get all items from the database

        ItemAdapter adapter = new ItemAdapter(itemList); // Create an adapter with the list of items

        // Set an item click listener
        adapter.setOnItemClickListener(itemId -> {
            // Create an intent to start DetailActivity
            Intent intent = new Intent(ListActivity.this, DetailActivity.class);
            intent.putExtra("item_id", itemId); // Pass the item ID to the detail activity
            startActivity(intent); // Start the detail activity
        });

        recyclerView.setAdapter(adapter); // Set the adapter for the RecyclerView
    }
}
