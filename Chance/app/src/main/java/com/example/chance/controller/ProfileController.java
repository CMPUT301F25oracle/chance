package com.example.chance.controller;

import com.example.chance.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Allows retrieving and updating user profile information.
 */
public class ProfileController {

    private static final String COLLECTION = "users";
    private final FirebaseManager firebaseManager;

    public ProfileController() {
        firebaseManager = FirebaseManager.getInstance();
    }

    public void getUser(String userId,
                        OnSuccessListener<User> onSuccess,
                        OnFailureListener onFailure) {

        firebaseManager.getDocument(COLLECTION, userId,
                doc -> {
                    if (doc.exists()) {
                        User user = doc.toObject(User.class);
                        onSuccess.onSuccess(user);
                    } else {
                        onFailure.onFailure(new Exception("User does not exist"));
                    }
                },
                onFailure);
    }

    public void updateUser(String userId, User updatedUser,
                           OnSuccessListener<Void> onSuccess,
                           OnFailureListener onFailure) {

        firebaseManager.setDocument(COLLECTION, userId, updatedUser, onSuccess, onFailure);
    }
}
