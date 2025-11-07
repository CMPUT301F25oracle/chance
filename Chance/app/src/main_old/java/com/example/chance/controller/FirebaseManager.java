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

/**
 * FirebaseManager centralizes all Firestore and Storage operations.
 * Other controllers interact with Firebase through this class.
 */
public class FirebaseManager {

    private static final String TAG = "FirebaseManager";
    private static FirebaseManager instance;

    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    // -------------------- Singleton --------------------
    private FirebaseManager() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // -------------------- Firestore CRUD --------------------

    /**
     * Add a new document to a collection with auto-generated ID.
     */
    public <T> void addDocument(String collectionName,
                                T object,
                                OnSuccessListener<DocumentReference> onSuccess,
                                OnFailureListener onFailure) {
        db.collection(collectionName)
                .add(object)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Overwrite or create a document with a custom ID.
     */
    public <T> void setDocument(String collectionName,
                                String documentId,
                                T object,
                                OnSuccessListener<Void> onSuccess,
                                OnFailureListener onFailure) {
        db.collection(collectionName)
                .document(documentId)
                .set(object)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Retrieve a specific document by ID.
     */
    public void getDocument(String collectionName,
                            String documentId,
                            OnSuccessListener<DocumentSnapshot> onSuccess,
                            OnFailureListener onFailure) {
        db.collection(collectionName)
                .document(documentId)
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Delete a document from Firestore.
     */
    public void deleteDocument(String collectionName,
                               String documentId,
                               OnSuccessListener<Void> onSuccess,
                               OnFailureListener onFailure) {
        db.collection(collectionName)
                .document(documentId)
                .delete()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Attach a real-time listener to a Firestore collection.
     */
    public void listenToCollection(String collectionName,
                                   EventListener<QuerySnapshot> listener) {
        db.collection(collectionName)
                .addSnapshotListener(listener);
    }

    // -------------------- Storage Operations --------------------

    /**
     * Upload a file (in bytes) to Firebase Storage.
     */
    public void uploadFile(String path,
                           byte[] fileData,
                           OnSuccessListener onSuccess,
                           OnFailureListener onFailure) {
        storage.getReference(path)
                .putBytes(fileData)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Get a StorageReference for a given path.
     */
    public StorageReference getStorageReference(String path) {
        return storage.getReference(path);
    }

    // -------------------- Utility + Accessors --------------------

    /**
     * Expose Firestore instance for custom queries.
     */
    public FirebaseFirestore getDb() {
        return db;
    }

    public void logError(String message, Exception e) {
        Log.e(TAG, message, e);
    }

    public void logInfo(String message) {
        Log.i(TAG, message);
    }
}
