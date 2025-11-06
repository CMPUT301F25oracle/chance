package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a lottery associated with a specific event.
 * Handles entrant selection and waiting list management.
 */
@IgnoreExtraProperties
public class Lottery {

    @DocumentId
    private String id;                // Firestore document ID
    private String eventId;           // ID of the event this lottery belongs to
    private List<String> entrants;    // IDs of entrants who joined
    private List<String> winners;     // IDs of entrants selected
    private List<String> waitingList; // IDs of entrants on waiting list
    private boolean isCompleted;      // Whether the draw has been conducted

    // Required empty constructor for Firestore
    public Lottery() {}

    public Lottery(String eventId) {
        this.eventId = eventId;
        this.entrants = new ArrayList<>();
        this.winners = new ArrayList<>();
        this.waitingList = new ArrayList<>();
        this.isCompleted = false;
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public List<String> getEntrants() { return entrants; }
    public void setEntrants(List<String> entrants) { this.entrants = entrants; }

    public List<String> getWinners() { return winners; }
    public void setWinners(List<String> winners) { this.winners = winners; }

    public List<String> getWaitingList() { return waitingList; }
    public void setWaitingList(List<String> waitingList) { this.waitingList = waitingList; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    // --- Lottery Logic Methods ---
    /**
     * Conducts a random draw for a given number of winners.
     * Marks the lottery as completed.
     */
    public void conductDraw(int numberOfWinners) {
        if (entrants == null || entrants.isEmpty()) return;

        winners = new ArrayList<>();
        waitingList = new ArrayList<>(entrants);
        Random random = new Random();

        int actualWinners = Math.min(numberOfWinners, entrants.size());
        for (int i = 0; i < actualWinners; i++) {
            int randomIndex = random.nextInt(waitingList.size());
            String winner = waitingList.remove(randomIndex);
            winners.add(winner);
        }

        isCompleted = true;
    }

    @Override
    public String toString() {
        return "Lottery for Event ID: " + eventId + " | Winners: " + winners.size();
    }
}
