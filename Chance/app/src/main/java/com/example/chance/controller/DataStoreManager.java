//package com.example.chance.controller;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//
//import androidx.annotation.NonNull;
//
//import com.example.chance.model.Event;
//import com.example.chance.model.EventImage;
//import com.example.chance.model.User;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.gms.tasks.Tasks;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.DocumentChange;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FieldValue;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.Base64;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executor;
//
//
//public class DataStoreManager {
//    // firestore requires emails to create new user accounts, so we simply
//    // use our own pseudo domain to get around this; specifically its
//    // preferred we use emails to login over usernames
//    private static DataStoreManager instance;
//    private static FirebaseAuth fAuth;
//    private static FirebaseFirestore fStore;
//    private final FirebaseManager db;
//
//    private final String PSEUDO_EMAIL = "@authentication.chance";
//    private final String USER_COLLECTION = "users";
//    private final String EVENT_COLLECTION = "events";
//    private final String EVENT_IMAGE_COLLECTION = "event_image";
//
//    private DataStoreManager() {
//        fAuth = FirebaseAuth.getInstance();
//        fStore = FirebaseFirestore.getInstance();
//        db = FirebaseManager.getInstance();
//    }
//
//    /**
//     * gets the datastore instance
//     * @return
//     */
//    public static DataStoreManager getInstance() {
//        if (instance == null) {
//            instance = new DataStoreManager();
//        }
//        return instance;
//    }
//
//    public Boolean isDeviceAuthenticated() {
//        FirebaseUser user = fAuth.getCurrentUser();
//        return user != null;
//    }
//
//    public void getAuthenticatedUser(OnSuccessListener<User> onSuccess, OnFailureListener onFailure) {
//        FirebaseUser user = fAuth.getCurrentUser();
//        if (user != null) {
//            getUserFromUID(user.getUid(), onSuccess, onFailure);
//        } else {
//            onFailure.onFailure(null);
//        }
//    }
//
//    public void authenticateUser(String username, String password, OnSuccessListener<User> onSuccess, OnFailureListener onFailure) {
//        fAuth.signInWithEmailAndPassword(username + PSEUDO_EMAIL, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseUser firestoreUser = fAuth.getCurrentUser();
//                            getUserFromUID(firestoreUser.getUid(), onSuccess, onFailure);
//                            // now we grab the users information from firestore
//                            onSuccess.onSuccess(null);
//                        } else {
//                            onFailure.onFailure(task.getException());
//                        }
//                    }
//                });
//    }
//
//    public void logoutAuthenticatedUser() {
//        fAuth.signOut();
//    }
//
//    public void createNewUser(String username, String password, OnSuccessListener<User> onSuccess, OnFailureListener onFailure) {
//        // source: https://firebase.google.com/docs/auth/android/password-auth
//        fAuth.createUserWithEmailAndPassword(username + PSEUDO_EMAIL, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseUser firestoreUser = fAuth.getCurrentUser();
//                            User user = new User(username);
//                            // now that the user is in firestore, we create their db entry
//                            assert firestoreUser != null;
//                            fStore.collection(USER_COLLECTION)
//                                .document(firestoreUser.getUid())
//                                .set(user)
//                                .addOnSuccessListener((__) -> {
//                                    onSuccess.onSuccess(user);
//                                })
//                                .addOnFailureListener((e) -> {
//                                    onFailure.onFailure(e);
//                                });
//                        } else {
//                            onFailure.onFailure(task.getException());
//                        }
//                    }
//                });
//    }
//
//
//
//    public void getUserFromUID(String uid, OnSuccessListener<User> onSuccess, OnFailureListener onFailure) {
//        db.getDocument("users", uid, (document) -> {
//            if (document.exists()) {
//                User user = document.toObject(User.class);
//                onSuccess.onSuccess(user);
//            } else {
//                onFailure.onFailure(null);
//            }
//        }, onFailure);
//    }
//
//    public void createNewEvent(Event event, OnSuccessListener<Event> onSuccess, OnFailureListener onFailure) {
//        fStore.collection(EVENT_COLLECTION)
//                .add(event)
//                .addOnSuccessListener(document -> {
//                    event.setID(document.getId());
//                    onSuccess.onSuccess(event);
//                })
//                .addOnFailureListener(onFailure);
//    }
//
//    public void getEventBannerFromID(String ID, OnSuccessListener<Bitmap> onSuccess, OnFailureListener onFailure) {
//        fStore.collection(EVENT_IMAGE_COLLECTION)
//                .document(ID)
//                .get()
//                .addOnSuccessListener(document -> {
//                    if (!document.exists()) {
//                        onFailure.onFailure(new Exception("Event image not found for ID: " + ID));
//                        return;
//                    }
//
//                    EventImage eventImage = document.toObject(EventImage.class);
//                    byte[] imageBase64 = Base64.getDecoder().decode(eventImage.getEventImage());
//                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBase64, 0, imageBase64.length);
//                    onSuccess.onSuccess(imageBitmap);
//
//                    // Guard: check that the document actually exists
////                    if (document == null || !document.exists()) {
////                        // no document found for this ID
////                        onFailure.onFailure(new Exception("Event image not found for ID: " + ID));
////                        return;
////                    }
////
////                    EventImage eventImage = document.toObject(EventImage.class);
////                    if (eventImage == null || eventImage.getEventImage() == null || eventImage.getEventImage().isEmpty()) {
////                        onFailure.onFailure(new Exception("Event image data is missing or empty for ID: " + ID));
////                        return;
////                    }
////
////                    byte[] imageBase64;
////                    try {
////                        imageBase64 = Base64.getDecoder().decode(eventImage.getEventImage());
////                    } catch (IllegalArgumentException ex) {
////                        onFailure.onFailure(ex); // invalid base64
////                        return;
////                    }
////
////                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBase64, 0, imageBase64.length);
////                    if (imageBitmap == null) {
////                        onFailure.onFailure(new Exception("Failed to decode image bitmap for ID: " + ID));
////                        return;
////                    }
////
////                    onSuccess.onSuccess(imageBitmap);
//                })
//                .addOnFailureListener(onFailure);
//    }
//
//    /**
//     * gets all events from firestore, and returns them through success callback
//     * @param onSuccess
//     * @param onFailure
//     */
//    public void getEvents(OnSuccessListener<List<Event>> onSuccess, OnFailureListener onFailure) {
//        fStore.collection(EVENT_COLLECTION)
//                .get()
//                .addOnSuccessListener((snapshot) -> {
//                    List<Event> events = snapshot.toObjects(Event.class);
//                    onSuccess.onSuccess(events);
//                })
//                .addOnFailureListener(onFailure);
//    }
//
//    /**
//     * Get a user from Firestore.
//     * @param username
//     * @param onSuccess
//     */
//    public void getUser(String username, OnSuccessListener<User> onSuccess) {
//        if (username.isEmpty()) {
//            onSuccess.onSuccess(null);
//            return;
//        }
//        db.getDocument("users", username, (doc) -> {
//            if (doc.exists()) {
//                User user = doc.toObject(User.class);
//                onSuccess.onSuccess(user);
//            } else {
//                onSuccess.onSuccess(null);
//            }
//        }, (e)->{});
//    }
//
//    /**
//     * updates the user in firebase
//     * @param username
//     * @param updatedUser
//     * @param onSuccess
//     */
//    public void updateUser(String username, User updatedUser, OnSuccessListener<Void> onSuccess) {
//        db.setDocument("users", username, updatedUser, onSuccess, (e)->{});
//    }
//
//    /**
//     * deletes the user from firebase
//     * @param username
//     * @param onSuccess
//     */
//    public void deleteUser(String username, OnSuccessListener<Void> onSuccess) {
//        db.deleteDocument("users", username, (___na) -> {
//            onSuccess.onSuccess(null);
//        }, (e)->{
//            throw new RuntimeException();
//        });
//    }
//
//
//    /**
//     * joins the user into the waiting list
//     * @param event
//     * @param entrantId
//     * @param onSuccess
//     */
//    public void joinWaitingList(Event event, String entrantId, OnSuccessListener<Void> onSuccess) {
//        event.addToWaitingList(entrantId);
//        db.setDocument("events", event.getID(), event, onSuccess, (e)->{});
//    }
//
//    /**
//     * removes the user from the waiting list
//     * @param event
//     * @param entrantId
//     * @param onSuccess
//     */
//    public void leaveWaitingList(Event event, String entrantId, OnSuccessListener<Void> onSuccess) {
//        event.leaveWaitingList(entrantId);
//        db.setDocument("events", event.getID(), event, onSuccess, (e)->{});
//    }
//
//    /**
//     * accepts the invitation for the specified event
//     * @param event
//     * @param entrantId
//     * @param onSuccess
//     */
//    public void acceptInvitation(Event event, String entrantId, OnSuccessListener<Void> onSuccess) {
//        event.acceptInvitation(entrantId);
//        db.setDocument("events", event.getID(), event, onSuccess, (e)->{});
//    }
//
//    /**
//     * rejects the invitation for the specified event
//     * @param event
//     * @param entrantId
//     * @param onSuccess
//     */
//    public void rejectInvitation(Event event, String entrantId, OnSuccessListener<Void> onSuccess) {
//        event.rejectInvitation(entrantId);
//        db.setDocument("events", event.getID(), event, onSuccess, (e)->{});
//    }
//
//    /**
//     * uploads an event poster
//     * @param event_id
//     * @param image
//     * @param onSuccess
//     */
////    public void uploadEventImage(String event_id, Base64 image, OnSuccessListener<Void> onSuccess) {
////        EventImage eventImage = new EventImage(image);
////        db.setDocument("event_images", event_id, eventImage, onSuccess, (e)->{});
////    }
//
//    /**
//     * grabs an events associated poster
//     * @param event_id
//     * @param onSuccess
//     */
////    public void browseEventImage(String event_id, OnSuccessListener<Base64> onSuccess) {
////        if (event_id.isEmpty()) {
////            onSuccess.onSuccess(null);
////            return;
////        } else {
////            db.getDocument("event_images", event_id, (doc) -> {
////                if (doc.exists()) {
////                    EventImage eventImage = doc.toObject(EventImage.class);
////                    onSuccess.onSuccess(eventImage.getEventImage());
////                } else {
////                    onSuccess.onSuccess(null);
////                }
////            }, (e) -> {
////            });
////        }
////    }
//
//
//
//    /**
//     * Create a new event in Firestore.
//     * @param name
//     * @param location
//     * @param capacity
//     * @param price
//     * @param description
//     * @param startDate
//     * @param endDate
//     * @param organizerUserName
//     * @return
//     */
//    public Event createEvent(String name, String location, int capacity, double price, String description, Date startDate, Date endDate, String organizerUserName) {
//        Event new_event = new Event(name, location, capacity, price, description, startDate, endDate, organizerUserName, 0);
//        db.setDocument("events", new_event.getID(), new_event, (s)->{}, (s)->{});
//        return new_event;
//    }
//
//    /**
//     * Get an event from Firestore.
//     * @param id
//     * @param onSuccess
//     */
//    public void getEvent(String id, OnSuccessListener<Event> onSuccess) {
//        db.getDocument("events", id, (doc) -> {
//            if (doc.exists()) {
//                Event event = doc.toObject(Event.class);
//                onSuccess.onSuccess(event);
//            } else {
//                onSuccess.onSuccess(null);
//            }
//        }, (e)->{});
//    }
//
//    /**
//     * Get all events from Firestore.
//     * @param onSuccess
//     */
//    public void getAllEvents(OnSuccessListener<List<Event>> onSuccess) {
//        db.getCollection("events", (snapshot) -> {
//            List<Event> events = snapshot.toObjects(Event.class);
//            onSuccess.onSuccess(events);
//        }, (e)->{});
//    }
//
//    public __event event(Event target_event) {
//        return new __event(target_event);
//    }
//
//    public class __event {
//        Event event;
//        __event(Event event) {
//            this.event = event;
//        }
//
//        public void getUsersInLottery(OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
//            fStore.collection(EVENT_COLLECTION)
//                    .document(event.getID())
//                    .get()
//                    .addOnSuccessListener(snapshot -> {
//                        Event event = snapshot.toObject(Event.class);
//                        List<String> waitingListUsers = event.getWaitingList();
//                        onSuccess.onSuccess(waitingListUsers);
//                    })
//                    .addOnFailureListener(onFailure);
//        }
//
//        public void checkUserInLottery(User user, OnSuccessListener<Boolean> onSuccess) {
//            getUsersInLottery(users -> {
//                boolean isInLottery = users.contains(user.getID());
//                onSuccess.onSuccess(isInLottery);
//            }, e->{});
//        }
//
//        public void enterLottery(User user) {
//            fStore.collection(EVENT_COLLECTION)
//                    .document(event.getID())
//                    .update("waitingList", FieldValue.arrayUnion(user.getID()));
//            event.addToWaitingList(user.getID());
//        }
//
//        public void leaveLottery(User user) {
//            fStore.collection(EVENT_COLLECTION)
//                    .document(event.getID())
//                    .update("waitingList", FieldValue.arrayRemove(user.getID()));
//            event.leaveWaitingList(user.getID());
//        }
//    }
//
//    public __eventImage eventImage(EventImage target_event_image) {
//        return new __eventImage(target_event_image);
//    }
//
//    public class __eventImage {
//        EventImage eventImage;
//        __eventImage(EventImage eventImage) {
//            this.eventImage = eventImage;
//        }
//
//        public void save(OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
//            fStore.collection(EVENT_IMAGE_COLLECTION)
//                    .document(eventImage.getID())
//                    .set(eventImage)
//                    .addOnSuccessListener(onSuccess)
//                    .addOnFailureListener(onFailure);
//        }
//
//    }
//}
//
//
//
package com.example.chance.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.model.Event;
import com.example.chance.model.EventImage;
import com.example.chance.model.Notification;
import com.example.chance.model.User;
import com.example.chance.util.Tuple3;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

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

                    // Guard: check that the document actually exists
