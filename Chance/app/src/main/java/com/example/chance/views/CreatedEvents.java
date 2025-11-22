package com.example.chance.views;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.model.Event;
import com.example.chance.views.base.MultiPurposeEventSearchScreen;

import java.util.List;
import java.util.Objects;

/**
 * Fragment that displays the list of events created by the current user.
 */
public class CreatedEvents extends MultiPurposeEventSearchScreen {


    /**
     * Observes data to filter and display only events organized by the logged-in user.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            cvm.getEvents().observe(getViewLifecycleOwner(), events -> {
                List<Event> userEvents = events
                        .stream()
                        .filter(event -> Objects.equals(user.getID(), event.getOrganizerUID())).toList();
                submitList(userEvents);
            });
        });
    }
}