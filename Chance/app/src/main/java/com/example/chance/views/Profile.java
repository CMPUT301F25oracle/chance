package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chance.controller.ChanceState;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.LoginBinding;
import com.example.chance.databinding.ProfileBinding;
import com.example.chance.model.User;

import java.util.Objects;


public class Profile extends Fragment {
    private ProfileBinding binding;
    private DataStoreManager dsm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ProfileBinding.inflate(inflater, container, false);
        dsm = DataStoreManager.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // now we load the users information from chance state
        User user = ChanceState.getInstance().getUser();
        binding.usernameInput.setText(user.getUsername());
        binding.fullnameInput.setText("---TBA---");
        binding.emailInput.setText(user.getEmail());
        binding.phoneInput.setText(user.getPhoneNumber());


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
