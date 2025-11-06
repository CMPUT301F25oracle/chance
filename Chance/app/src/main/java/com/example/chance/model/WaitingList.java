package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a waiting list for an event after the lottery draw.
 * Entrants here are next in line if winners withdraw or capacity increases.
 */
@IgnoreExtraProperties
public class WaitingList {

    @DocumentId
    private String id;               // Firestore document ID
    private String eventId;          // Event this waiting list belongs to
    private List<String> entrantIds; // Entrants currently on the waiting list

    // Required empty constructor for Firestore
    public WaitingList() {}

    public WaitingList(String eventId) {
        this.eventId = eventId;
        this.entrantIds = new ArrayList<>();
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public List<String> getEntrantIds() { return entrantIds; }
    public void setEntrantIds(List<String> entrantIds) { this.entrantIds = entrantIds; }

    // --- Logic Methods ---
    public void addEntrant(String entrantId) {
        if (!entrantIds.contains(entrantId)) {
            entrantIds.add(entrantId);
        }
    }

    public void removeEntrant(String entrantId) {
        entrantIds.remove(entrantId);
    }

    public boolean isEmpty() {
        return entrantIds == null || entrantIds.isEmpty();
    }

    public String getNextEntrant() {
        if (!isEmpty()) {
            return entrantIds.get(0);
        }
        return null;
    }

    @Override
    public String toString() {
        return "WaitingList for Event ID: " + eventId + " (" + entrantIds.size() + " entrants)";
    }
}
