package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.Date;
import java.util.Objects;

/**
 * Represents an event in the Event Lottery System.
 * Created by Organizer and stored in Firestore.
 */

@IgnoreExtraProperties
public class Event {

    @DocumentId
    private String id;          // Firestore document ID

    private String name;        // Event name
    private String location;    // Venue or online link
    private int capacity;       // Max entrants
    private double price;       // Entry fee (0 for free events)
    private String description; // Event details
    private Date date;          // Event date
    private Date startDate;    // Event start date
    private Date endDate;     // Event end date
    private String eventOrganizerName;

    // Required empty constructor for Firestore
    public Event() {}

    public Event(String name, String location, int capacity, double price, String description, Date startDate, Date endDate, String eventOrganizerName) {
        this.id = eventOrganizerName + "-" + name;
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
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return name + " (" + location + ") on " + date;
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
}
