package com.example.chance.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.model.Event;
import com.example.chance.model.EventImage;
import com.example.chance.model.Notification;
import com.example.chance.model.User;
import com.example.chance.util.RxFirebase;
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
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;


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

    public void getAllEventBanners(OnSuccessListener<List<EventImage>> onSuccess, OnFailureListener onFailure) {
        fStore.collection(EVENT_IMAGE_COLLECTION)
            .get()
            .addOnSuccessListener(snapshot -> {
                List<EventImage> eventImages = snapshot.toObjects(EventImage.class);
                onSuccess.onSuccess(eventImages);
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
                                Event event = document.toObject(Event.class);
                                emitter.onNext(new Tuple3(event, documentChange.getType(), null));
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
                    byte[] imageBase64 = Base64.getDecoder().decode(eventImage.getEventImage());
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBase64, 0, imageBase64.length);
                    onSuccess.onSuccess(imageBitmap);
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * deletes the event banner associated with the event ID (from event_image collection)
     * @param eventId The ID of the event, which is also the document ID of the banner image.
     * @param onSuccess Listener for successful deletion.
     * @param onFailure Listener for failed deletion.
     */
    public void deleteEventBanner(String eventId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        fStore.collection(EVENT_IMAGE_COLLECTION)
                .document(eventId)
                .delete()
                .addOnSuccessListener(onSuccess)
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
     * @param updatedUser
     * @param onSuccess
     */
    public void updateUser(User updatedUser, OnSuccessListener<Void> onSuccess) {
        db.setDocument("users", updatedUser.getID(), updatedUser, onSuccess, (e)->{});
    }

    /**
     * deletes the user from firebase
     * @param userId
     * @param onSuccess
     */
    public void deleteUser(String userId, OnSuccessListener<Void> onSuccess) {
        db.deleteDocument("users", userId, (___na) -> {
            onSuccess.onSuccess(null);
        }, (e)->{
            throw new RuntimeException(e);
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
        event.declineInvitation(entrantId);
        db.setDocument("events", event.getID(), event, onSuccess, (e)->{});
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
        Event new_event = new Event(name, location, capacity, price, description, startDate, endDate, organizerUserName, 0);
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

    public void removeEvent(Event event, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.deleteDocument("events", event.getID(), onSuccess, onFailure);
    }

    public DataStoreUser user(User target_user) {
        return new DataStoreUser(target_user);
    }


    public class DataStoreUser {
        User user;
        DataStoreUser(User user) {
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

        public void deleteAllNotifications(OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
            fStore.collection(USER_COLLECTION)
                    .document(user.getID())
                    .collection(NOTIFICATION_COLLECTION)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            doc.getReference().delete();
                        }
                        onSuccess.onSuccess(null);
                    })
                    .addOnFailureListener(onFailure);
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

        public void getUsersInLottery(OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
            fStore.collection(EVENT_COLLECTION)
                .document(event.getID())
                .get()
                .addOnSuccessListener(snapshot -> {
                    Event event = snapshot.toObject(Event.class);
                    List<String> waitingListUsers = event.getWaitingList();
                    onSuccess.onSuccess(waitingListUsers);
                })
                .addOnFailureListener(onFailure);
        }

        public void checkUserInLottery(User user, OnSuccessListener<Boolean> onSuccess) {
            getUsersInLottery(users -> {
                boolean isInLottery = users.contains(user.getID());
                onSuccess.onSuccess(isInLottery);
            }, e -> {
            });
        }

        public void acceptedInvite(User user) {
            String userID = user.getID();
            DocumentReference eventDocument = fStore.collection(EVENT_COLLECTION)
                .document(event.getID());
            eventDocument.update("acceptedInvite", FieldValue.arrayUnion(userID));
            eventDocument.update("invitationList", FieldValue.arrayRemove(userID));
            event.acceptInvitation(userID);
        }

        public void declinedInvite(User user) {
            String userID = user.getID();
            DocumentReference eventDocument = fStore.collection(EVENT_COLLECTION)
                .document(event.getID());
            eventDocument.update("declinedInvite", FieldValue.arrayUnion(userID));
            eventDocument.update("invitationList", FieldValue.arrayRemove(userID));
            event.declineInvitation(userID);
        }

        // MODIFIED: Now accepts latitude and longitude to store user location
        public void enterLottery(User user, double latitude, double longitude) {
            String userID = user.getID();
            DocumentReference eventDoc = fStore.collection(EVENT_COLLECTION)
                .document(event.getID());

            // Update waiting list
            eventDoc.update("waitingList", FieldValue.arrayUnion(userID));

            // Store location as GeoPoint in Firestore
            GeoPoint location = new GeoPoint(latitude, longitude);
            eventDoc.update("waitingListLocations." + userID, location);

            // Update local event object
            event.addToWaitingList(userID, latitude, longitude);
            HashMap<String, String> eventAttributes = new HashMap<>();
            eventAttributes.put("name", event.getName());
            eventAttributes.put("ID", event.getID());
            fStore.collection(USER_COLLECTION)
                .document(userID)
                .update("eventHistory", FieldValue.arrayUnion(eventAttributes));
        }

        // BACKWARD COMPATIBILITY: Keep old method that doesn't require location
        public void enterLottery(User user) {
            enterLottery(user, 0.0, 0.0);
        }

        // MODIFIED: Remove location data when user leaves lottery
        public void leaveLottery(User user) {
            String userID = user.getID();
            DocumentReference eventDoc = fStore.collection(EVENT_COLLECTION)
                .document(event.getID());

            // Remove from waiting list
            eventDoc.update("waitingList", FieldValue.arrayRemove(userID));

            // Remove location data from Firestore
            Map<String, Object> updates = new HashMap<>();
            updates.put("waitingListLocations." + userID, FieldValue.delete());
            eventDoc.update(updates);

            // Update local event object
            event.leaveWaitingList(userID);
        }

        public void clearWaitingList() {
            DocumentReference eventDoc = fStore.collection(EVENT_COLLECTION)
                .document(event.getID());
            eventDoc.update("waitingList", new ArrayList<>());
            event.setWaitingList(new ArrayList<>());
        }

        public void drawEntrants(OnSuccessListener<Void> completed) {
            Map<String, String> meta = new HashMap<>();
            meta.put("title", "You\'ve been invited to join " + event.getName());
            meta.put("description", "Click here to join!");
            meta.put("eventID", event.getID());
            Notification notificationTemplate = new Notification();
            notificationTemplate.setMeta(meta);
            notificationTemplate.setType(0);
            notificationTemplate.setCreationDate(new Date());

            event.pollForInvitation();
            List<Single<DocumentSnapshot>> invitedUserInstances = new ArrayList<>();
            List<Single<DocumentSnapshot>> notInvitedUserInstances = new ArrayList<>();

            for (String invitedUser : event.getInvitationList()) {
                Single<DocumentSnapshot> userSingle = RxFirebase.toSingle(
                    fStore.collection(USER_COLLECTION)
                        .document(invitedUser)
                        .get());
                invitedUserInstances.add(userSingle);
                fStore.collection(EVENT_COLLECTION)
                    .document(event.getID())
                    .update("waitingList", FieldValue.arrayRemove(invitedUser));
                fStore.collection(EVENT_COLLECTION)
                    .document(event.getID())
                    .update("invitationList", FieldValue.arrayUnion(invitedUser));
            }
            for (String notInvitedUser : event.getWaitingList()) {
                Single<DocumentSnapshot> userSingle = RxFirebase.toSingle(
                    fStore.collection(USER_COLLECTION)
                        .document(notInvitedUser)
                        .get());
                notInvitedUserInstances.add(userSingle);
                fStore.collection(EVENT_COLLECTION)
                    .document(event.getID())
                    .update("waitingList", FieldValue.arrayRemove(notInvitedUser));
            }


            Observable.fromIterable(invitedUserInstances)
                .flatMapSingle(single -> single)
                .subscribe(userSnapshot -> {
                    User user = userSnapshot.toObject(User.class);
                    DataStoreUser dataStoreUser = user(user);
                    if (dataStoreUser.user.getNotificationsEnabled()) {
                        dataStoreUser.postNotification(notificationTemplate, __ -> {
                        }, __ -> {
                        });
                    }
                }, e -> {

                }, () -> {
                    Observable.fromIterable(notInvitedUserInstances)
                        .flatMapSingle(single -> single)
                        .subscribe(userSnapshot -> {
                            meta.put("title", "New notification from " + event.getName());
                            meta.put("description", "Click here to view details.");
                            notificationTemplate.setType(1);
                            User user = userSnapshot.toObject(User.class);
                            DataStoreUser dataStoreUser = user(user);
                            if (dataStoreUser.user.getNotificationsEnabled()) {
                                dataStoreUser.postNotification(notificationTemplate, __ -> {
                                }, __ -> {
                                });
                            }
                        }, e -> {

                        }, () -> {
                            completed.onSuccess(null);
                        });
                });
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