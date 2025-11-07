package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.Date;

/**
 * Represents a sign-up record for a new user.
 * Stores account creation details and role information.
 */
@IgnoreExtraProperties
public class SignUp {

    @DocumentId
    private String id;              // Firestore document ID
    private String name;            // User’s full name
    private String email;           // Email address
    private String role;            // "entrant", "organizer", or "admin"
    private String deviceId;        // Unique device identifier
    private Date createdAt;         // Account creation timestamp
    private boolean verified;       // Whether account is verified (optional)

    // Required empty constructor for Firestore
    public SignUp() {}

    public SignUp(String name, String email, String role, String deviceId) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.deviceId = deviceId;
        this.createdAt = new Date();
        this.verified = false;
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    // --- Helper Methods ---
    public boolean isOrganizer() {
        return "organizer".equalsIgnoreCase(role);
    }

    public boolean isEntrant() {
        return "entrant".equalsIgnoreCase(role);
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}
