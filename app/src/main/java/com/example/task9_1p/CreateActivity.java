package com.example.task9_1p;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CreateActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioLost;
    private RadioButton radioFound;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText descriptionEditText;
    private EditText locationEditText;
    private DatePicker datePicker;
    private Button saveButton;
    private Button getLocationButton;

    private DatabaseHelper dbHelper;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    // Request codes and permissions
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000;

    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;

    // ActivityResultLauncher for launching autocomplete and location search activities
    private final ActivityResultLauncher<Intent> autocompleteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    String address = place.getAddress();
                    locationEditText.setText(address);
                }
            });


    private final ActivityResultLauncher<Intent> locationSearchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    String address = place.getAddress();
                    locationEditText.setText(address);
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);

        dbHelper = new DatabaseHelper(this);

        radioGroup = findViewById(R.id.radioGroup);
        radioLost = findViewById(R.id.radio_lost);
        radioFound = findViewById(R.id.radio_found);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        datePicker = findViewById(R.id.datePicker);
        saveButton = findViewById(R.id.savebutton);
        getLocationButton = findViewById(R.id.getLocationButton);

        if (radioGroup == null || radioLost == null || radioFound == null || nameEditText == null ||
                phoneEditText == null || descriptionEditText == null || locationEditText == null || datePicker == null || saveButton == null) {
            Toast.makeText(this, "Initialization error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        saveButton.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedButton = findViewById(checkedId);

            if (selectedButton == null) {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
                return;
            }

            String postType = selectedButton.getText().toString();
            String name = nameEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String location = locationEditText.getText().toString();

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();

            String formattedDate = year + "-" + month + "-" + day;

            dbHelper.insertItem(postType, name, phone, description, formattedDate, location);

            Toast.makeText(CreateActivity.this, "Item saved", Toast.LENGTH_SHORT).show();
            clearFields();
        });

        // Set up the click listener for location search
        locationEditText.setOnClickListener(v -> {
            // Define the fields to be returned by the autocomplete search
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

            // Create an autocomplete intent builder
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(CreateActivity.this);

            // Start the autocomplete activity using ActivityResultLauncher
            autocompleteLauncher.launch(intent);
        });

        getLocationButton.setOnClickListener(v -> {
            // Check if location permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request location permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                // If permission is granted, get current location
                getLocation();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Places.initialize(getApplicationContext(), "AIzaSyArriuVKfmbYW523xEMqK7ydrxgANg9Rvg");
        placesClient = Places.createClient(this);

        // Set focusable false for locationEditText to prevent manual input
        locationEditText.setFocusable(false);
        locationEditText.setOnClickListener(v -> {
            // Define the fields to be returned by the autocomplete search
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(CreateActivity.this);
            // Start the autocomplete activity using ActivityResultLauncher
            locationSearchLauncher.launch(intent);
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                Log.d("Location", "Location: " + location.toString());
                // If location is not null, get latitude and longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Geocoder geocoder = new Geocoder(CreateActivity.this, Locale.getDefault());
                try {
                    Log.d("Location", "Attempting to get address...");
                    // Attempt to get address from coordinates
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String addressFragments = address.getMaxAddressLineIndex() >= 0 ? address.getAddressLine(0) : "";
                        Log.d("Location", "Address found: " + address.toString());
                        runOnUiThread(() -> {
                            locationEditText.setText(addressFragments);
                        });
                        Log.d("Location", "Address set to editTextLocation:" + locationEditText.getText().toString());
                    } else {
                        Toast.makeText(CreateActivity.this, "No address found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException ioException) {
                    Toast.makeText(CreateActivity.this, "Service not available", Toast.LENGTH_SHORT).show();
                } catch (IllegalArgumentException illegalArgumentException) {
                    Toast.makeText(CreateActivity.this, "Invalid lat long used", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CreateActivity.this, "Location not detected", Toast.LENGTH_SHORT).show();
                Log.d("Location", "Location not detected");
            }
        });
    }

    // Method to handle location permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to clear input fields
    private void clearFields() {
        nameEditText.setText("");
        phoneEditText.setText("");
        descriptionEditText.setText("");
        locationEditText.setText("");
        radioGroup.clearCheck();
    }
}