package com.example.chance.controller;

import com.example.chance.model.Event;
import com.example.chance.model.User;
import com.example.chance.model.WaitingList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class DataStoreManager {
    private static DataStoreManager instance;

    private final FirebaseManager db;

    private DataStoreManager() {
        db = FirebaseManager.getInstance();
    }

    public static DataStoreManager getInstance() {
        if (instance == null) {
            instance = new DataStoreManager();
        }
        return instance;
    }

    /**
     * Create a new user in Firestore.
     * @param username
     * @param password
     * @return
     */
    public User createUser(String username, String password) {
        User new_user = new User(username, password, "");
        db.setDocument("users", new_user.getUsername(), new_user, (s)->{}, (s)->{});
        return new_user;
    }
    // TODO: make WAYYYYY more secure.

    /**
     * Get a user from Firestore.
     * @param username
     * @param onSuccess
     */
    public void getUser(String username, OnSuccessListener<User> onSuccess) {
        if (username.isEmpty()) {
            onSuccess.onSuccess(null);
            return;
        }
        db.getDocument("users", username, (doc) -> {
            if (doc.exists()) {
                User user = doc.toObject(User.class);
                onSuccess.onSuccess(user);
            } else {
                onSuccess.onSuccess(null);
            }
        }, (e)->{});
    }

    public void updateUser(String username, User updatedUser, OnSuccessListener<Void> onSuccess) {
        db.setDocument("users", username, updatedUser, onSuccess, (e)->{});
    }

    public void deleteUser(String username, OnSuccessListener<Void> onSuccess) {
        db.deleteDocument("users", username, (___na) -> {
            onSuccess.onSuccess(null);
        }, (e)->{
            throw new RuntimeException();
        });
    }


    public void joinWaitingList(Event event, String entrantId, OnSuccessListener<Void> onSuccess) {
        event.setWaitingList(entrantId);
        db.setDocument("events", event.getId(), event, onSuccess, (e)->{});
    }

    public void leaveWaitingList(Event event, String entrantId, OnSuccessListener<Void> onSuccess) {
        event.leaveWaitingList(entrantId);
        db.setDocument("events", event.getId(), event, onSuccess, (e)->{});
    }


    /**
     * Create a new event in Firestore.
     * @param name
     * @param location
     * @param capacity
     * @param price
     * @param description
     * @param startDate
     * @param endDate
     * @param organizerUserName
     * @return
     */
    public Event createEvent(String name, String location, int capacity, double price, String description, Date startDate, Date endDate, String organizerUserName) {
        Event new_event = new Event(name, location, capacity, price, description, startDate, endDate, organizerUserName);
        db.setDocument("events", new_event.getId(), new_event, (s)->{}, (s)->{});
        return new_event;
    }

    /**
     * Get an event from Firestore.
     * @param id
     * @param onSuccess
     */
    public void getEvent(String id, OnSuccessListener<Event> onSuccess) {
        db.getDocument("events", id, (doc) -> {
            if (doc.exists()) {
                Event event = doc.toObject(Event.class);
                onSuccess.onSuccess(event);
            } else {
                onSuccess.onSuccess(null);
            }
        }, (e)->{});
    }

    /**
     * Get all events from Firestore.
     * @param onSuccess
     */
    public void getAllEvents(OnSuccessListener<List<Event>> onSuccess) {
        db.getCollection("events", (snapshot) -> {
            List<Event> events = snapshot.toObjects(Event.class);
            onSuccess.onSuccess(events);
        }, (e)->{});
    }
}
