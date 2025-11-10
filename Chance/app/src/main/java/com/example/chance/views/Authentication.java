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
import com.example.chance.R;
import com.example.chance.controller.ChanceState;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.AuthenticationBinding;
import com.example.chance.model.User;

import java.util.Objects;

public class Authentication extends Fragment {

    private AuthenticationBinding binding;
    private DataStoreManager dsm;
    private ChanceViewModel cvm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = AuthenticationBinding.inflate(inflater, container, false);
        dsm = DataStoreManager.getInstance();
        cvm = new ViewModelProvider(requireActivity()).get(ChanceViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.signUpButton.setOnClickListener(v -> {
            String username = binding.username.getText().toString();
            String password = binding.password.getText().toString();
            dsm.getUser(username, (user) -> {
                if (user != null) {
                    binding.errorMessage.setVisibility(VISIBLE);
                    binding.errorMessage.setText("Account username already taken");

                } else {
                    User new_user = dsm.createUser(username, password);
                    ChanceState.getInstance().setUser(new_user);
                    navigateToHome();
                }
            });
        });

         binding.loginButton.setOnClickListener(v -> {
             // first we grab the credentials
             String username = binding.username.getText().toString();
             String password = binding.password.getText().toString();
             dsm.getUser(username, (user) -> {
                 if (user == null || !Objects.equals(user.getPassword(), password)) {
                     binding.errorMessage.setVisibility(VISIBLE);
                     binding.errorMessage.setText("Username or Password was invalid");
                 } else {
                     ChanceState.getInstance().setUser(user);
                     navigateToHome();
                 }
             });
         });
    }

    private void navigateToHome() {
        cvm.setNewFragment(Home.class, null);
        cvm.setLoadMainUI(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}