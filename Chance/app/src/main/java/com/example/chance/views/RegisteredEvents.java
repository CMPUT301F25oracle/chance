package com.example.chance.views;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.model.Event;
import com.example.chance.views.base.MultiPurposeEventSearchScreen;

import java.util.List;

/**
 * Fragment that displays the list of events the user has registered for.
 */
public class RegisteredEvents extends MultiPurposeEventSearchScreen {

    /**
     * Filters all events to show only those where the current user is on the waiting list.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            cvm.getEvents().observe(getViewLifecycleOwner(), events -> {
                List<Event> userEvents = events
                        .stream()
                        .filter(event -> event.getWaitingList().contains(user.getID())).toList();
                submitList(userEvents);
            });
        });
    }
}