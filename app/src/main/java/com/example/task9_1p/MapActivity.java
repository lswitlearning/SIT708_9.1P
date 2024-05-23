package com.example.task9_1p;

import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize the DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        // Get all items from the database
        List<DatabaseHelper.ItemDescription> itemList = dbHelper.getAllItems();

        if (!itemList.isEmpty()) {
            for (DatabaseHelper.ItemDescription itemDesc : itemList) {
                int id = itemDesc.getId();
                LostAndFoundItem item = dbHelper.getItemById(id);
                if (item != null) {
                    String locationString = item.getLocation();
                    Log.d("MapActivity", "Item location string: " + locationString);
                    LatLng location = getLocationFromAddress(locationString);

                    if (location != null) {
                        Log.d("MapActivity", "Adding marker at location: " + location.toString());
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(location)
                                .title(item.getName())
                                .snippet(item.getDescription()));
                        marker.setTag(item);
                    } else {
                        Log.d("MapActivity", "Invalid location: " + locationString);
                    }
                } else {
                    Log.d("MapActivity", "Item not found with ID: " + id);
                }
            }

            // Move camera to the location of the first item
            LostAndFoundItem firstItem = dbHelper.getItemById(itemList.get(0).getId());
            LatLng firstLocation = getLocationFromAddress(firstItem.getLocation());
            if (firstLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10.0f));
            }

            mMap.setOnMarkerClickListener(this);
        } else {
            Log.d("MapActivity", "No items found in the database");
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LostAndFoundItem item = (LostAndFoundItem) marker.getTag();
        if (item != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(item.getName());
            builder.setMessage("Description: " + item.getDescription() +
                    "\nPhone: " + item.getPhone() +
                    "\nDate: " + item.getDate() +
                    "\nLocation: " + item.getLocation());
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(this, "No details available", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private LatLng getLocationFromAddress(String addressString) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(addressString, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            } else {
                Log.e("MapActivity", "No addresses found for: " + addressString);
            }
        } catch (IOException e) {
            Log.e("MapActivity", "Geocoder exception: ", e);
        }
        return null;
    }
}
