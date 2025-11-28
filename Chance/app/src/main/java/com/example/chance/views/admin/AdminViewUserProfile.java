package com.example.chance.views.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chance.model.Notification;
import java.util.Comparator;

import com.example.chance.adapters.NotificationPopupAdapter;
import com.example.chance.databinding.AdminViewUserProfileBinding;
import com.example.chance.model.Notification;
import com.example.chance.model.User;
import com.example.chance.views.base.ChanceFragment;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.Comparator;

public class AdminViewUserProfile extends ChanceFragment {
    private AdminViewUserProfileBinding binding;
    private String userID;

    // NEW: adapter for the notifications list
    private NotificationPopupAdapter notificationAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = AdminViewUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userID = getArguments().getString("userID");

        // --- setup notifications RecyclerView (same as NotificationPopup) ---
        RecyclerView notificationsContainer = binding.notificationsRecyclerView; // id: notifications_recycler_view
        notificationAdapter = new NotificationPopupAdapter();
        notificationsContainer.setAdapter(notificationAdapter);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        notificationsContainer.setLayoutManager(layoutManager);
        // ---------------------------------------------------------------------

        // Fetch profile
        dsm.getUser(userID, user -> {
            binding.usernameInput.setText(user.getUsername());
            binding.fullnameInput.setText(user.getFullName());
            binding.emailInput.setText(user.getEmail());
            binding.phoneInput.setText(user.getPhoneNumber());

            // Fetch notifications for THIS user (same logic as NotificationPopup)
            dsm.user(user).getNotifications(notificationList -> {
                notificationList.sort(new Comparator<Notification>() {
                    @Override
                    public int compare(Notification n1, Notification n2) {
                        // newest first
                        return Math.toIntExact(
                                n2.getCreationDate().getTime() - n1.getCreationDate().getTime()
                        );
                    }
                });
                notificationAdapter.submitList(notificationList);
            }, __ -> {});

        });
    }
}
