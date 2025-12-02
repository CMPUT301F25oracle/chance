package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.databinding.CustomEventNotificationPopupBinding;
import com.example.chance.databinding.InvitedToEventPopupBinding;
import com.example.chance.model.User;
import com.example.chance.views.base.ChancePopup;

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
        List<String> waitlistUserIDS = meta.getStringArrayList("waitlistUserIDS");
        binding.sendNotificationButton.setOnClickListener(v -> {
            String message = binding.notificationContents.getText().toString();
            for (String userID : waitlistUserIDS) {
                //dsm.user(new User(userID)).sendNotification(message);
            }
        });


    }
}
