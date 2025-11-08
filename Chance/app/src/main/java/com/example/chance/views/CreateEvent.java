package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chance.R;
import com.example.chance.controller.ChanceState;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.CreateEventBinding;
import com.example.chance.model.Event;
import com.example.chance.model.User;

import java.util.Calendar;
import java.util.Date;

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
            Calendar calendar = Calendar.getInstance();

            String event_name = binding.eventNameInput.getText().toString();
            String event_address = binding.eventAddressInput.getText().toString();

            DatePicker event_reg_start = binding.registrationStartInput;
            calendar.set(event_reg_start.getYear(), event_reg_start.getMonth(), event_reg_start.getDayOfMonth());
            Date event_start_calendar = calendar.getTime();

            DatePicker event_reg_end = binding.registrationEndInput;
            calendar.set(event_reg_end.getYear(), event_reg_end.getMonth(), event_reg_end.getDayOfMonth());
            Date event_end_calendar = calendar.getTime();

            int maximum_candidates = Integer.parseInt(binding.candidateMaximumInput.getText().toString());
            String event_description = binding.descriptionInput.getText().toString();

            Event new_event = DataStoreManager.getInstance().createEvent(event_name, event_address, maximum_candidates, 0, event_description, event_start_calendar, event_end_calendar, user.getUsername());

        });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}