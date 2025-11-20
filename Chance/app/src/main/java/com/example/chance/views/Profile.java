package com.example.chance.views;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
        //DataStoreManager dsm = DataStoreManager.getInstance();
        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.usernameInput.setText(user.getUsername());
                binding.fullnameInput.setText(user.getFullName());
                binding.emailInput.setText(user.getEmail());
                binding.phoneInput.setText(user.getPhoneNumber());


                binding.logoutButton.setOnClickListener(v -> {
                    logoutUser();

                });
                // now we setup our listeners
                binding.deleteAccountButton.setOnClickListener(v -> {
                    // we redefine to make sure the newest instance is obtained
                    dsm.deleteUser(user.getUsername(), (na)->{});
                    logoutUser();
                });

                //binding.profileImage.setImageURI(pfp_uri);
                binding.usernameInput.setText(user.getUsername());
                binding.fullnameInput.setText(user.getFullName());
                binding.emailInput.setText(user.getEmail());
                binding.phoneInput.setText(user.getPhoneNumber());
                //region: callback actions
                binding.fullnameInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        // nothing needed here for now
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // nothing needed here for now
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // grab the full new name
                        String new_full_name = binding.fullnameInput.getText().toString();
                        user.setFullName(new_full_name);
                        dsm.updateUser(user, (v) -> {});
                    }
                });
                binding.emailInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        // nothing needed here for now
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // nothing needed here for now
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // grab the full new name
                        String new_email = binding.emailInput.getText().toString();
                        user.setEmail(new_email);
                        dsm.updateUser(user, (v) -> {});
                    }
                });
                binding.phoneInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        // nothing needed here for now
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // nothing needed here for now
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String new_phone_number = binding.phoneInput.getText().toString();
                        user.setPhoneNumber(new_phone_number);
                        dsm.updateUser(user, (v) -> {});
                    }
                });

                binding.deleteAccountButton.setOnClickListener(v -> {
                    // we redefine to make sure the newest instance is obtained
                    dsm.deleteUser(user.getUsername(), (na)->{});
                    cvm.setNewFragment(Authentication.class, null, "");
                });

                //endregion: callback actions
            }
        });


    }

    public void logoutUser() {
        dsm.logoutAuthenticatedUser();
        cvm.setLoadMainUI(false);
        cvm.setNewFragment(Authentication.class, null, "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
