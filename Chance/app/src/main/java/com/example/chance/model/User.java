package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    @DocumentId
    private String ID;

    private String username = "";
    private String fullName = "";
    private String email = "";
    private String phoneNumber = "";
    private List<String> joinedEvents = new ArrayList<>();
    private List<String> selectedEvents = new ArrayList<>();
    private List<String> waitingListEvents = new ArrayList<>();
    private String profileImageUrl = "";
    private boolean notificationsEnabled = true;
    private String organizationName = "";
    private List<Map<String, String>> eventHistory = new ArrayList<>();




    // Required empty constructor for Firestore
    public User() {}

    public User(String username) {
        this.username = username;
        this.notificationsEnabled = true;
    }


    /**
     * grabs the username
     * @return
     */
    public String getUsername() { return username; }

    /**
     * sets a new username
     * @param name
     */
    public void setUsername(String name) { this.username = name; }

    /**
     * grabs the users email
     * @return
     */
    public String getEmail() { return email; }

    /**
     * sets a new email
     * @param email
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * grabs the users phone number
     * @return
     */
    public String getPhoneNumber() { return phoneNumber; }

    /**
     * sets a new phone number
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }


    /**
     * gets events the user has joined
     * @return
     */
    public List<String> getJoinedEvents() { return joinedEvents; }

    /**
     * replaces joined events list with a new list
     * @param joinedEvents
     */
    public void setJoinedEvents(List<String> joinedEvents) { this.joinedEvents = joinedEvents; }

    /**
     * gets events the user was selected for
     * @return
     */
    public List<String> getSelectedEvents() { return selectedEvents; }

    /**
     * replaces selected events list with a new list
     * @param selectedEvents
     */
    public void setSelectedEvents(List<String> selectedEvents) { this.selectedEvents = selectedEvents; }

    /**
     * gets events the user is waiting to hear back from
     * @return
     */
    public List<String> getWaitingListEvents() { return waitingListEvents; }

    /**
     * replaces the events the user is waiting to hear back from with a new list
     * @param waitingListEvents
     */
    public void setWaitingListEvents(List<String> waitingListEvents) { this.waitingListEvents = waitingListEvents; }

    /**
     * gets the user profile photo
     * @return
     */
    public String getProfileImageUrl() { return profileImageUrl; }

    /**
     * sets the user profile photo
     * @param profileImageUrl
     */
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    /**
     * sets the users notifcations preference
     * @param notificationsEnabled
     */
    public void setNotificationsEnabled(boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }

    public boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    @Override
    public String toString() {
        return username + " (" + email + ")";
    }

    /**
     * gets name of organization the user is part of
     * @return
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * sets the organization the user is part of
     * @param organizationName
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * gets the users full name
     * @return
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * sets the users full name
     * @param fullName
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<Map<String, String>> getEventHistory() {
        return eventHistory;
    }

    public void setEventHistory(List<Map<String, String>> eventHistory) {
        this.eventHistory = eventHistory;
    }
}
