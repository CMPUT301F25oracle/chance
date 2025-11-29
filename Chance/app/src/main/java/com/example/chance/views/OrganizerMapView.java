package com.example.chance.views;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chance.R;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.model.WaitingListEntry;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class OrganizerMapView extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "OrganizerMapView";

    private MapView mapView;
    private GoogleMap googleMap;
    private String eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_map_view);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        eventId = getIntent().getStringExtra("eventID");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        loadEntrantLocations();
    }

    private void loadEntrantLocations() {
        if (eventId == null) {
            Log.e(TAG, "Event ID is null. Cannot load entrant locations.");
            return;
        }

        DataStoreManager.getInstance().getEvent(eventId, event -> {
            if (event != null) {
                List<WaitingListEntry> waitingList = event.getWaitingList();
                if (waitingList != null && !waitingList.isEmpty()) {
                    for (WaitingListEntry entry : waitingList) {
                        LatLng location = new LatLng(entry.getLatitude(), entry.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(location));
                    }
                    // Move camera to the first entrant's location
                    WaitingListEntry firstEntry = waitingList.get(0);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(firstEntry.getLatitude(), firstEntry.getLongitude()), 10));
                }
            } else {
                Log.e(TAG, "Event not found for ID: " + eventId);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
