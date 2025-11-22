package com.example.chance.views.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.databinding.AdminBinding;
import com.example.chance.views.base.ChanceFragment;

/**
 * Fragment representing the main Admin dashboard.
 * Provides navigation to admin-specific features like browsing profiles.
 */
public class Admin extends ChanceFragment {
    private AdminBinding binding;

    /**
     * Inflates the layout for the Admin fragment using ViewBinding.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = AdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Initializes UI listeners and handles navigation button clicks.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.browseProfilesButton.setOnClickListener(v -> {
            cvm.setNewFragment(AdminViewUsers.class, null, "fade");
        });

    }
}