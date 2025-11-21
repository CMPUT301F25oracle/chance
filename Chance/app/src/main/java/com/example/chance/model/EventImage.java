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
