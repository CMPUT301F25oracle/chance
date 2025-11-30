package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.databinding.InvitedToEventPopupBinding;
import com.example.chance.views.base.ChancePopup;

public class InvitedToEventPopup  extends ChancePopup {

    private InvitedToEventPopupBinding binding;

    /**
     * Inflates the notification popup layout using ViewBinding.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = InvitedToEventPopupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String eventID = meta.getString("eventID");
        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            dsm.getEvent(eventID, event -> {
                binding.title.setText(event.getName());
                binding.description.setText(event.getDescription());
                dsm.getEventBannerFromID(event.getID(), imageBitmap -> {
                    binding.banner.setImageBitmap(imageBitmap);
                }, __ -> {});

                binding.declineButton.setOnClickListener(v -> {
                    dsm.event(event).declinedInvite(user);
                    cvm.removePopup();
                });
                binding.acceptButton.setOnClickListener(v -> {
                    dsm.event(event).acceptedInvite(user);
                    cvm.removePopup();
                });
            });
        });

    }
}
