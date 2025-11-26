package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.databinding.NotificationPopupBinding;
import com.example.chance.views.base.ChancePopup;

/**
 * Popup fragment for displaying user notifications.
 */
public class NotificationPopup extends ChancePopup {

    private NotificationPopupBinding binding;

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
    }
}