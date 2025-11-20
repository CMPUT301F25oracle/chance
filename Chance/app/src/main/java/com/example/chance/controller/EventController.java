//package com.example.chance.controller;
//
//import com.example.chance.model.Event;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.EventListener;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * EventController manages all logic related to Event creation,
// * modification, retrieval, deletion, and real-time updates.
// */
//public class EventController {
//
//    private static final String COLLECTION = "events";
//    private final FirebaseManager firebaseManager;
//
//    public EventController() {
//        firebaseManager = FirebaseManager.getInstance();
//    }
//
//    // -------------------- Create --------------------
//    public void createEvent(Event event,
//                            OnSuccessListener<DocumentReference> onSuccess,
//                            OnFailureListener onFailure) {
//        firebaseManager.addDocument(COLLECTION, event, onSuccess, onFailure);
//    }
//
//    // -------------------- Read (Single Event) --------------------
//    public void getEvent(String eventId,
//                         OnSuccessListener<Event> onSuccess,
//                         OnFailureListener onFailure) {
//        firebaseManager.getDocument(COLLECTION, eventId,
//                document -> {
//                    if (document.exists()) {
//                        Event event = document.toObject(Event.class);
//                        onSuccess.onSuccess(event);
//                    } else {
//                        onFailure.onFailure(new Exception("Event not found"));
//                    }
//                },
//                onFailure);
//    }
//
//    // -------------------- Read (All Events) --------------------
//    public void getAllEvents(OnSuccessListener<List<Event>> onSuccess,
//                             OnFailureListener onFailure) {
//        firebaseManager.getDb().collection(COLLECTION)
//                .get()
//                .addOnSuccessListener(query -> {
//                    List<Event> events = new ArrayList<>();
//                    for (DocumentSnapshot doc : query.getDocuments()) {
//                        Event event = doc.toObject(Event.class);
//                        if (event != null) {
//                            event.setID(doc.getId());
//                            events.add(event);
//                        }
//                    }
//                    onSuccess.onSuccess(events);
//                })
//                .addOnFailureListener(onFailure);
//    }
//
//    // -------------------- Update --------------------
//    public void updateEvent(String eventId,
//                            Event updatedEvent,
//                            OnSuccessListener<Void> onSuccess,
//                            OnFailureListener onFailure) {
//        firebaseManager.setDocument(COLLECTION, eventId, updatedEvent, onSuccess, onFailure);
//    }
//
//    // -------------------- Delete --------------------
//    public void deleteEvent(String eventId,
//                            OnSuccessListener<Void> onSuccess,
//                            OnFailureListener onFailure) {
//        firebaseManager.deleteDocument(COLLECTION, eventId, onSuccess, onFailure);
//    }
//
//    // -------------------- Real-Time Listener --------------------
//    public void listenForEventChanges(EventListener<QuerySnapshot> listener) {
//        firebaseManager.listenToCollection(COLLECTION, listener);
//    }
//}


package com.example.chance.controller;

import com.example.chance.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * EventController manages all logic related to Event creation,
 * modification, retrieval, deletion, and real-time updates.
 */
public class EventController {

    private static final String COLLECTION = "events";
    private static final String EVENT_IMAGE_COLLECTION = "event_image"; // Added for clarity
    private final FirebaseManager firebaseManager;
    private final DataStoreManager dataStoreManager; // Reference to DataStoreManager for specific tasks

    public EventController() {
        firebaseManager = FirebaseManager.getInstance();
        dataStoreManager = DataStoreManager.getInstance(); // Initialize DataStoreManager
    }

    // -------------------- Create --------------------
    public void createEvent(Event event,
                            OnSuccessListener<DocumentReference> onSuccess,
                            OnFailureListener onFailure) {
        firebaseManager.addDocument(COLLECTION, event, onSuccess, onFailure);
    }

    // -------------------- Read (Single Event) --------------------
    public void getEvent(String eventId,
                         OnSuccessListener<Event> onSuccess,
                         OnFailureListener onFailure) {
        firebaseManager.getDocument(COLLECTION, eventId,
                document -> {
                    if (document.exists()) {
                        Event event = document.toObject(Event.class);
                        onSuccess.onSuccess(event);
                    } else {
                        onFailure.onFailure(new Exception("Event not found"));
                    }
                },
                onFailure);
    }

    // -------------------- Read (All Events) --------------------
    public void getAllEvents(OnSuccessListener<List<Event>> onSuccess,
                             OnFailureListener onFailure) {
        firebaseManager.getDb().collection(COLLECTION)
                .get()
                .addOnSuccessListener(query -> {
                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            event.setID(doc.getId());
                            events.add(event);
                        }
                    }
                    onSuccess.onSuccess(events);
                })
                .addOnFailureListener(onFailure);
    }

    // -------------------- Update --------------------
    public void updateEvent(String eventId,
                            Event updatedEvent,
                            OnSuccessListener<Void> onSuccess,
                            OnFailureListener onFailure) {
        firebaseManager.setDocument(COLLECTION, eventId, updatedEvent, onSuccess, onFailure);
    }

    /**
     * Removes the event banner by deleting the corresponding document
     * from the event_image collection via the DataStoreManager.
     * @param eventId The ID of the event whose banner should be removed.
     * @param onSuccess Callback for successful removal.
     * @param onFailure Callback for failure during removal.
     */
    public void removeEventBanner(String eventId,
                                  OnSuccessListener<Void> onSuccess,
                                  OnFailureListener onFailure) {
        // Delegate the deletion of the banner document (which is keyed by eventId)
        // to the DataStoreManager.
        dataStoreManager.deleteEventBanner(eventId, onSuccess, onFailure);
    }

    // -------------------- Delete --------------------
    public void deleteEvent(String eventId,
                            OnSuccessListener<Void> onSuccess,
                            OnFailureListener onFailure) {
        firebaseManager.deleteDocument(COLLECTION, eventId, onSuccess, onFailure);
    }

    // -------------------- Real-Time Listener --------------------
    public void listenForEventChanges(EventListener<QuerySnapshot> listener) {
        firebaseManager.listenToCollection(COLLECTION, listener);
    }
}