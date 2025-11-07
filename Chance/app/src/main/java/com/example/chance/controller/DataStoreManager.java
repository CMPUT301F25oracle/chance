package com.example.chance.controller;

import com.example.chance.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DataStoreManager {
    private static DataStoreManager instance;

    private final FirebaseManager db;

    private DataStoreManager() {
        db = FirebaseManager.getInstance();
    }

    public static DataStoreManager getInstance() {
        if (instance == null) {
            instance = new DataStoreManager();
        }
        return instance;
    }

    public User createUser(String username, String password) {
        User new_user = new User(username, password, "");
        db.setDocument("users", new_user.getUsername(), new_user, (s)->{}, (s)->{});
        return new_user;
    }
    // TODO: make WAYYYYY more secure.
    public void getUser(String username, OnSuccessListener<User> onSuccess) {
        db.getDocument("users", username, (doc) -> {
            if (doc.exists()) {
                User user = doc.toObject(User.class);
                onSuccess.onSuccess(user);
            } else {
                onSuccess.onSuccess(null);
            }
        }, (e)->{});
    }
}
