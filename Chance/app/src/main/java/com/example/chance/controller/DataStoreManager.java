package com.example.chance.controller;

import androidx.annotation.NonNull;

import com.example.chance.model.Event;
import com.example.chance.model.EventImage;
import com.example.chance.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;


public class DataStoreManager {
    private static final String PSEUDO_EMAIL = "@authentication.chance";
    private static DataStoreManager instance;
    private static FirebaseAuth fAuth;
    private final FirebaseManager db;

    private DataStoreManager() {
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseManager.getInstance();
    }

    /**
     * gets the datastore instance
     * @return
     */
    public static DataStoreManager getInstance() {
        if (instance == null) {
            instance = new DataStoreManager();
        }
        return instance;
    }

    public Boolean isDeviceAuthenticated() {
        FirebaseUser user = fAuth.getCurrentUser();
        return user != null;
    }

    public void AuthenticateUser(String username, String password) {

    }

    public void createNewUser(String username, String password, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        // source: https://firebase.google.com/docs/auth/android/password-auth
        fAuth.createUserWithEmailAndPassword(username + PSEUDO_EMAIL, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            onSuccess.onSuccess(null);
                        } else {
                            onFailure.onFailure(task.getException());
                        }
                    }
                });
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

    /**
     * updates the user in firebase
     * @param username
     * @param updatedUser
     * @param onSuccess
     */
    public void updateUser(String username, User updatedUser, OnSuccessListener<Void> onSuccess) {
        db.setDocument("users", username, updatedUser, onSuccess, (e)->{});
    }

    /**
     * deletes the user from firebase
     * @param username
     * @param onSuccess
     */
    public void deleteUser(String username, OnSuccessListener<Void> onSuccess) {
        db.deleteDocument("users", username, (___na) -> {
            onSuccess.onSuccess(null);
        }, (e)->{
            throw new RuntimeException();
        });
    }


    /**
     * joins the user into the waiting list
     * @param event
     * @param entrantId
     * @param onSuccess
     */
    public void joinWaitingList(Event event, String entrantId, OnSuccessListener<Void> onSuccess) {
        event.addToWaitingList(entrantId);
        db.setDocument("events", event.getID(), event, onSuccess, (e)->{});
    }

    /**
     * removes the user from the waiting list
     * @param event
     * @param entrantId
     * @param onSuccess
     */
    public void leaveWaitingList(Event event, String entrantId, OnSuccessListener<Void> onSuccess) {
        event.leaveWaitingList(entrantId);
        db.setDocument("events", event.getID(), event, onSuccess, (e)->{});
    }

    /**
     * accepts the invitation for the specified event
     * @param event
     * @param entrantId
     * @param onSuccess
     */
    public void acceptInvitation(Event event, String entrantId, OnSuccessListener<Void> onSuccess) {
        event.acceptInvitation(entrantId);
        db.setDocument("events", event.getID(), event, onSuccess, (e)->{});
    }

    /**
     * rejects the invitation for the specified event
     * @param event
     * @param entrantId
     * @param onSuccess
     */
    public void rejectInvitation(Event event, String entrantId, OnSuccessListener<Void> onSuccess) {
        event.rejectInvitation(entrantId);
        db.setDocument("events", event.getID(), event, onSuccess, (e)->{});
    }

    /**
     * uploads an event poster
     * @param event_id
     * @param image
     * @param onSuccess
     */
    public void uploadEventImage(String event_id, Base64 image, OnSuccessListener<Void> onSuccess) {
        EventImage eventImage = new EventImage(event_id, image);
        db.setDocument("event_images", event_id, eventImage, onSuccess, (e)->{});
    }

    /**
     * grabs an events associated poster
     * @param event_id
     * @param onSuccess
     */
    public void browseEventImage(String event_id, OnSuccessListener<Base64> onSuccess) {
        if (event_id.isEmpty()) {
            onSuccess.onSuccess(null);
            return;
        } else {
            db.getDocument("event_images", event_id, (doc) -> {
                if (doc.exists()) {
                    EventImage eventImage = doc.toObject(EventImage.class);
                    onSuccess.onSuccess(eventImage.getEventImage());
                } else {
                    onSuccess.onSuccess(null);
                }
            }, (e) -> {
            });
        }
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
        db.setDocument("events", new_event.getID(), new_event, (s)->{}, (s)->{});
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
