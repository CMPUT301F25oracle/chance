package com.example.chance.controller;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chance.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for managing administrator operations.
 * Handles viewing and deleting users from the system.
 */
public class AdminController {
    private static final String TAG = "AdminController";
    private static final String USERS_COLLECTION = "users";
    private static final String PROFILE_IMAGES_PATH = "profile_images/";

    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    /**
     * Constructor initializing Firebase instances.
     */
    public AdminController() {
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    /**
     * Interface for user list retrieval callbacks.
     */
    public interface UserListCallback {
        void onSuccess(List<User> users);
        void onFailure(String errorMessage);
    }

    /**
     * Interface for delete operation callbacks.
     */
    public interface DeleteCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    /**
     * Retrieves all users from Firestore.
     *
     * @param callback Callback to handle success or failure
     */
    public void getAllUsers(UserListCallback callback) {
        db.collection(USERS_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> users = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                if (user != null) {
                                    user.setUserId(document.getId());
                                    users.add(user);
                                }
                            }
                            Log.d(TAG, "Successfully retrieved " + users.size() + " users");
                            callback.onSuccess(users);
                        } else {
                            String errorMsg = "Failed to retrieve users: " +
                                    (task.getException() != null ? task.getException().getMessage() : "Unknown error");
                            Log.e(TAG, errorMsg);
                            callback.onFailure(errorMsg);
                        }
                    }
                });
    }

    /**
     * Deletes a user from the system.
     * This includes removing the user document from Firestore and their profile image from Storage.
     *
     * @param userId The ID of the user to delete
     * @param callback Callback to handle success or failure
     */
    public void deleteUser(String userId, DeleteCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onFailure("Invalid user ID");
            return;
        }

        // First, try to delete the user's profile image if it exists
        deleteUserProfileImage(userId, new DeleteCallback() {
            @Override
            public void onSuccess() {
                // Profile image deleted or didn't exist, now delete user document
                deleteUserDocument(userId, callback);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Image deletion failed, but continue with user document deletion
                Log.w(TAG, "Failed to delete profile image: " + errorMessage);
                deleteUserDocument(userId, callback);
            }
        });
    }

    /**
     * Deletes a user's profile image from Firebase Storage.
     *
     * @param userId The ID of the user whose image to delete
     * @param callback Callback to handle success or failure
     */
    public void deleteUserProfileImage(String userId, DeleteCallback callback) {
        StorageReference profileImageRef = storage.getReference()
                .child(PROFILE_IMAGES_PATH + userId);

        profileImageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully deleted profile image for user: " + userId);
                    callback.onSuccess();
                })
                .addOnFailureListener(exception -> {
                    // Image might not exist, which is okay
                    Log.w(TAG, "Could not delete profile image: " + exception.getMessage());
                    callback.onSuccess(); // Still call success since image might not exist
                });
    }

    /**
     * Deletes the user document from Firestore.
     *
     * @param userId The ID of the user to delete
     * @param callback Callback to handle success or failure
     */
    private void deleteUserDocument(String userId, DeleteCallback callback) {
        db.collection(USERS_COLLECTION)
                .document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully deleted user: " + userId);
                    callback.onSuccess();
                })
                .addOnFailureListener(exception -> {
                    String errorMsg = "Failed to delete user: " + exception.getMessage();
                    Log.e(TAG, errorMsg);
                    callback.onFailure(errorMsg);
                });
    }

    /**
     * Removes only the profile image of a user without deleting the user account.
     *
     * @param userId The ID of the user whose profile image to remove
     * @param callback Callback to handle success or failure
     */
    public void removeUserProfileImageOnly(String userId, DeleteCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onFailure("Invalid user ID");
            return;
        }

        deleteUserProfileImage(userId, callback);
    }
}*/