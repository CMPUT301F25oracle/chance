package com.example.chance.views;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.model.Event;

import java.util.List;
import java.util.Objects;

public class RegisteredEvents extends MultiPurposeEventSearchScreen {
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
