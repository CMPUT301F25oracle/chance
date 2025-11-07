package com.example.chance.views;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.chance.R;
import com.example.chance.controller.ChanceState;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.controller.FirebaseManager;
import com.example.chance.databinding.LoginBinding;

import java.util.Objects;

public class Login extends Fragment {

    private LoginBinding binding;
    private DataStoreManager dsm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = LoginBinding.inflate(inflater, container, false);
        dsm = DataStoreManager.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

         binding.loginButton.setOnClickListener(v -> {
             // first we grab the credentials
             String username = binding.username.getText().toString();
             String password = binding.password.getText().toString();
             dsm.getUser(username, (user) -> {
                 if (user != null) {
                     if (!Objects.equals(user.getUsername(), username)) {
                         return;
                     }
                     if (!Objects.equals(user.getPassword(), password)) {
                         return;
                     }
                     // otherwise we can authenticate the user
                     ChanceState.getInstance().setUser(user);
                     navigateToHome();
                 }
             });
         });
    }

    private void navigateToHome() {
        // we're logged in / signed up now, so we can show title and nav bar again
        requireActivity().findViewById(R.id.title_bar).setVisibility(VISIBLE);
        requireActivity().findViewById(R.id.nav_bar).setVisibility(VISIBLE);
        // Replace Login fragment with Home fragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_view, new Home())
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}