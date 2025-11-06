package com.example.chance.controller;

import com.example.chance.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all event-related Firestore logic (CRUD operations).
 * Uses FirebaseManager as a bridge to Firestore.
 */
public class EventController {

    private static final String COLLECTION = "events";
    private final FirebaseManager firebaseManager;

    public EventController() {
        firebaseManager = FirebaseManager.getInstance();
    }

    // --- Add Event ---
    public void addEvent(Event event,
                         OnSuccessListener<DocumentReference> onSuccess,
                         OnFailureListener onFailure) {
        firebaseManager.addDocument(COLLECTION, event, onSuccess, onFailure);
    }

    // --- Get Single Event ---
    public void getEvent(String eventId,
                         OnSuccessListener<Event> onSuccess,
                         OnFailureListener onFailure) {
        firebaseManager.getDocument(COLLECTION, eventId,
                document -> {
                    if (document.exists()) {
                        Event event = document.toObject(Event.class);
                        if (event != null) event.setId(document.getId());
                        onSuccess.onSuccess(event);
                    } else {
                        onFailure.onFailure(new Exception("Event not found"));
                    }
                },
                onFailure);
    }

    // --- Get All Events ---
    public void getAllEvents(OnSuccessListener<List<Event>> onSuccess,
                             OnFailureListener onFailure) {
        firebaseManager.getAllDocuments(COLLECTION,
                querySnapshot -> {
                    List<Event> events = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Event event = doc.toObject(Event.class);
                        event.setId(doc.getId());
                        events.add(event);
                    }
                    onSuccess.onSuccess(events);
                },
                onFailure);
    }

    // --- Update Event ---
    public void updateEvent(String eventId, Event updatedEvent,
                            OnSuccessListener<Void> onSuccess,
                            OnFailureListener onFailure) {
        firebaseManager.setDocument(COLLECTION, eventId, updatedEvent, onSuccess, onFailure);
    }

    // --- Delete Event ---
    public void deleteEvent(String eventId,
                            OnSuccessListener<Void> onSuccess,
                            OnFailureListener onFailure) {
        firebaseManager.deleteDocument(COLLECTION, eventId, onSuccess, onFailure);
    }
}
