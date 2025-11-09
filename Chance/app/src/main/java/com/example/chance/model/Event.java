package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Represents an event in the Event Lottery System.
 * Created by Organizer and stored in Firestore.
 */

@IgnoreExtraProperties
public class Event {

    @DocumentId
    private String ID;          // Firestore document ID

    private String name;        // Event name
    private String location;    // Venue or online link
    private int capacity;       // Max entrants
    private double price;       // Entry fee (0 for free events)
    private String description; // Event details
    private Date startDate;    // Event start date
    private Date endDate;     // Event end date
    private String eventOrganizerName;
    private int maxInvited;   // Max entrants that can be invited


    private List<String> waitingList;

    private List<String> invitationList;


    // Required empty constructor for Firestore
    public Event() {}

    public Event(String name, String location, int capacity, double price, String description, Date startDate, Date endDate, String eventOrganizerName) {
        this.ID = eventOrganizerName + "-" + name;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.price = price;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventOrganizerName = eventOrganizerName;
    }

    // --- Getters and Setters ---
    public String getID() { return ID; }
    public void setID(String eventId) { this.ID = eventId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // --- Utility Methods ---
    @Exclude
    public boolean isFull() {
        return capacity <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Objects.equals(ID, event.ID);
    }

    @Override
    public int hashCode() { return Objects.hash(ID); }

    @Override
    public String toString() {
        return name + " (" + location + ") on ?";
    }


    public String getEventOrganizerName() {
        return eventOrganizerName;
    }

    public void setEventOrganizerName(String eventOrganizerName) {
        this.eventOrganizerName = eventOrganizerName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<String> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(String userId) {
        waitingList.add(userId);
    }

    public void leaveWaitingList(String userId) {
        waitingList.remove(userId);
    }

    public List<String> getInvitationList() {
        return invitationList;
    }

    public void acceptInvitation(String userId) {
        invitationList.add(userId);
    }

    public void rejectInvitation(String userId) {
        waitingList.remove(userId);
    }

    public List<String> viewWaitingListEntrants() {
        return this.getWaitingList();
    }

    public int viewWaitingListEntrantsCount() {
        return this.getWaitingList().size();
    }

    public void pollForInvitation() {
        int i = 0;
        List<String> waitingList = this.getWaitingList();
        while (i < invitationList.size()) {
            int j = (int)Math.random()*(invitationList.size() - 1);
            invitationList.add(waitingList.get(j));
            waitingList.remove(j);
            i++;
        }
    }

}
