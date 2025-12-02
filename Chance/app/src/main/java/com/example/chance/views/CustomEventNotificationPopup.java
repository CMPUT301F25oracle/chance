package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.databinding.CustomEventNotificationPopupBinding;
import com.example.chance.databinding.InvitedToEventPopupBinding;
import com.example.chance.views.base.ChancePopup;

public class CustomEventNotificationPopup extends ChancePopup {
    private CustomEventNotificationPopupBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CustomEventNotificationPopupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
