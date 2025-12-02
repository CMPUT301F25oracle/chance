package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.databinding.InvitedToEventPopupBinding;
import com.example.chance.databinding.NotSelectedForEventPopupBinding;
import com.example.chance.views.base.ChancePopup;

public class NotSelectedForEventPopup extends ChancePopup {
    private NotSelectedForEventPopupBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = NotSelectedForEventPopupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String eventID = meta.getString("eventID");

        dsm.getEvent(eventID, event -> {
            binding.title.setText(event.getName());
            binding.description.setText(event.getDescription());
            dsm.getEventBannerFromID(event.getID(), imageBitmap -> {
                binding.banner.setImageBitmap(imageBitmap);
            }, __ -> {});
        });
    }
}

/**
 * ==================== NotSelectedForEventPopup.java Comments ====================
 *
 * This file defines the NotSelectedForEventPopup class, a custom dialog fragment
 * that extends the base ChancePopup. Its purpose is to inform a user that they
 * were not selected from the waiting list (lottery) for a particular event.
 *
 * === NotSelectedForEventPopup Class ===
 * A DialogFragment that displays a summary of an event to a user who did not win
 * the lottery. The popup shows the event's name, description, and banner to
 * remind the user which event the notification is for. This popup is purely
 * informational.
 *
 * --- onCreateView Method ---
 * This standard lifecycle method inflates the popup's layout using
 * `NotSelectedForEventPopupBinding`, which is the auto-generated view binding class
 * for the `not_selected_for_event_popup.xml` layout. It sets up the root view of
 * the fragment.
 *
 * --- onViewCreated Method ---
 * This method is called after the view is created. It retrieves the event's ID
 * from the `meta` Bundle (a feature provided by the base ChancePopup class). It then uses
 * the DataStoreManager (`dsm`) to asynchronously fetch the full event details.
 * Upon successful retrieval, it populates the UI by setting the event's name (`title`),
 * description, and banner image.
 */