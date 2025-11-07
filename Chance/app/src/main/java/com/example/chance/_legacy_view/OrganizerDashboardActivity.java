package com.example.chance.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance._legacy_adapter.OrganizerDashboardAdapter;
import com.example.chance.controller.EventController;
import com.example.chance.model.Event;

import java.util.List;

public class OrganizerDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventController eventController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_dashboard);

        recyclerView = findViewById(R.id.recycler_dashboard_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventController = new EventController();
        loadOrganizerEvents();
    }

    private void loadOrganizerEvents() {
        eventController.getAllEvents(this::setupRecycler, e ->
                Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show());
    }

    private void setupRecycler(List<Event> events) {
        OrganizerDashboardAdapter adapter = new OrganizerDashboardAdapter(this, events, new OrganizerDashboardAdapter.OnOrganizerEventClickListener() {
            @Override
            public void onManageClicked(Event event) {
                Intent intent = new Intent(OrganizerDashboardActivity.this, ManageEventActivity.class);
                intent.putExtra("eventId", event.getId());
                startActivity(intent);
            }

            @Override
            public void onCreateClicked(Event event) {
                Intent intent = new Intent(OrganizerDashboardActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
