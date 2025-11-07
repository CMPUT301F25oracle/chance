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
import com.example.chance.databinding.LoginBinding;

public class Login extends Fragment {

    private LoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = LoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Example: Setup login button click listener
         binding.loginButton.setOnClickListener(v -> {
             // Validate credentials
             // If successful, navigate to home
             navigateToHome();
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