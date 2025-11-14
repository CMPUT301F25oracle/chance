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

public class SplashScreen extends Fragment {
    private SplashScreenBinding binding;
    private DataStoreManager dsm;
    private ChanceViewModel cvm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = SplashScreenBinding.inflate(inflater, container, false);
        dsm = DataStoreManager.getInstance();
        cvm = new ViewModelProvider(requireActivity()).get(ChanceViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (dsm.isDeviceAuthenticated()) {
            dsm.getAuthenticatedUser(user -> {
                cvm.setAuthenticationSuccess(user);
                cvm.setNewFragment(Home.class, null);
                cvm.setLoadMainUI(true);
            }, (e) -> {
                cvm.setNewFragment(Authentication.class, null);
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
