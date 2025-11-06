package com.example.chance.controller;

import com.example.chance.model.Entrant;
import com.example.chance.model.Organizer;
import com.example.chance.model.Administrator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * ProfileController manages all user profile operations:
 * retrieving, updating, and deleting profiles from Firestore.
 */
public class ProfileController {

    private static final String ENTRANT_COLLECTION = "entrants";
    private static final String ORGANIZER_COLLECTION = "organizers";
    private static final String ADMIN_COLLECTION = "administrators";

    private final FirebaseManager firebaseManager;

    public ProfileController() {
        firebaseManager = FirebaseManager.getInstance();
    }

    // ----------------- Entrant Operations -----------------

    public void getEntrant(String entrantId, OnSuccessListener<Entrant> onSuccess, OnFailureListener onFailure) {
        firebaseManager.getDocument(ENTRANT_COLLECTION, entrantId,
                document -> {
                    if (document.exists()) {
                        Entrant entrant = document.toObject(Entrant.class);
                        onSuccess.onSuccess(entrant);
                    } else {
                        onFailure.onFailure(new Exception("Entrant not found"));
                    }
                },
                onFailure);
    }

    public void updateEntrant(String entrantId, Entrant entrant, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firebaseManager.setDocument(ENTRANT_COLLECTION, entrantId, entrant, onSuccess, onFailure);
    }

    public void deleteEntrant(String entrantId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firebaseManager.deleteDocument(ENTRANT_COLLECTION, entrantId, onSuccess, onFailure);
    }

    // ----------------- Organizer Operations -----------------

    public void getOrganizer(String organizerId, OnSuccessListener<Organizer> onSuccess, OnFailureListener onFailure) {
        firebaseManager.getDocument(ORGANIZER_COLLECTION, organizerId,
                document -> {
                    if (document.exists()) {
                        Organizer organizer = document.toObject(Organizer.class);
                        onSuccess.onSuccess(organizer);
                    } else {
                        onFailure.onFailure(new Exception("Organizer not found"));
                    }
                },
                onFailure);
    }

    public void updateOrganizer(String organizerId, Organizer organizer, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firebaseManager.setDocument(ORGANIZER_COLLECTION, organizerId, organizer, onSuccess, onFailure);
    }

    public void deleteOrganizer(String organizerId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firebaseManager.deleteDocument(ORGANIZER_COLLECTION, organizerId, onSuccess, onFailure);
    }

    // ----------------- Administrator Operations -----------------

    public void getAdministrator(String adminId, OnSuccessListener<Administrator> onSuccess, OnFailureListener onFailure) {
        firebaseManager.getDocument(ADMIN_COLLECTION, adminId,
                document -> {
                    if (document.exists()) {
                        Administrator admin = document.toObject(Administrator.class);
                        onSuccess.onSuccess(admin);
                    } else {
                        onFailure.onFailure(new Exception("Administrator not found"));
                    }
                },
                onFailure);
    }

    public void updateAdministrator(String adminId, Administrator admin, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firebaseManager.setDocument(ADMIN_COLLECTION, adminId, admin, onSuccess, onFailure);
    }

    public void deleteAdministrator(String adminId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        firebaseManager.deleteDocument(ADMIN_COLLECTION, adminId, onSuccess, onFailure);
    }
}