//                    if (document == null || !document.exists()) {
//                        // no document found for this ID
//                        onFailure.onFailure(new Exception("Event image not found for ID: " + ID));
//                        return;
//                    }
//
//                    EventImage eventImage = document.toObject(EventImage.class);
//                    if (eventImage == null || eventImage.getEventImage() == null || eventImage.getEventImage().isEmpty()) {
//                        onFailure.onFailure(new Exception("Event image data is missing or empty for ID: " + ID));
//                        return;
//                    }
//
//                    byte[] imageBase64;
//                    try {
//                        imageBase64 = Base64.getDecoder().decode(eventImage.getEventImage());
//                    } catch (IllegalArgumentException ex) {
//                        onFailure.onFailure(ex); // invalid base64
//                        return;
//                    }
//
//                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBase64, 0, imageBase64.length);
//                    if (imageBitmap == null) {
//                        onFailure.onFailure(new Exception("Failed to decode image bitmap for ID: " + ID));
//                        return;
//                    }
//
//                    onSuccess.onSuccess(imageBitmap);
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
//    public void uploadEventImage(String event_id, Base64 image, OnSuccessListener<Void> onSuccess) {
//        EventImage eventImage = new EventImage(image);
//        db.setDocument("event_images", event_id, eventImage, onSuccess, (e)->{});
//    }

    /**
     * grabs an events associated poster
     * @param event_id
     * @param onSuccess
     */
