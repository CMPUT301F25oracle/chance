package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.List;

/**
 * Administrator model represents a system-level user
 * responsible for moderating events, users, and reported content.
 */
@IgnoreExtraProperties
public class Administrator {

    @DocumentId
    private String id;               // Firestore document ID
    private String name;             // Admin name
    private String email;            // Admin login or contact
    private List<String> managedEvents; // IDs of events flagged or under review
    private List<String> flaggedUsers;  // IDs of users flagged for issues

    // Required empty constructor for Firestore
    public Administrator() {}

    public Administrator(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getManagedEvents() { return managedEvents; }
    public void setManagedEvents(List<String> managedEvents) { this.managedEvents = managedEvents; }

    public List<String> getFlaggedUsers() { return flaggedUsers; }
    public void setFlaggedUsers(List<String> flaggedUsers) { this.flaggedUsers = flaggedUsers; }

    @Override
    public String toString() {
        return "Administrator: " + name + " (" + email + ")";
    }
}
