
package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.Date;

/**
 * Represents an event invitation sent from an Organizer to an Entrant.
 * Used for private or early access events, or lottery bypass invitations.
 */
@IgnoreExtraProperties
public class Invitation {

    @DocumentId
    private String id;          // Firestore document ID
    private String eventId;     // Target event
    private String senderId;    // Organizer ID
    private String receiverId;  // Entrant ID
    private Date sentAt;        // Time invitation was sent
    private boolean accepted;   // Whether entrant accepted
    private boolean declined;   // Whether entrant declined

    // Required empty constructor for Firestore
    public Invitation() {}

    public Invitation(String eventId, String senderId, String receiverId) {
        this.eventId = eventId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.sentAt = new Date();
        this.accepted = false;
        this.declined = false;
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public Date getSentAt() { return sentAt; }
    public void setSentAt(Date sentAt) { this.sentAt = sentAt; }

    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }

    public boolean isDeclined() { return declined; }
    public void setDeclined(boolean declined) { this.declined = declined; }

    // --- Helper Methods ---
    public void accept() {
        this.accepted = true;
        this.declined = false;
    }

    public void decline() {
        this.declined = true;
        this.accepted = false;
    }

    public boolean isPending() {
        return !accepted && !declined;
    }

    @Override
    public String toString() {
        return "Invitation to Event " + eventId + " from " + senderId + " → " + receiverId;
    }
}

