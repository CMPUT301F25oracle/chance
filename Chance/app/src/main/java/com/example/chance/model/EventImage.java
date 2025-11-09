package com.example.chance.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Base64;
import java.util.List;

public class EventImage {

    @DocumentId
    private String event_id;
    private Base64 event_image;

    public EventImage(String event_id, Base64 event_image) {
        this.event_image = event_image;
        this.event_id = event_id;
    }

    public Base64 getEventImage() {
        return event_image;
    }

    public void setEventImage(String event_id, Base64 event_image) {
        this.event_image = event_image;
        this.event_id = event_id;
    }
}
