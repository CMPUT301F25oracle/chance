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
/**
 * ==================== WaitingListMapActivity.java Comments ====================
 *
 * This file defines the WaitingListMapActivity class, which is responsible for
 * displaying a Google Map with markers indicating the geographical locations of
 * users who have signed up for an event's waiting list (lottery).
 *
 * === WaitingListMapActivity Class ===
 * An AppCompatActivity that implements the OnMapReadyCallback to interact with
 * the Google Map. It receives an event ID from the launching intent and uses it
 * to fetch the corresponding event data, including the locations of users on the
 * waiting list.
 *
 * --- EXTRA_EVENT_ID Constant ---
 * A public constant string used as the key for passing the event ID via an Intent extra.
 *
 * --- onCreate Method ---
 * Initializes the activity, sets the content view, and retrieves the event ID
 * from the intent. If the event ID is missing, it logs an error and closes the
 * activity. It then asynchronously initializes the SupportMapFragment.
 *
 * --- onMapReady Method ---
 * This callback method is invoked when the Google Map is fully loaded and ready
 * to be used. It configures basic UI settings like zoom controls and then
 * triggers the process to load the event data and display the markers.
 *
 * --- loadEventAndDisplayMarkers Method ---
 * Fetches the specific Event object from Firestore using the DataStoreManager.
 * Upon successfully retrieving the event, it calls the displayUserMarkers method
 * to render the locations on the map.
 *
 * --- displayUserMarkers Method ---
 * This is the core logic method. It retrieves the map of user locations from the
 * Event object. It then iterates through this map, and for each user:
 * 1. It checks if the location is valid, skipping the default (0,0) coordinates
 *    which indicate that a user signed up without providing a location.
 * 2. For each valid location, it creates a Google Maps LatLng object and adds a
 *    marker to the map.
 * 3. It uses a LatLngBounds.Builder to calculate the geographic bounds that
 *    encompass all the added markers.
 * 4. Finally, if any markers were added, it animates the map's camera to zoom
 *    and pan perfectly to frame all the markers on the screen with a bit of padding.
 * 5. It includes comprehensive logging and Toast messages for debugging and to
 *    inform the user if no locations are available to display.
 */



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