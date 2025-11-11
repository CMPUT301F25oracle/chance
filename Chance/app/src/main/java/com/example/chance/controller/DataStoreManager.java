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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;


public class DataStoreManager {
    // firestore requires emails to create new user accounts, so we simply
    // use our own pseudo domain to get around this; specifically its
    // preferred we use emails to login over usernames
    private static DataStoreManager instance;
    private static FirebaseAuth fAuth;
    private static FirebaseFirestore fStore;
    private final FirebaseManager db;

    private final String PSEUDO_EMAIL = "@authentication.chance";
    private final String USER_COLLECTION = "users";
    private final String EVENT_COLLECTION = "events";
    private final String EVENT_IMAGE_COLLECTION = "event_image";

    private DataStoreManager() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
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

    public void getAuthenticatedUser(OnSuccessListener<User> onSuccess, OnFailureListener onFailure) {
        FirebaseUser user = fAuth.getCurrentUser();
        if (user != null) {
            getUserFromUID(user.getUid(), onSuccess, onFailure);
        } else {
            onFailure.onFailure(null);
        }
    }

    public void authenticateUser(String username, String password, OnSuccessListener<User> onSuccess, OnFailureListener onFailure) {
        fAuth.signInWithEmailAndPassword(username + PSEUDO_EMAIL, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firestoreUser = fAuth.getCurrentUser();
                            getUserFromUID(firestoreUser.getUid(), onSuccess, onFailure);
                            // now we grab the users information from firestore
                            onSuccess.onSuccess(null);
                        } else {
                            onFailure.onFailure(task.getException());
                        }
                    }
                });
    }

    public void createNewUser(String username, String password, OnSuccessListener<User> onSuccess, OnFailureListener onFailure) {
        // source: https://firebase.google.com/docs/auth/android/password-auth
        fAuth.createUserWithEmailAndPassword(username + PSEUDO_EMAIL, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firestoreUser = fAuth.getCurrentUser();
                            User user = new User(username);
                            // now that the user is in firestore, we create their db entry
                            assert firestoreUser != null;
                            fStore.collection(USER_COLLECTION)
                                .document(firestoreUser.getUid())
                                .set(user)
                                .addOnSuccessListener((__) -> {
                                    onSuccess.onSuccess(user);
                                })
                                .addOnFailureListener((e) -> {
                                    onFailure.onFailure(e);
                                });
                        } else {
                            onFailure.onFailure(task.getException());
                        }
                    }
                });
    }



    public void getUserFromUID(String uid, OnSuccessListener<User> onSuccess, OnFailureListener onFailure) {
        db.getDocument("users", uid, (document) -> {
            if (document.exists()) {
                User user = document.toObject(User.class);
                onSuccess.onSuccess(user);
            } else {
                onFailure.onFailure(null);
            }
        }, onFailure);
    }

    public void createNewEvent(Event event, OnSuccessListener<Event> onSuccess, OnFailureListener onFailure) {
        fStore.collection(EVENT_COLLECTION)
                .add(event)
                .addOnSuccessListener(document -> {
                    event.setID(document.getId());
                    onSuccess.onSuccess(event);
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * gets all events from firestore, and returns them through success callback
     * @param onSuccess
     * @param onFailure
     */
    public void getEvents(OnSuccessListener<List<Event>> onSuccess, OnFailureListener onFailure) {
        fStore.collection(EVENT_COLLECTION)
                .get()
                .addOnSuccessListener((snapshot) -> {
                    List<Event> events = snapshot.toObjects(Event.class);
                    onSuccess.onSuccess(events);
                })
                .addOnFailureListener(onFailure);
    }

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

    public __event event(Event target_event) {
        return new __event(target_event);
    }

    public class __event {
        Event event;
        __event(Event event) {
            this.event = event;
        }

//        public void enterLottery() {
//            fStore.collection(EVENT_COLLECTION)
//
//        }
    }
}
