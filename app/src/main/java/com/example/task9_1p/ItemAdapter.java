package com.example.task9_1p;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter class for handling RecyclerView items
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<DatabaseHelper.ItemDescription> itemList;
    private OnItemClickListener onItemClickListener;

    // Custom interface for item click events
    public interface OnItemClickListener {
        void onItemClick(int itemId);
    }

    // Constructor that takes a list of items
    public ItemAdapter(List<DatabaseHelper.ItemDescription> itemList) {
        this.itemList = itemList;
    }

    // Create a new ViewHolder instance for a RecyclerView item
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view, parent, false); // Inflate the layout for an item
        return new ViewHolder(view); // Return the new ViewHolder
    }

    // Bind data to the ViewHolder for each item in the list
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatabaseHelper.ItemDescription item = itemList.get(position); // Get the item at the given position

        // Ensure TextView and idTextView are initialized
        if (holder.textDescription == null || holder.idTextView == null) {
            throw new NullPointerException("TextView or idTextView is null"); // Handle initialization errors
        }

        // Set the description and ID in the ViewHolder
        holder.textDescription.setText(item.getDescription()); // Set the item description
        holder.idTextView.setText(String.valueOf(item.getId())); // Set the item ID

        // Set the click event for the item view
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                int itemId = Integer.parseInt(holder.idTextView.getText().toString()); // Convert text to int
                onItemClickListener.onItemClick(itemId); // Call the custom click listener with the item ID
            }
        });
    }

    // Get the total number of items in the list
    @Override
    public int getItemCount() {
        return itemList.size(); // Return the number of items
    }

    // Set the custom item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Custom ViewHolder class to represent an item in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDescription;
        TextView idTextView; // Hidden TextView for item ID

        // Constructor for the ViewHolder
        public ViewHolder(View itemView) {
            super(itemView);
            textDescription = itemView.findViewById(R.id.textDescription);
            idTextView = itemView.findViewById(R.id.idTextView);
        }
    }
}
