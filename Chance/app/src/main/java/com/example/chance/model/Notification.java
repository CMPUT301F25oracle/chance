package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;
import java.util.Map;

@IgnoreExtraProperties
public class Notification {
    @DocumentId
    @Exclude
    private String ID;
    private Date creationDate = new Date();
    private int type;
    private Map<String, String> meta;

    public Notification() {}

    public Notification(int type, Date creationDate, Map<String, String> meta) {
        this.creationDate = creationDate;
        this.type = type;
        this.meta = meta;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}

/**
 * ==================== Notification.java Comments ====================
 *
 * This file defines the Notification class, which serves as the data model for a notification
 * sent to a user within the application. It encapsulates all the properties related to a
 * notification, such as its type and any associated metadata. This class is designed to be
 * directly used with Firebase Firestore for data persistence within a sub-collection of a user.
 *
 * === Notification Class ===
 * Represents a single notification. Each notification has a type, a creation date, and a
 * flexible 'meta' map to store additional, context-specific information. For example, a
 * notification about an event might store the event's ID and name in the meta map.
 *
 * --- @DocumentId & @Exclude ---
 * The 'ID' field is automatically populated by Firestore with the document's ID.
 * The @Exclude annotation prevents this field from being serialized when writing to Firestore,
 * as the ID is metadata managed by the database itself.
 *
 * --- @IgnoreExtraProperties ---
 * Allows Firestore to gracefully handle cases where the data in the database might have
 * fields that are not defined in this class, preventing crashes during deserialization.
 *
 * === Constructors ===
 * - A required empty public constructor for Firestore deserialization.
 * - A parameterized constructor for creating new Notification instances with their essential data.
 *
 * === Getters and Setters ===
 * Standard accessor and mutator methods are provided for all properties (ID, creationDate, type, meta),
 * allowing other parts of the application to interact with the notification data in a
 * structured way.
 */