//    public void browseEventImage(String event_id, OnSuccessListener<Base64> onSuccess) {
//        if (event_id.isEmpty()) {
//            onSuccess.onSuccess(null);
//            return;
//        } else {
//            db.getDocument("event_images", event_id, (doc) -> {
//                if (doc.exists()) {
//                    EventImage eventImage = doc.toObject(EventImage.class);
//                    onSuccess.onSuccess(eventImage.getEventImage());
//                } else {
//                    onSuccess.onSuccess(null);
//                }
//            }, (e) -> {
//            });
//        }
//    }



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

    public __user user(User target_user) {
        return new __user(target_user);
    }


    public class __user {
        User user;
        __user(User user) {
            this.user = user;
        }

        public Observable<Tuple3<Notification, DocumentChange.Type, Void>> observeNotifications() {
            return Observable.create(emitter -> {
                fStore.collection(NOTIFICATION_COLLECTION)
                    .collection(user.getID())
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
            fStore.collection(NOTIFICATION_COLLECTION)
                .collection(user.getID())
                    .add(notification)
                    .addOnSuccessListener(document -> {
                        notification.setID(document.getId());
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
            }, e->{});
        }

        public void enterLottery(User user) {
            fStore.collection(EVENT_COLLECTION)
                    .document(event.getID())
                    .update("waitingList", FieldValue.arrayUnion(user.getID()));
            event.addToWaitingList(user.getID());
        }

        public void leaveLottery(User user) {
            fStore.collection(EVENT_COLLECTION)
                    .document(event.getID())
                    .update("waitingList", FieldValue.arrayRemove(user.getID()));
            event.leaveWaitingList(user.getID());
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
