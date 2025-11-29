package com.example.chance.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.model.Event;
import com.example.chance.model.EventImage;
import com.example.chance.model.Notification;
import com.example.chance.model.User;
import com.example.chance.model.WaitingListEntry;
import com.example.chance.util.Tuple3;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Observable;

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
    private final String NOTIFICATION_COLLECTION = "notifications";
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

    public void logoutAuthenticatedUser() {
        fAuth.signOut();
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
                            DocumentReference userCollectionRef = fStore.collection(USER_COLLECTION).document(firestoreUser.getUid());
                            userCollectionRef
                                    .set(user)
                                    .addOnSuccessListener((__) -> {
                                        onSuccess.onSuccess(user);
                                    })
                                    .addOnFailureListener((e) -> {
                                        onFailure.onFailure(e);
                                    });
                            userCollectionRef
                                .collection(NOTIFICATION_COLLECTION)
                                .add(new Notification(-1, null, null));
                        } else {
                            onFailure.onFailure(task.getException());
                        }
                    }
                });
    }

    public void getAllUsers(OnSuccessListener<List<User>> onSuccess, OnFailureListener onFailure) {
        fStore.collection(USER_COLLECTION)
                .get()
                .addOnSuccessListener((snapshot) -> {
                    List<User> users = snapshot.toObjects(User.class);
                    onSuccess.onSuccess(users);
                })
                .addOnFailureListener(onFailure);
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

    public Observable<Tuple3<Event, DocumentChange.Type, Void>> observeEventsCollection() {
        return Observable.create(emitter -> {
            fStore.collection(EVENT_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        for (DocumentChange documentChange : snapshots.getDocumentChanges()) {
                            DocumentSnapshot document = documentChange.getDocument();
                            try {
                                Event event = document.toObject(Event.class);
                                emitter.onNext(new Tuple3(event, documentChange.getType(), null));
                            } catch (Exception e) {
                                Log.e("DataStoreManager", "Error converting event", e);
                            }

                        }

                    }

                });
        });
    }


    public void getEventBannerFromID(String ID, OnSuccessListener<Bitmap> onSuccess, OnFailureListener onFailure) {
        fStore.collection(EVENT_IMAGE_COLLECTION)
                .document(ID)
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        onFailure.onFailure(new Exception("Event image not found for ID: " + ID));
                        return;
                    }

                    EventImage eventImage = document.toObject(EventImage.class);
                    byte[] imageBase64 = Base64.decode(eventImage.getEventImage(), Base64.DEFAULT);
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBase64, 0, imageBase64.length);
                    onSuccess.onSuccess(imageBitmap);

                })
                .addOnFailureListener(onFailure);
    }

    public void deleteEventBanner(String eventId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        fStore.collection(EVENT_IMAGE_COLLECTION)
                .document(eventId)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getEvents(OnSuccessListener<List<Event>> onSuccess, OnFailureListener onFailure) {
        fStore.collection(EVENT_COLLECTION)
                .get()
                .addOnSuccessListener((snapshot) -> {
                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        try {
                            Event event = document.toObject(Event.class);
                            events.add(event);
                        } catch (Exception e) {
                            Log.e("DataStoreManager", "Error converting event", e);
                        }
                    }
                    onSuccess.onSuccess(events);
                })
                .addOnFailureListener(onFailure);
    }

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

    public void updateUser(User updatedUser, OnSuccessListener<Void> onSuccess) {
        db.setDocument("users", updatedUser.getID(), updatedUser, onSuccess, (e)->{});
    }

    public void deleteUser(String username, OnSuccessListener<Void> onSuccess) {
        db.deleteDocument("users", username, (___na) -> {
            onSuccess.onSuccess(null);
        }, (e)->{
            throw new RuntimeException();
        });
    }


    public void joinWaitingList(Event event, String entrantId, double latitude, double longitude, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        WaitingListEntry newEntry = new WaitingListEntry(entrantId, latitude, longitude);
        fStore.collection(EVENT_COLLECTION).document(event.getID())
                .update("waitingList", FieldValue.arrayUnion(newEntry))
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void leaveWaitingList(Event event, String entrantId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        getEvent(event.getID(), updatedEvent -> {
            if (updatedEvent != null) {
                WaitingListEntry entryToRemove = null;
                for (WaitingListEntry entry : updatedEvent.getWaitingList()) {
                    if (entry.getUserId().equals(entrantId)) {
                        entryToRemove = entry;
                        break;
                    }
                }
                if (entryToRemove != null) {
                    fStore.collection(EVENT_COLLECTION)
                            .document(event.getID())
                            .update("waitingList", FieldValue.arrayRemove(entryToRemove))
                            .addOnSuccessListener(onSuccess)
                            .addOnFailureListener(onFailure);
                }
            }
        });
    }

    public void acceptInvitation(Event event, String entrantId, OnSuccessListener<Void> onSuccess) {
        event.acceptInvitation(entrantId);
        db.setDocument("events", event.getID(), event, onSuccess, (e)->{});
    }

    public void rejectInvitation(Event event, String entrantId, OnSuccessListener<Void> onSuccess) {
        event.rejectInvitation(entrantId);
        db.setDocument("events", event.getID(), event, onSuccess, (e)->{});
    }

    public Event createEvent(String name, String location, int capacity, double price, String description, Date startDate, Date endDate, String organizerUserName) {
        Event new_event = new Event(name, location, capacity, price, description, startDate, endDate, organizerUserName, 0);
        db.setDocument("events", new_event.getID(), new_event, (s)->{}, (s)->{});
        return new_event;
    }

    public void getEvent(String id, OnSuccessListener<Event> onSuccess) {
        db.getDocument("events", id, (doc) -> {
            if (doc.exists()) {
                try {
                    Event event = doc.toObject(Event.class);
                    onSuccess.onSuccess(event);
                } catch (Exception e) {
                    onSuccess.onSuccess(null);
                    Log.e("DataStoreManager", "Error converting event", e);
                }
            } else {
                onSuccess.onSuccess(null);
            }
        }, (e)->{});
    }

    public void getAllEvents(OnSuccessListener<List<Event>> onSuccess) {
        db.getCollection("events", (snapshot) -> {
            List<Event> events = new ArrayList<>();
            for (DocumentSnapshot document : snapshot.getDocuments()) {
                try {
                    Event event = document.toObject(Event.class);
                    events.add(event);
                } catch (Exception e) {
                    Log.e("DataStoreManager", "Error converting event", e);
                }
            }
            onSuccess.onSuccess(events);
        }, (e)->{});
    }

    public void getAllEventImages(OnSuccessListener<List<EventImage>> onSuccess, OnFailureListener onFailure) {
        fStore.collection(EVENT_IMAGE_COLLECTION)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<EventImage> images = snapshot.toObjects(EventImage.class);
                    onSuccess.onSuccess(images);
                })
                .addOnFailureListener(onFailure);
    }

    public void removeEvent(Event event, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.deleteDocument("events", event.getID(), onSuccess, onFailure);
    }

    public __user user(User target_user) {
        return new __user(target_user);
    }


    public class __user {
        User user;
        __user(User user) {
            this.user = user;
        }

        public void getNotifications(OnSuccessListener<List<Notification>> onSuccess, OnFailureListener onFailure) {
            fStore.collection(USER_COLLECTION)
                .document(user.getID())
                .collection(NOTIFICATION_COLLECTION)
                .get()
                .addOnSuccessListener((snapshot) -> {
                    List<Notification> notifications = snapshot.toObjects(Notification.class)
                        .stream()
                        .filter(notification -> notification.getType() != -1)
                        .collect(Collectors.toList());
                    onSuccess.onSuccess(notifications);
                })
                .addOnFailureListener(onFailure);
        }

        public Observable<Tuple3<Notification, DocumentChange.Type, Void>> observeNotifications() {
            return Observable.create(emitter -> {
                fStore.collection(USER_COLLECTION)
                    .document(user.getID())
                    .collection(NOTIFICATION_COLLECTION)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                return;
                            }
                            for (DocumentChange documentChange : snapshots.getDocumentChanges()) {
                                DocumentSnapshot document = documentChange.getDocument();
                                Notification notification = document.toObject(Notification.class);
                                emitter.onNext(new Tuple3(notification, documentChange.getType(), null));
                            }
                        }
                    });
            });
        }

        public void postNotification(Notification notification, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
            fStore.collection(USER_COLLECTION)
                .document(user.getID())
                .collection(NOTIFICATION_COLLECTION)
                    .add(notification)
                    .addOnSuccessListener(document -> {
                        //notification.setID(document.getId());
                        onSuccess.onSuccess(null);
                    })
                    .addOnFailureListener(onFailure);
        }
    }

    public __event event(Event target_event) {
        return new __event(target_event);
    }

    public class __event {
        Event event;
        __event(Event event) {
            this.event = event;
        }

        public void getUsersInLottery(OnSuccessListener<List<WaitingListEntry>> onSuccess, OnFailureListener onFailure) {
            fStore.collection(EVENT_COLLECTION)
                    .document(event.getID())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        Event event = snapshot.toObject(Event.class);
                        onSuccess.onSuccess(event.getWaitingList());
                    })
                    .addOnFailureListener(onFailure);
        }

        public void checkUserInLottery(User user, OnSuccessListener<Boolean> onSuccess) {
            getUsersInLottery(users -> {
                boolean isInLottery = users.stream().anyMatch(u -> u.getUserId().equals(user.getID()));
                onSuccess.onSuccess(isInLottery);
            }, e->{});
        }

        public void enterLottery(User user, double latitude, double longitude) {
            WaitingListEntry newEntry = new WaitingListEntry(user.getID(), latitude, longitude);
            fStore.collection(EVENT_COLLECTION)
                    .document(event.getID())
                    .update("waitingList", FieldValue.arrayUnion(newEntry));
        }

        public void leaveLottery(User user) {
            // This is tricky because we need the full WaitingListEntry object to remove it.
            // For now, we will fetch the event, find the entry, and then remove it.
            getEvent(event.getID(), updatedEvent -> {
                if (updatedEvent != null) {
                    WaitingListEntry entryToRemove = null;
                    for (WaitingListEntry entry : updatedEvent.getWaitingList()) {
                        if (entry.getUserId().equals(user.getID())) {
                            entryToRemove = entry;
                            break;
                        }
                    }
                    if (entryToRemove != null) {
                        fStore.collection(EVENT_COLLECTION)
                                .document(event.getID())
                                .update("waitingList", FieldValue.arrayRemove(entryToRemove));
                        event.leaveWaitingList(user.getID());
                    }
                }
            });
        }

        public void drawEntrants() {
            Log.d("DataStoreManager", "drawEntrants called for event " + event.getID() + " at " + System.currentTimeMillis());
            List<String> newInvitations = event.pollForInvitation();

            db.setDocument(EVENT_COLLECTION, event.getID(), event, unused -> {
                Log.d("DataStoreManager", "Event successfully updated after polling.");
            }, e -> {
                Log.e("DataStoreManager", "Error updating event after polling.", e);
            });

            if (newInvitations.isEmpty()) {
                return;
            }

            Map<String, String> meta = new HashMap<>();
            meta.put("eventID", event.getID());
            meta.put("title", event.getName());
            meta.put("description", event.getDescription());
            Notification inviteNotification = new Notification();
            inviteNotification.setMeta(meta);
            inviteNotification.setType(0);
            inviteNotification.setCreationDate(new Date());
            for (String invitedUserId : newInvitations) {
                fStore.collection(USER_COLLECTION)
                    .document(invitedUserId)
                    .collection(NOTIFICATION_COLLECTION)
                    .add(inviteNotification);
            }
        }


    }

    public __eventImage eventImage(EventImage target_event_image) {
        return new __eventImage(target_event_image);
    }

    public class __eventImage {
        EventImage eventImage;
        __eventImage(EventImage eventImage) {
            this.eventImage = eventImage;
        }

        public void save(OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
            fStore.collection(EVENT_IMAGE_COLLECTION)
                    .document(eventImage.getID())
                    .set(eventImage)
                    .addOnSuccessListener(onSuccess)
                    .addOnFailureListener(onFailure);
        }

    }
}
