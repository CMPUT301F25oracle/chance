package com.example.chance.controller;

import com.example.chance.model.Event;
import com.example.chance.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Handles lottery logic: joining events, drawing winners, and managing waitlists.
 * You will wire Firebase calls later (after Firestore is connected).
 */
public class LotteryController {

    private final EventController eventController;

    public LotteryController() {
        eventController = new EventController();
    }

    /**
     * Adds a user to event waitlist
     */
    public void joinWaitlist(String eventId, User user,
                             OnSuccessListener<Void> onSuccess,
                             OnFailureListener onFailure) {

        eventController.getEvent(eventId, event -> {
            if (event == null) {
                onFailure.onFailure(new Exception("Event not found"));
                return;
            }

            event.getWaitlist().add(user.getId());
            eventController.updateEvent(eventId, event, onSuccess, onFailure);

        }, onFailure);
    }

    /**
     * Runs the draw and selects winners up to event capacity
     */
    public void runLottery(String eventId,
                           OnSuccessListener<Event> onSuccess,
                           OnFailureListener onFailure) {

        eventController.getEvent(eventId, event -> {
            if (event == null) {
                onFailure.onFailure(new Exception("Event not found"));
                return;
            }

            int spots = event.getCapacity() - event.getEnrolled().size();

            if (spots <= 0) {
                onSuccess.onSuccess(event); // already full
                return;
            }

            // Move first N waitlisted users → enrolled list
            for (int i = 0; i < Math.min(spots, event.getWaitlist().size()); i++) {
                String userId = event.getWaitlist().remove(0);
                event.getEnrolled().add(userId);
            }

            eventController.updateEvent(eventId, event, unused -> onSuccess.onSuccess(event), onFailure);

        }, onFailure);
    }
}
