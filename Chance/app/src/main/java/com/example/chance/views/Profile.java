package com.example.chance.views;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chance.ChanceViewModel;
import com.example.chance.R;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.ProfileBinding;
import com.example.chance.model.User;
import com.example.chance.views.base.ChanceFragment;

import com.example.chance.views.Authentication;
import java.util.Objects;


public class Profile extends ChanceFragment {
    private ProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // now we load the users information from chance state
        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.usernameInput.setText(user.getUsername());
                binding.fullnameInput.setText(user.getFullName());
                binding.emailInput.setText(user.getEmail());
                binding.phoneInput.setText(user.getPhoneNumber());

                binding.logoutButton.setOnClickListener(v -> {
                    logoutUser();
                });

                binding.saveInformationButton.setOnClickListener(v -> {
                    String fullName = binding.fullnameInput.getText().toString();
                    String email = binding.emailInput.getText().toString();
                    String phoneNumber = binding.phoneInput.getText().toString();

                    if (!fullName.trim().isEmpty() && !email.trim().isEmpty() && !phoneNumber.trim().isEmpty()) {
                        user.setFullName(fullName);
                        user.setEmail(email);
                        user.setPhoneNumber(phoneNumber);
                        dsm.updateUser(user, (na)->{
                            Toast.makeText(getContext(), "Information saved successfully", Toast.LENGTH_SHORT).show();
                        });
                    }
                    else {
                        Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    }
                });

                binding.deleteAccountButton.setOnClickListener(v -> {
                    // we redefine to make sure the newest instance is obtained
                    dsm.deleteUser(user.getUsername(), (na)->{});
                    logoutUser();
                });
            }
        });
    }

    public void logoutUser() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("addToBackStack", false);
        dsm.logoutAuthenticatedUser();
        cvm.setLoadMainUI(false);
        cvm.setNewFragment(Authentication.class, bundle, "circular:300");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
