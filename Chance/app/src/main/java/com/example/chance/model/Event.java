package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private String organizerUID;
    private int maxInvited;   // Max entrants that can be invited
    private List<String> waitingList = new ArrayList<>();
    private List<String> invitationList = new ArrayList<>();
    private List<String> acceptedInvite = new ArrayList<>();
    private List<String> declinedInvite = new ArrayList<>();

    // NEW: Store user locations when they join the waiting list
    private Map<String, GeoPoint> waitingListLocations = new HashMap<>();


    // Required empty constructor for Firestore
    public Event() {}

    public Event(String name, String location, int capacity, double price, String description, Date startDate, Date endDate, String organizerUID, int maxInvited) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.price = price;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organizerUID = organizerUID;
        this.maxInvited = maxInvited;
        this.waitingList = new ArrayList<>();
        this.invitationList = new ArrayList<>();
        this.waitingListLocations = new HashMap<>();
    }

    @Override
    public String toString() {
        return name + " (" + location + ") on ?";
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

    public String getOrganizerUID() {
        return organizerUID;
    }

    public void setOrganizerUID(String organizerUID) {
        this.organizerUID = organizerUID;
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

    public void setWaitingList(ArrayList<String> waitingList) {
        this.waitingList = waitingList;
    }

    public List<String> getInvitationList() {
        return invitationList;
    }

    public void setInvitationList(List<String> invitationList) {
        this.invitationList = invitationList;
    }

    public int getMaxInvited() {
        return maxInvited;
    }

    public void setMaxInvited(int maxInvited) {
        this.maxInvited = maxInvited;
    }

    public List<String> getAcceptedInvite() {
        return acceptedInvite;
    }

    public void setAcceptedInvite(List<String> acceptedInvite) {
        this.acceptedInvite = acceptedInvite;
    }

    public List<String> getDeclinedInvite() {
        return declinedInvite;
    }

    public void setDeclinedInvite(List<String> declinedInvite) {
        this.declinedInvite = declinedInvite;
    }

    // NEW: Getter and Setter for waitingListLocations
    public Map<String, GeoPoint> getWaitingListLocations() {
        return waitingListLocations;
    }

    public void setWaitingListLocations(Map<String, GeoPoint> waitingListLocations) {
        this.waitingListLocations = waitingListLocations;
    }

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
    public int hashCode() {
        return Objects.hash(ID);
    }

    // MODIFIED: Now accepts latitude and longitude to store user location
    public void addToWaitingList(String userId, double latitude, double longitude) {
        if (!waitingList.contains(userId) && waitingList.size() < maxInvited) {
            waitingList.add(userId);
            // Store the location as GeoPoint
            waitingListLocations.put(userId, new GeoPoint(latitude, longitude));
        } else {
            System.out.println("Max capacity reached for this event");
        }
    }

    // BACKWARD COMPATIBILITY: Keep old method that doesn't require location
    // This uses default location (0, 0) if no location is provided
    public void addToWaitingList(String userId) {
        addToWaitingList(userId, 0.0, 0.0);
    }

    // MODIFIED: Remove location data when user leaves waiting list
    public void leaveWaitingList(String userId) {
        waitingList.remove(userId);
        waitingListLocations.remove(userId);  // Remove location data too
    }

    public void acceptInvitation(String userId) {
        acceptedInvite.add(userId);
        invitationList.remove(userId);
    }

    public void declineInvitation(String userId) {
        declinedInvite.add(userId);
        invitationList.remove(userId);
    }

    public List<String> viewWaitingListEntrants() {
        return this.getWaitingList();
    }

    public int viewWaitingListEntrantsCount() {
        return this.getWaitingList().size();
    }

    public void pollForInvitation() {
        int i = 0;
        Collections.shuffle(waitingList);
        while (i < this.getMaxInvited() && i < this.getCapacity() && !waitingList.isEmpty()) {
            invitationList.add(waitingList.removeLast());
            i++;
        }
    }
}


/**
 * ==================== Event.java Comments ====================
 *
 * This file defines the Event class, which serves as the data model for an event
 * within the application. It encapsulates all properties and behaviors associated with an event,
 * such as its details, capacity, and participant lists. This class is designed to be directly
 * used with Firebase Firestore for data persistence.
 *
 * === Event Class ===
 * Represents a single event created by an Organizer. It includes information about the
 * event's name, location, capacity, price, description, and dates. It also manages
 * different lists of participants: those on the waiting list, those invited, those who
 * have accepted invitations, and those who have declined.
 *
 * It has recently been updated to include location tracking for users who join the
 * waiting list, storing their geographical coordinates as a GeoPoint.
 *
 * --- @DocumentId ---
 * The 'ID' field is automatically populated by Firestore with the document's ID.
 *
 * --- @IgnoreExtraProperties ---
 * Allows Firestore to ignore any extra fields in the database that are not present in this class.
 *
 * === Constructors ===
 * - A required empty public constructor for Firestore deserialization.
 * - A parameterized constructor for creating new Event instances with initial values.
 *
 * === Getters and Setters ===
 * Standard accessor and mutator methods are provided for all properties of the event,
 * allowing other parts of the application to interact with the event data.
 * This includes the new 'waitingListLocations' map.
 *
 * === Utility Methods ===
 * - isFull(): A convenience method to check if the event has reached its capacity (capacity <= 0).
 * - equals() & hashCode(): Overridden to define event equality based on the unique event ID.
 * - toString(): Provides a simple string representation of the event.
 *
 * === Core Logic Methods ===
 * - addToWaitingList(String userId, double latitude, double longitude):
 *   Adds a user to the event's waiting list and records their geographical location.
 *   Checks against the maximum number of invited participants.
 *
 * - addToWaitingList(String userId):
 *   A backward-compatible version of the method that adds a user to the waiting list
 *   without location data, using a default (0,0) coordinate.
 *
 * - leaveWaitingList(String userId):
 *   Removes a user from the event's waiting list and also removes their associated
 *   location data to maintain data consistency.
 *
 * - acceptInvitation(String userId):
 *   Moves a user from the 'invitationList' to the 'acceptedInvite' list.
 *
 * - declineInvitation(String userId):
 *   Moves a user from the 'invitationList' to the 'declinedInvite' list.
 *
 * - viewWaitingListEntrants() & viewWaitingListEntrantsCount():
 *   Provide read-only access to the waiting list and its size.
 *
 * - pollForInvitation():
 *   Implements the lottery logic. It shuffles the waiting list and moves a number
 *   of users (up to 'maxInvited' and 'capacity') to the 'invitationList'.
 */