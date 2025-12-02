package com.example.chance.controller;

import com.example.chance.model.Event;
import com.example.chance.model.User;

public class AdminController {

    public void deleteUser(User user) {
        // In a real app, you'd have logic to delete the user from your database.
        // For this example, we'll just clear the user's data.
        user.setUsername(null);
        user.setEmail(null);
    }

    public void deleteEvent(Event event) {
        // In a real app, you'd have logic to delete the event from your database.
        // For this example, we'll just clear the event's data.
        event.setName(null);
        event.setLocation(null);
    }
}
