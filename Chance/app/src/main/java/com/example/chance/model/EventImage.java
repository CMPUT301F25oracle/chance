package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Base64;
import java.util.List;

public class EventImage {

    @DocumentId
    private String ID;

    private String eventImage;

    public EventImage() {}
    public EventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }
}

/**
 * ==================== EventImage.java Comments ====================
 *
 * This file defines the EventImage class, which serves as the data model for an
 * event's associated image, typically a banner or poster. This class is designed
 * to be stored in a dedicated 'event_image' collection in Firebase Firestore.
 *
 * === EventImage Class ===
 * Represents the image data for a single event. The image itself is stored as a
 * String, which is expected to be a Base64 encoded representation of the image file.
 * This approach allows for embedding the image data directly within the Firestore
 * document.
 *
 * --- @DocumentId ---
 * The 'ID' field is automatically populated by Firestore with the document's ID.
 * Crucially, this ID is designed to match the ID of the corresponding Event document,
 * creating a direct one-to-one relationship between an event and its image.
 *
 * === Constructors ===
 * - An empty public constructor is required for Firestore to automatically deserialize
 *   documents into EventImage objects.
 * - A parameterized constructor is provided for convenience when creating a new
 *   EventImage instance with its Base64 image data.
 *
 * === Getters and Setters ===
 * Standard accessor and mutator methods are provided for all properties, allowing
 * other parts of the application to interact with the event image data.
 */