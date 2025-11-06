package com.example.chance.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance.adapter.EventListAdapter;
import com.example.chance.controller.EventController;
import com.example.chance.model.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays all events to the user (Entrant view).
 * Allows searching, filtering, and QR code scanning.
 */
public class EventListActivity extends AppCompatActivity {

    private EventListAdapter adapter;
    private final List<Event> all = new ArrayList<>();
    private EventController eventController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        RecyclerView rv = findViewById(R.id.recyclerEvents);
        TextInputEditText search = findViewById(R.id.searchInput);
        FloatingActionButton fab = findViewById(R.id.fabScan);

        // --- RecyclerView setup ---
        adapter = new EventListAdapter(event -> {
            Intent i = new Intent(this, EventDetailActivity.class);
            i.putExtra("eventId", event.getId());
            startActivity(i);
        });
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // --- Fetch events from Firestore ---
        eventController = new EventController();
        eventController.getAllEvents(events -> {
            all.clear();
            all.addAll(events);
            adapter.submitList(all);
        }, error -> {
            Log.e("EventListActivity", "Error loading events", error);
        });

        // --- Search bar logic ---
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s == null ? "" : s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // --- QR Scanner button ---
        fab.setOnClickListener(v -> startActivity(new Intent(this, QRScannerActivity.class)));
    }

    // --- Filter events by search query ---
    private void filter(String q) {
        if (q.isEmpty()) {
            adapter.submitList(all);
            return;
        }
        List<Event> filtered = new ArrayList<>();
        String lower = q.toLowerCase();
        for (Event e : all) {
            if ((e.getName() != null && e.getName().toLowerCase().contains(lower)) ||
                    (e.getLocation() != null && e.getLocation().toLowerCase().contains(lower))) {
                filtered.add(e);
            }
        }
        adapter.submitList(filtered);
    }
}
