package com.example.chance.controller;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
import java.util.UUID;
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

    private final String PSEUDO_EMAIL = "@authentication.chance";
    private final String USER_COLLECTION = "users";
    private final String NOTIFICATION_COLLECTION = "notifications";
    private final String EVENT_COLLECTION = "events";
    private final String EVENT_IMAGE_COLLECTION = "event_image";

    private DataStoreManager() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
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
                                        userCollectionRef
                                            .collection(NOTIFICATION_COLLECTION)
                                            .add(new Notification(-1, null, null))
                                            .addOnSuccessListener(___ -> {
                                                onSuccess.onSuccess(user);
                                            });
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
        fStore.collection(USER_COLLECTION)
            .document(uid)
            .get()
            .addOnSuccessListener(userDocument -> {
                if (userDocument.exists()) {
                    User user = userDocument.toObject(User.class);
                    onSuccess.onSuccess(user);
                } else {
                    onFailure.onFailure(null);
                }
            })
            .addOnFailureListener(onFailure);
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
                                event.setID(document.getId());
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
     * updates the user in firebase
     * @param updatedUser
     * @param onSuccess
     */
    public void updateUser(User updatedUser, OnSuccessListener<Void> onSuccess) {
        fStore.collection(USER_COLLECTION)
            .document(updatedUser.getID())
            .set(updatedUser)
            .addOnSuccessListener(onSuccess);
    }

    /**
     * deletes the user from firebase
     * @param userId
     * @param onSuccess
     */
    public void deleteUser(String userId, OnSuccessListener<Void> onSuccess) {
        fStore.collection(USER_COLLECTION)
            .document(userId)
            .delete()
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(e -> {
                throw new RuntimeException(e);
            });
    }

    /**
     * Get an event from Firestore.
     * @param id
     * @param onSuccess
     */
    public void getEvent(String id, OnSuccessListener<Event> onSuccess) {
        fStore.collection(EVENT_COLLECTION)
            .document(id)
            .get()
            .addOnSuccessListener(eventSnapshot -> {
                if (eventSnapshot.exists()) {
                    Event event = eventSnapshot.toObject(Event.class);
                    onSuccess.onSuccess(event);
                } else {
                    onSuccess.onSuccess(null);
                }
            })
            .addOnFailureListener(e -> {});
    }

    /**
     * Get all events from Firestore.
     * @param onSuccess
     */
    public void getAllEvents(OnSuccessListener<List<Event>> onSuccess) {
        fStore.collection(EVENT_COLLECTION)
            .get()
            .addOnSuccessListener(snapshot -> {
                List<Event> events = snapshot.toObjects(Event.class);
                onSuccess.onSuccess(events);
            })
            .addOnFailureListener(e -> {});
    }

    public void removeEvent(Event event, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        fStore.collection(EVENT_COLLECTION)
            .document(event.getID())
            .delete()
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure);
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

        public void create(OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
            DocumentReference newDocument = fStore.collection(EVENT_COLLECTION)
                .document();
            event.setID(newDocument.getId());
            newDocument.set(event)
                .addOnSuccessListener(document -> {
                    onSuccess.onSuccess(null);
                })
                .addOnFailureListener(onFailure);
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
            HashMap<String, String> newHistoryEntry = new HashMap<>();
            newHistoryEntry.put("name", event.getName());
            newHistoryEntry.put("ID", event.getID());
            fStore.collection(USER_COLLECTION)
                .document(userID)
                .update("eventHistory", FieldValue.arrayUnion(newHistoryEntry));
            for (Map<String, String> history : user.getEventHistory()) {
                if (history.get("ID").equals(event.getID())) {
                    return;
                }
            }
            Log.d("DataStoreManager", "Adding new history entry for user " + userID + " in event " + event.getID());
            List<Map<String, String>> newHistory = new ArrayList<>(user.getEventHistory());
            newHistory.addLast(newHistoryEntry);
            user.setEventHistory(newHistory);
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

        @SuppressLint("CheckResult")
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



/**
 * ==================== DataStoreManager.java Comments ====================
 *
 * This file contains the DataStoreManager class, which is responsible for all interactions
 * with the Firebase Firestore database. It follows a singleton pattern to ensure only one
 * instance manages the database connection.
 *
 * === DataStoreManager Class ===
 * Manages all data persistence and retrieval from Firestore. It includes methods for
 * managing users, events, and their related data like notifications and images.
 *
 * --- getInstance Method ---
 * Provides a global access point to the singleton instance of DataStoreManager.
 * @return The single instance of DataStoreManager.
 *
 * --- observeEvents Method ---
 * Sets up a real-time listener on the events collection in Firestore. It emits a tuple
 * containing the event object, the type of change (added, modified, removed), and a null value
 * whenever there is a change in the event data.
 * @return An Observable that emits event changes.
 *
 * --- getEventBannerFromID Method ---
 * Retrieves the banner image for a specific event, decodes it from Base64, and returns it as a Bitmap.
 * @param ID The ID of the event to get the banner for.
 * @param onSuccess Success listener returning the Bitmap image.
 * @param onFailure Failure listener.
 *
 * --- deleteEventBanner Method ---
 * Deletes the event banner associated with a given event ID from the 'event_image' collection.
 * @param eventId The ID of the event, which is also the document ID of the banner image.
 * @param onSuccess Listener for successful deletion.
 * @param onFailure Listener for failed deletion.
 *
 * --- getEvents Method ---
 * Fetches all events from the Firestore 'events' collection once.
 * @param onSuccess Success listener returning a list of all events.
 * @param onFailure Failure listener.
 *
 * --- updateUser Method ---
 * Updates a user's document in the 'users' collection. The entire user object is overwritten.
 * @param updatedUser The User object with updated information.
 * @param onSuccess Success listener.
 *
 * --- deleteUser Method ---
 * Deletes a user from the 'users' collection in Firestore.
 * @param userId The ID of the user to be deleted.
 * @param onSuccess Success listener.
 *
 * --- getEvent Method ---
 * Retrieves a single event by its document ID from the 'events' collection.
 * @param id The document ID of the event.
 * @param onSuccess Success listener returning the Event object, or null if not found.
 *
 * --- getAllEvents Method ---
 * Retrieves all events from the 'events' collection.
 * @param onSuccess Success listener returning a list of all Event objects.
 *
 * --- removeEvent Method ---
 * Deletes a specific event document from the 'events' collection.
 * @param event The event object to be deleted.
 * @param onSuccess Listener for successful deletion.
 * @param onFailure Listener for failed deletion.
 *
 * --- user Method ---
 * Factory method to create a DataStoreUser instance for a specific user. This provides
 * a scoped API for user-related database operations.
 * @param target_user The user to perform operations on.
 * @return A new DataStoreUser instance.
 *
 *
 * === DataStoreUser Inner Class ===
 * Provides a scoped API for database operations related to a single user.
 *
 * --- getNotifications Method ---
 * Fetches all non-system notifications for the user.
 * @param onSuccess Success listener returning a list of Notification objects.
 * @param onFailure Failure listener.
 *
 * --- observeNotifications Method ---
 * Sets up a real-time listener for the user's notifications.
 * @return An Observable that emits notification changes.
 *
 * --- deleteAllNotifications Method ---
 * Deletes all notifications for the user.
 * @param onSuccess Success listener.
 * @param onFailure Failure listener.
 *
 * --- postNotification Method ---
 * Adds a new notification to the user's notification sub-collection.
 * @param notification The notification to add.
 * @param onSuccess Success listener.
 * @param onFailure Failure listener.
 *
 *
 * --- event Method ---
 * Factory method to create an __event instance for a specific event. This provides
 * a scoped API for event-related database operations.
 * @param target_event The event to perform operations on.
 * @return A new __event instance.
 *
 * === __event Inner Class ===
 * Provides a scoped API for database operations related to a single event.
 *
 * --- create Method ---
 * Creates a new event document in Firestore and sets its ID.
 * @param onSuccess Success listener.
 * @param onFailure Failure listener.
 *
 * --- getUsersInLottery Method ---
 * Retrieves the list of user IDs in the event's waiting list (lottery).
 * @param onSuccess Success listener returning a list of user IDs.
 * @param onFailure Failure listener.
 *
 * --- checkUserInLottery Method ---
 * Checks if a specific user is in the event's lottery.
 * @param user The user to check.
 * @param onSuccess Success listener returning true if the user is in the lottery, false otherwise.
 *
 * --- acceptedInvite Method ---
 * Moves a user from the 'invitationList' to the 'acceptedInvite' list for the event.
 * @param user The user who accepted the invitation.
 *
 * --- declinedInvite Method ---
 * Moves a user from the 'invitationList' to the 'declinedInvite' list for the event.
 * @param user The user who declined the invitation.
 *
 * --- enterLottery (with location) Method ---
 * Adds a user to the event's waiting list and records their geographical location.
 * Also adds the event to the user's event history.
 * @param user The user entering the lottery.
 * @param latitude The user's latitude.
 * @param longitude The user's longitude.
 *
 * --- enterLottery (without location) Method ---
 * Backward-compatible method to add a user to the lottery without location data.
 * @param user The user entering the lottery.
 *
 * --- leaveLottery Method ---
 * Removes a user from the event's waiting list and deletes their associated location data.
 * @param user The user leaving the lottery.
 */