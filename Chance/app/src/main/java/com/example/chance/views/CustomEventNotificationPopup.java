package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.databinding.CustomEventNotificationPopupBinding;
import com.example.chance.databinding.InvitedToEventPopupBinding;
import com.example.chance.model.Notification;
import com.example.chance.model.User;
import com.example.chance.views.base.ChancePopup;

import java.util.HashMap;
import java.util.List;

public class CustomEventNotificationPopup extends ChancePopup {
    private CustomEventNotificationPopupBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CustomEventNotificationPopupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String eventName = meta.getString("eventName");
        List<String> waitlistIDS = meta.getStringArrayList("waitlistIDS");
        Notification notification = new Notification();
        notification.setType(2);
        HashMap<String, String> meta = new HashMap<>();
        notification.setMeta(meta);
        meta.put("title", "New notification from " + eventName);
        binding.sendNotificationButton.setOnClickListener(v -> {
            String message = binding.notificationContents.getText().toString();
            meta.put("description", message);
            for (String userID : waitlistIDS) {
                User tempUser = new User();
                tempUser.setID(userID);
                dsm.user(tempUser).postNotification(notification, __ -> {}, __ -> {});
            }
        });


    }
}
