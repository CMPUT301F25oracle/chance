package com.example.chance.model;

import com.google.common.primitives.Bytes;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Notification {
    @DocumentId
    private String ID;
    private int type;
    private Date postedAt;
    private Blob meta;

    public Notification(String ID, int type, String message, Date postedAt, Blob meta) {
        this.ID = ID;
        this.type = type;
        this.postedAt = postedAt;
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

    public Date getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(Date postedAt) {
        this.postedAt = postedAt;
    }

    public Blob getMeta() {
        return meta;
    }

    public void setMeta(Blob meta) {
        this.meta = meta;
    }
}
