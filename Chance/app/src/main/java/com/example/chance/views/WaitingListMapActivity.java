package com.example.chance.views;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chance.R;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.model.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;
import java.util.Map;
import android.widget.Toast;

public class WaitingListMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "WaitingListMap";
    public static final String EXTRA_EVENT_ID = "event_id";

    private GoogleMap mMap;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_list_map);

        // Get event ID from intent
        eventId = getIntent().getStringExtra(EXTRA_EVENT_ID);
        if (eventId == null) {
            Log.e(TAG, "No event ID provided");
            finish();
            return;
        }


        // Obtain the SupportMapFragment and get notified when the map is ready
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        // Load event and display markers
        loadEventAndDisplayMarkers();
    }

    private void loadEventAndDisplayMarkers() {
        DataStoreManager.getInstance().getEvent(eventId, event -> {
            if (event != null) {
                displayUserMarkers(event);
            }
        });
    }

    private void displayUserMarkers(Event event) {
        Map<String, GeoPoint> locations = event.getWaitingListLocations();

        Log.d(TAG, "Event ID: " + event.getID());
        Log.d(TAG, "Event name: " + event.getName());
        Log.d(TAG, "Waiting list size: " + event.getWaitingList().size());
        Log.d(TAG, "Locations map size: " + (locations != null ? locations.size() : "null"));

        if (locations == null || locations.isEmpty()) {
            Log.i(TAG, "No user locations to display");
            Toast.makeText(this, "No locations found in waiting list", Toast.LENGTH_LONG).show();
            return;
        }

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        int markerCount = 0;

        // Add a marker for each user in the waiting list
        for (Map.Entry<String, GeoPoint> entry : locations.entrySet()) {
            String userId = entry.getKey();
            GeoPoint geoPoint = entry.getValue();

            Log.d(TAG, "User: " + userId + " -> Lat: " + geoPoint.getLatitude() + ", Lng: " + geoPoint.getLongitude());

            // Skip invalid locations (default 0,0)
            if (geoPoint.getLatitude() == 0.0 && geoPoint.getLongitude() == 0.0) {
                Log.w(TAG, "Skipping user " + userId + " - location is (0,0)");
                continue;
            }

            LatLng position = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

            // Add marker
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title("User: " + userId));

            boundsBuilder.include(position);
            markerCount++;
            Log.d(TAG, "Added marker for user: " + userId);
        }

        Log.d(TAG, "Total markers added: " + markerCount);

        // Adjust camera to show all markers
        if (markerCount > 0) {
            LatLngBounds bounds = boundsBuilder.build();
            int padding = 100; // padding in pixels
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        } else {
            Toast.makeText(this, "All users have invalid locations (0,0)", Toast.LENGTH_LONG).show();
            // Set a default camera position
            LatLng defaultPosition = new LatLng(0, 0);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, 2));
        }
    }
}

//    private void displayUserMarkers(Event event) {
//        Map<String, GeoPoint> locations = event.getWaitingListLocations();
//
//        if (locations == null || locations.isEmpty()) {
//            Log.i(TAG, "No user locations to display");
//            return;
//        }
//
//        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
//        int markerCount = 0;
//
//        // Add a marker for each user in the waiting list
//        for (Map.Entry<String, GeoPoint> entry : locations.entrySet()) {
//            String userId = entry.getKey();
//            GeoPoint geoPoint = entry.getValue();
//
//            // Skip invalid locations (default 0,0)
//            if (geoPoint.getLatitude() == 0.0 && geoPoint.getLongitude() == 0.0) {
//                continue;
//            }
//
//            LatLng position = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
//
//            // Add marker
//            mMap.addMarker(new MarkerOptions()
//                    .position(position)
//                    .title("User: " + userId));
//
//            boundsBuilder.include(position);
//            markerCount++;
//        }
//
//        // Adjust camera to show all markers
//        if (markerCount > 0) {
//            LatLngBounds bounds = boundsBuilder.build();
//            int padding = 100; // padding in pixels
//            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
//        }
//    }
//}