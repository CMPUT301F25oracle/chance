package com.example.chance.model;

import com.google.common.primitives.Bytes;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

@IgnoreExtraProperties
public class Notification {
    @DocumentId
    private String ID;

    private String stringJsonMeta;

    public Notification(String stringJsonMeta) {
        this.stringJsonMeta = stringJsonMeta;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getStringJsonMeta() {
        return stringJsonMeta;
    }

    public void setStringJsonMeta(String stringJsonMeta) {
        this.stringJsonMeta = stringJsonMeta;
    }

    @Exclude
    public JSONObject getJsonMeta() throws JSONException {
        return new JSONObject(this.stringJsonMeta);
    }

    @Exclude
    public void setJsonMeta(JSONObject jsonMeta) {
        this.stringJsonMeta = jsonMeta.toString();
    }
}
