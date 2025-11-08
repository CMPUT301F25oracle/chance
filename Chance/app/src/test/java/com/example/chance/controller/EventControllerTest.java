package com.example.chance.controller;

import com.example.chance.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * JUnit 4 + Mockito tests for EventController.
 * Verifies Firestore CRUD calls are routed correctly via FirebaseManager.
 */
public class EventControllerTest {

    private EventController eventController;

    @Mock private FirebaseManager mockFirebaseManager;
    @Mock private OnSuccessListener<DocumentReference> mockOnSuccessRef;
    @Mock private OnSuccessListener<Event> mockOnSuccessEvent;
    @Mock private OnSuccessListener<List<Event>> mockOnSuccessList;
    @Mock private OnSuccessListener<Void> mockOnSuccessVoid;
    @Mock private OnFailureListener mockOnFailure;
    @Mock private EventListener<QuerySnapshot> mockListener;
    @Mock private DocumentSnapshot mockDocument;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Replace FirebaseManager singleton with mock
        eventController = new EventController() {
            { // anonymous subclass to override FirebaseManager instance
                try {
                    java.lang.reflect.Field field = EventController.class.getDeclaredField("firebaseManager");
                    field.setAccessible(true);
                    field.set(this, mockFirebaseManager);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    // ---- Create Event ----
    @Test
    public void testCreateEventCallsAddDocument() {
        Event event = new Event();
        eventController.createEvent(event, mockOnSuccessRef, mockOnFailure);

        verify(mockFirebaseManager, times(1))
                .addDocument(eq("events"), eq(event), eq(mockOnSuccessRef), eq(mockOnFailure));
    }

    // ---- Get Single Event ----
    @Test
    public void testGetEventCallsGetDocument() {
        eventController.getEvent("E001", mockOnSuccessEvent, mockOnFailure);

        verify(mockFirebaseManager, times(1))
                .getDocument(eq("events"), eq("E001"), any(), eq(mockOnFailure));
    }

    // ---- Get All Events ----
    @Test
    public void testGetAllEventsCallsFirestoreCollectionGet() {
        // The FirebaseManager returns its Firestore instance for queries,
        // but here we simply verify that the collection "events" is referenced
        eventController.getAllEvents(mockOnSuccessList, mockOnFailure);

        verify(mockFirebaseManager.getDb(), atLeastOnce());
    }

    // ---- Update Event ----
    @Test
    public void testUpdateEventCallsSetDocument() {
        Event updated = new Event();
        eventController.updateEvent("E002", updated, mockOnSuccessVoid, mockOnFailure);

        verify(mockFirebaseManager, times(1))
                .setDocument(eq("events"), eq("E002"), eq(updated), eq(mockOnSuccessVoid), eq(mockOnFailure));
    }

    // ---- Delete Event ----
    @Test
    public void testDeleteEventCallsDeleteDocument() {
        eventController.deleteEvent("E003", mockOnSuccessVoid, mockOnFailure);

        verify(mockFirebaseManager, times(1))
                .deleteDocument(eq("events"), eq("E003"), eq(mockOnSuccessVoid), eq(mockOnFailure));
    }

    // ---- Real-Time Listener ----
    @Test
    public void testListenForEventChangesCallsListenToCollection() {
        eventController.listenForEventChanges(mockListener);

        verify(mockFirebaseManager, times(1))
                .listenToCollection(eq("events"), eq(mockListener));
    }

    // ---- Error Handling Path Simulation ----
    @Test
    public void testGetEventHandlesMissingDocumentGracefully() {
        // Simulate a document that does not exist
        when(mockDocument.exists()).thenReturn(false);

        // Capture the OnSuccessListener passed to FirebaseManager.getDocument
        ArgumentCaptor<OnSuccessListener<DocumentSnapshot>> captor = ArgumentCaptor.forClass(OnSuccessListener.class);

        eventController.getEvent("MissingEvent", mockOnSuccessEvent, mockOnFailure);
        verify(mockFirebaseManager).getDocument(eq("events"), eq("MissingEvent"), captor.capture(), eq(mockOnFailure));

        // Invoke captured listener manually to simulate Firebase response
        captor.getValue().onSuccess(mockDocument);

        verify(mockOnFailure, atLeastOnce()).onFailure(any(Exception.class));
    }
}
