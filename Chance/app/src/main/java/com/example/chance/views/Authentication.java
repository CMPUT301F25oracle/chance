package com.example.chance.views;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chance.ChanceViewModel;
import com.example.chance.R;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.AuthenticationBinding;
import com.example.chance.databinding.ChanceTextInputBinding;
import com.example.chance.model.User;
import com.example.chance.views.base.ChanceFragment;

import java.util.Objects;

/**
 * Fragment managing user login and sign-up processes.
 */
public class Authentication extends ChanceFragment {

    private AuthenticationBinding binding;

    /**
     * Inflates the authentication screen layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = AuthenticationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Sets up click listeners for sign-up and login buttons to handle user credentials.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.signUpButton.setOnClickListener(v -> {
            String username = binding.username.getText();
            String password = binding.password.getText();
            dsm.createNewUser(username, password, this::userAuthenticated, (e)->{
                binding.errorMessage.setVisibility(VISIBLE);
                binding.errorMessage.setText("There was a problem creating the new user");
            });
        });

        binding.loginButton.setOnClickListener(v -> {
            // first we grab the credentials
            String username = binding.username.getText();
            String password = binding.password.getText();
            dsm.authenticateUser(username, password, this::userAuthenticated,(e)->{
                binding.errorMessage.setVisibility(VISIBLE);
                binding.errorMessage.setText("There was a problem loggin in");
            });
        });
    }

    /**
     * Handles successful authentication by updating state and navigating to the Home screen.
     */
    private void userAuthenticated(User user) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("addToBackStack", false);
        dsm.getAuthenticatedUser(authenticatedUser -> {
            cvm.setAuthenticationSuccess(authenticatedUser);
            cvm.setNewFragment(Home.class, bundle, "");
            cvm.setLoadMainUI(true);
        }, (e) -> {
            cvm.setNewFragment(Authentication.class, bundle, "");
        });
    }

    /**
     * Cleans up binding references when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}