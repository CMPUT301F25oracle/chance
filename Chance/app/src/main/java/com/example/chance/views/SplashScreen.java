package com.example.chance.views;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chance.ChanceViewModel;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.SplashScreenBinding;
import com.example.chance.model.User;
import com.example.chance.views.base.ChanceFragment;

/**
 * Initial entry point fragment.
 * Routes the user to Home or Authentication based on login status.
 */
public class SplashScreen extends ChanceFragment {
    private SplashScreenBinding binding;

    /**
     * Inflates the splash screen layout using ViewBinding.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = SplashScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Checks authentication status and navigates to Home or Authentication accordingly.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = new Bundle();
        bundle.putBoolean("addToBackStack", false);

        if (dsm.isDeviceAuthenticated()) {
            dsm.getAuthenticatedUser(user -> {
                cvm.setAuthenticationSuccess(user);
                cvm.setNewFragment(Home.class, bundle, "");
                cvm.setLoadMainUI(true);
            }, (e) -> {
                cvm.setNewFragment(Authentication.class, bundle, "");
            });
        } else {
            cvm.setNewFragment(Authentication.class, bundle, "");
        }
    }

    /**
     * Cleans up view binding when the fragment is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}