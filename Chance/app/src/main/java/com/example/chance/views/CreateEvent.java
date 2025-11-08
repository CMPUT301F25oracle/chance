package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chance.R;
import com.example.chance.controller.ChanceState;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.CreateEventBinding;
import com.example.chance.model.Event;
import com.example.chance.model.User;

public class CreateEvent extends Fragment {

    private CreateEventBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = CreateEventBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = ChanceState.getInstance().getUser();

        binding.submitButton.setOnClickListener(v -> {
            String event_name = binding.eventNameInput.getText().toString();
            String event_address = binding.eventAddressInput.getText().toString();
            String event_registration_start = binding.registrationStartInput.getText().toString();
            String event_registration_end = binding.registrationEndInput.getText().toString();
            int maximum_candidates = Integer.parseInt(binding.candidateMaximumInput.getText().toString());
            String event_description = binding.descriptionInput.getText().toString();

            Event new_event = DataStoreManager.getInstance().createEvent(event_name, event_address, maximum_candidates, 0, event_description, null);

        });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}