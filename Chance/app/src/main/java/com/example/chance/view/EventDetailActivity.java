package com.example.chance.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chance.R;
import com.example.chance.controller.EventController;
import com.example.chance.model.Event;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;

/**
 * Displays the details of a single event when the user selects one from the list.
 */
public class EventDetailActivity extends AppCompatActivity {

    private final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private EventController controller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        String eventId = getIntent().getStringExtra("eventId");

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvMeta = findViewById(R.id.tvMeta);
        TextView tvDescription = findViewById(R.id.tvDescription);
        MaterialButton btnJoin = findViewById(R.id.btnJoin);

        controller = new EventController();

        if (eventId != null) {
            controller.getEvent(eventId, event -> {
                if (event == null) return;

                tvTitle.setText(event.getName());
                String meta = (event.getLocation() == null ? "" : event.getLocation())
                        + (event.getDate() == null ? "" : " • " + df.format(event.getDate()));
                tvMeta.setText(meta);
                tvDescription.setText(event.getDescription() == null ? "" : event.getDescription());
            }, e -> {});
        }

        btnJoin.setOnClickListener(v -> {
            // TODO: Implement "join event" logic here later with LotteryController
        });
    }
}
