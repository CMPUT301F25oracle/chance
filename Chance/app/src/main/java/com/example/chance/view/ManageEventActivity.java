package com.example.chance.view;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chance.R;
import com.example.chance.controller.EventController;
import com.example.chance.controller.LotteryController;

/**
 * Organizer view for managing an event.
 * Displays event details, entrants, and allows drawing winners.
 */
public class ManageEventActivity extends AppCompatActivity {

    private EventController eventController;
    private LotteryController lotteryController;
    private String eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Uncomment when manage event XML layout is created with proper IDs
        // setContentView(R.layout.activity_manage_event);

        eventController = new EventController();
        lotteryController = new LotteryController();

        eventId = getIntent().getStringExtra("eventId");

        Toast.makeText(this, "Manage Event - Waiting for UI layout", Toast.LENGTH_SHORT).show();
    }

    /**
     * Load event details when XML is ready.
     */
    private void loadEventDetails() {
        eventController.getEvent(eventId, event -> {
            // TODO: Set event name when TextView is available
            Toast.makeText(this, "Loaded: " + event.getName(), Toast.LENGTH_SHORT).show();
        }, e -> Toast.makeText(this, "Failed to load event", Toast.LENGTH_SHORT).show());
    }

    /**
     * Conduct lottery draw for this event.
     */
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