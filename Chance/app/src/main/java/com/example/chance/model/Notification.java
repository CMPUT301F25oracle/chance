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
    private Date creationDate;
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
