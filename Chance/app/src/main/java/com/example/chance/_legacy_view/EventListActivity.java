package com.example.chance.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance._legacy_adapter.EventListAdapter;
import com.example.chance.controller.EventController;
import com.example.chance.model.Event;

import java.util.List;

/**
 * Displays a list of all available events to entrants.
 */
public class EventListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventController eventController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        recyclerView = findViewById(R.id.recycler_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventController = new EventController();

        loadEvents();
    }

    private void loadEvents() {
        eventController.getAllEvents(this::setupRecycler, e ->
                Toast.makeText(this, "Failed to load events: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setupRecycler(List<Event> events) {
        EventListAdapter adapter = new EventListAdapter(this, events, event -> {
            Intent intent = new Intent(EventListActivity.this, EventDetailActivity.class);
            intent.putExtra("eventId", event.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
}
