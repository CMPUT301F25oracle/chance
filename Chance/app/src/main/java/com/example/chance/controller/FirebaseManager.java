package com.example.chance.controller;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/**
 * FirebaseManager is responsible for managing all Firestore and Storage operations.
 * Other controllers call its methods instead of directly using Firebase APIs.
 */
public class FirebaseManager {

    private static final String TAG = "FirebaseManager";
    private static FirebaseManager instance;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    private FirebaseManager() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // Singleton pattern — ensures only one FirebaseManager exists
    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // --- Firestore CRUD Methods ---

    public <T> void addDocument(String collectionName, T object, OnSuccessListener<DocumentReference> onSuccess, OnFailureListener onFailure) {
        db.collection(collectionName)
                .add(object)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public <T> void setDocument(String collectionName, String documentId, T object, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection(collectionName)
                .document(documentId)
                .set(object)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void getDocument(String collectionName, String documentId, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        db.collection(collectionName)
                .document(documentId)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void deleteDocument(String collectionName, String documentId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        db.collection(collectionName)
                .document(documentId)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public void listenToCollection(String collectionName, EventListener<QuerySnapshot> listener) {
        db.collection(collectionName).addSnapshotListener(listener);
    }

    // --- Storage Methods (for event posters or profile images) ---
    public StorageReference getStorageReference(String path) {
        return storage.getReference(path);
    }

    public void uploadFile(String path, byte[] fileData, OnSuccessListener success, OnFailureListener failure) {
        storage.getReference(path)
                .putBytes(fileData)
                .addOnSuccessListener(success)
                .addOnFailureListener(failure);
    }

    // --- Utility ---
    public void logError(String message, Exception e) {
        Log.e(TAG, message, e);
    }

    public void logInfo(String message) {
        Log.i(TAG, message);
    }
}
