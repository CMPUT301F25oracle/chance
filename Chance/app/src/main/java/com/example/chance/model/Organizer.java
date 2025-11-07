package com.example.chance.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.List;

/**
 * Organizer model extends Entrant and adds event management features.
 * Organizers can create, edit, and manage events.
 */
@IgnoreExtraProperties
public class Organizer extends User {

    private List<String> createdEvents;   // IDs of events created by this organizer
    private String organizationName;      // optional company or host name
    private String bio;                   // short description or tagline

    // Required empty constructor for Firestore
    public Organizer() {
        super();
    }

    public Organizer(String name, String email, String deviceId, String organizationName) {
        super(name, email, deviceId);
        this.organizationName = organizationName;
    }

    // --- Getters and Setters ---
    public List<String> getCreatedEvents() { return createdEvents; }
    public void setCreatedEvents(List<String> createdEvents) { this.createdEvents = createdEvents; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    @Override
    public String toString() {
        return getUsername() + " (" + organizationName + ")";
    }
}
