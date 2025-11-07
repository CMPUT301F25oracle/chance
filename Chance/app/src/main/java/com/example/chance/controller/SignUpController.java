//package com.example.chance.controller;
//
//import com.example.chance.model.SignUp;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//
///**
// * SignUpController handles user registration and role setup.
// * It stores and retrieves SignUp data from Firestore.
// */
//public class SignUpController {
//
//    private static final String COLLECTION = "signups";
//    private final FirebaseManager firebaseManager;
//
//    public SignUpController() {
//        firebaseManager = FirebaseManager.getInstance();
//    }
//
//    // --- Register New User ---
//    public void registerUser(SignUp signUp, OnSuccessListener<DocumentReference> onSuccess, OnFailureListener onFailure) {
//        firebaseManager.addDocument(COLLECTION, signUp, onSuccess, onFailure);
//    }
//
//    // --- Retrieve User by ID ---
//    public void getUser(String userId, OnSuccessListener<SignUp> onSuccess, OnFailureListener onFailure) {
//        firebaseManager.getDocument(COLLECTION, userId,
//                document -> {
//                    if (document.exists()) {
//                        SignUp user = document.toObject(SignUp.class);
//                        onSuccess.onSuccess(user);
//                    } else {
//                        onFailure.onFailure(new Exception("User not found"));
//                    }
//                },
//                onFailure);
//    }
//
//    // --- Update User Info ---
//    public void updateUser(String userId, SignUp updatedSignUp, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
//        firebaseManager.setDocument(COLLECTION, userId, updatedSignUp, onSuccess, onFailure);
//    }
//
//    // --- Delete User ---
//    public void deleteUser(String userId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
//        firebaseManager.deleteDocument(COLLECTION, userId, onSuccess, onFailure);
//    }
//
//    // --- Verify Account ---
//    public void verifyUser(String userId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
//        firebaseManager.getDocument(COLLECTION, userId,
//                document -> {
//                    if (document.exists()) {
//                        SignUp signUp = document.toObject(SignUp.class);
//                        if (signUp != null) {
//                            signUp.setVerified(true);
//                            firebaseManager.setDocument(COLLECTION, userId, signUp, onSuccess, onFailure);
//                        }
//                    } else {
//                        onFailure.onFailure(new Exception("User not found for verification"));
//                    }
//                },
//                onFailure);
//    }
//}
