package com.example.chance.view;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance.controller.EventController;
import com.example.chance.controller.LotteryController;
import com.example.chance.model.Event;
import com.example.chance.model.Lottery;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * Organizer view for managing an event.
 * Displays event details, entrants, and allows drawing winners.
 */
public class ManageEventActivity extends AppCompatActivity {

    private TextView tvEventTitle;
    private MaterialButton btnDrawWinners;
    private RecyclerView recyclerViewEntrants;
    private EventController eventController;
    private LotteryController lotteryController;
    private String eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        tvEventTitle = findViewById(R.id.tv_manage_event_title);
        btnDrawWinners = findViewById(R.id.btn_draw_winners);
        recyclerViewEntrants = findViewById(R.id.recycler_manage_event);
        recyclerViewEntrants.setLayoutManager(new LinearLayoutManager(this));

        eventController = new EventController();
        lotteryController = new LotteryController();

        eventId = getIntent().getStringExtra("eventId");
        loadEventDetails();

        btnDrawWinners.setOnClickListener(v -> conductDraw());
    }

    private void loadEventDetails() {
        eventController.getEvent(eventId, event -> {
            tvEventTitle.setText(event.getName());
            Toast.makeText(this, "Loaded event successfully", Toast.LENGTH_SHORT).show();
        }, e -> Toast.makeText(this, "Failed to load event", Toast.LENGTH_SHORT).show());
    }

    private void conductDraw() {
        lotteryController.createLottery(eventId,
                doc -> {
                    String lotteryId = doc.getId();
                    lotteryController.conductDraw(lotteryId, 3,
                            result -> Toast.makeText(this, "Draw completed!", Toast.LENGTH_SHORT).show(),
                            e -> Toast.makeText(this, "Draw failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                },
                e -> Toast.makeText(this, "Failed to start lottery", Toast.LENGTH_SHORT).show());
    }
}
