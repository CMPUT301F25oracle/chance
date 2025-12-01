package com.example.chance.views.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

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

        // Get the user ID from the bundle (passed from AdminViewUsers)
        String userId = meta.getString("userId");

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(requireContext(), "Error: No user ID provided", Toast.LENGTH_SHORT).show();
            cvm.setNewFragment(AdminViewUsers.class, null, "fade");
            return;
        }

        // Setup notifications RecyclerView
        RecyclerView notificationsContainer = binding.notificationsRecyclerView;
        notificationAdapter = new NotificationPopupAdapter();
        notificationsContainer.setAdapter(notificationAdapter);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        notificationsContainer.setLayoutManager(layoutManager);

        // Fetch user by UID (not username)
        dsm.getUserFromUID(userId, user -> {
            if (user == null) {
                Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show();
                cvm.setNewFragment(AdminViewUsers.class, null, "fade");
                return;
            }

            // Display user information
            binding.usernameInput.setText(user.getUsername() != null ? user.getUsername() : "N/A");
            binding.fullnameInput.setText(user.getFullName() != null ? user.getFullName() : "N/A");
            binding.emailInput.setText(user.getEmail() != null ? user.getEmail() : "N/A");
            binding.phoneInput.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A");

            // Fetch notifications for this user
            dsm.user(user).getNotifications(notificationList -> {
                if (notificationList != null && !notificationList.isEmpty()) {
                    notificationList.sort(new Comparator<Notification>() {
                        @Override
                        public int compare(Notification n1, Notification n2) {
                            return Math.toIntExact(
                                    n2.getCreationDate().getTime() - n1.getCreationDate().getTime()
                            );
                        }
                    });
                    notificationAdapter.submitList(notificationList);
                }
            }, error -> {
                // Error loading notifications - just show empty list
            });

            // DELETE USER BUTTON
            binding.deleteUserButton.setOnClickListener(v -> {
                // Show confirmation dialog before deleting
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to permanently delete this user and all their data?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            deleteUserAndEvents(user);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

        }, error -> {
            Toast.makeText(requireContext(), "Error loading user profile", Toast.LENGTH_SHORT).show();
            cvm.setNewFragment(AdminViewUsers.class, null, "fade");
        });
    }

    /**
     * Deletes the user and all events they created
     */
    private void deleteUserAndEvents(User user) {
        // First, delete all notifications for the user
        dsm.user(user).deleteAllNotifications(unused -> {
            // Then, get all events created by this user
            dsm.getAllEvents(events -> {
                // Filter events created by this user
                for (com.example.chance.model.Event event : events) {
                    if (event.getOrganizerUID().equals(user.getID())) {
                        // Delete the event and its banner
                        dsm.removeEvent(event, unused2 -> {}, error -> {});
                        dsm.deleteEventBanner(event.getID(), unused3 -> {}, error -> {});
                    }
                }

                // Finally, delete the user
                dsm.deleteUser(user.getID(), unused4 -> {
                    Toast.makeText(requireContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                    cvm.setNewFragment(AdminViewUsers.class, null, "fade");
                });
            });
        }, error -> {
            // Handle error while deleting notifications
            Toast.makeText(requireContext(), "Error deleting user's notifications", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}