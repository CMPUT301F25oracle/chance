package com.example.chance.model;

public class WaitingListEntry {
    private String userId;
    private double latitude;
    private double longitude;

    // Public no-argument constructor is required for Firestore deserialization
    public WaitingListEntry() {}

    public WaitingListEntry(String userId, double latitude, double longitude) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Public getters are required for Firestore serialization
    public String getUserId() {
        return userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
