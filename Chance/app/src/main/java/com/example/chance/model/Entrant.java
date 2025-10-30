package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.List;

@IgnoreExtraProperties
public class Entrant {

    @DocumentId
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String deviceId;
    private List<String> joinedEvents;
    private List<String> selectedEvents;
    private List<String> waitingListEvents;
    private String profileImageUrl;
    private boolean notificationsEnabled;

    // Required empty constructor for Firestore
    public Entrant() {}

    public Entrant(String name, String email, String deviceId) {
        this.name = name;
        this.email = email;
        this.deviceId = deviceId;
        this.notificationsEnabled = true;
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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
        return name + " (" + email + ")";
    }
}
