package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.adapters.MultiPurposeEventSearchScreenListAdapter;
import com.example.chance.adapters.NotificationPopupAdapter;
import com.example.chance.databinding.NotificationPopupBinding;
import com.example.chance.views.base.ChancePopup;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

/**
 * Popup fragment for displaying user notifications.
 */
public class NotificationPopup extends ChancePopup {

    private NotificationPopupBinding binding;
    private NotificationPopupAdapter notificationsAdapter;

    /**
     * Inflates the notification popup layout using ViewBinding.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = NotificationPopupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Initializes the popup view components.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView notificationsContainer = binding.notificationsContainer;
        notificationsAdapter = new NotificationPopupAdapter();
        notificationsContainer.setAdapter(notificationsAdapter);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        notificationsContainer.setLayoutManager(layoutManager);

        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            dsm.user(user).getNotifications(notificationList->{
                notificationsAdapter.submitList(notificationList);
            }, __ -> {});
        });
    }
}