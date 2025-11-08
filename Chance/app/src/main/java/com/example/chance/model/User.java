package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.List;

@IgnoreExtraProperties
public class User {

    @DocumentId
    private String username;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;
    private String deviceId;
    private List<String> joinedEvents;
    private List<String> selectedEvents;
    private List<String> waitingListEvents;
    private String profileImageUrl;
    private boolean notificationsEnabled;
    private String organizationName;

    // Required empty constructor for Firestore
    public User() {}

    public User(String name, String password, String deviceId) {
        this.username = name;
        this.password = password;
        this.deviceId = deviceId;
        this.notificationsEnabled = true;
    }

    // --- Getters and Setters ---

    public String getUsername() { return username; }
    public void setUsername(String name) { this.username = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public List<String> getJoinedEvents() { return joinedEvents; }
    public void setJoinedEvents(List<String> joinedEvents) { this.joinedEvents = joinedEvents; }

    public List<String> getSelectedEvents() { return selectedEvents; }
    public void setSelectedEvents(List<String> selectedEvents) { this.selectedEvents = selectedEvents; }

    public List<String> getWaitingListEvents() { return waitingListEvents; }
    public void setWaitingListEvents(List<String> waitingListEvents) { this.waitingListEvents = waitingListEvents; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }

    @Override
    public String toString() {
        return username + " (" + email + ")";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
