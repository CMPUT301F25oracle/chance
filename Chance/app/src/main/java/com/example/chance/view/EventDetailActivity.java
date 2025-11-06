package com.example.chance.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chance.R;
import com.example.chance.controller.EventController;
import com.example.chance.model.Event;
import com.google.android.material.button.MaterialButton;

/**
 * Displays full details of an event and allows entrants to join.
 */
public class EventDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvLocation, tvDescription, tvDate, tvPrice, tvCapacity;
    private MaterialButton btnJoin;
    private EventController eventController;
    private String eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        tvTitle = findViewById(R.id.tv_event_title);
        tvLocation = findViewById(R.id.tv_event_location);
        tvDescription = findViewById(R.id.tv_event_description);
        tvDate = findViewById(R.id.tv_event_date);
        tvPrice = findViewById(R.id.tv_event_price);
        tvCapacity = findViewById(R.id.tv_event_capacity);
        btnJoin = findViewById(R.id.btn_join_event);

        eventController = new EventController();
        eventId = getIntent().getStringExtra("eventId");

        loadEventDetails();
    }

    private void loadEventDetails() {
        eventController.getEvent(eventId, event -> {
            if (event != null) displayEvent(event);
        }, e -> Toast.makeText(this, "Failed to load event", Toast.LENGTH_SHORT).show());
    }

    private void displayEvent(Event event) {
        tvTitle.setText(event.getName());
        tvLocation.setText(event.getLocation());
        tvDescription.setText(event.getDescription());
        tvDate.setText(event.getDate() != null ? event.getDate().toString() : "");
        tvPrice.setText(event.getPrice() == 0 ? "Free" : "$" + event.getPrice());
        tvCapacity.setText("Capacity: " + event.getCapacity());

        btnJoin.setOnClickListener(v ->
                Toast.makeText(this, "Joined " + event.getName(), Toast.LENGTH_SHORT).show());
    }
}
