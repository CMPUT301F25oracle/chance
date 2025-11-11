package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chance.ChanceViewModel;
import com.example.chance.R;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.CreateEventBinding;
import com.example.chance.model.Event;
import com.example.chance.model.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateEvent extends Fragment {

    private CreateEventBinding binding;
    private DataStoreManager dsm;
    private ChanceViewModel cvm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = CreateEventBinding.inflate(inflater, container, false);
        dsm = DataStoreManager.getInstance();
        cvm = new ViewModelProvider(requireActivity()).get(ChanceViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            binding.submitButton.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();

                String event_name = binding.eventNameInput.getText().toString();
                String event_address = binding.eventAddressInput.getText().toString();
                int maximum_candidates = Integer.parseInt(binding.candidateMaximumInput.getText().toString());
                float attendance_price = Float.parseFloat(binding.priceInput.getText().toString());

                DatePicker event_reg_start = binding.registrationStartInput;
                calendar.set(event_reg_start.getYear(), event_reg_start.getMonth(), event_reg_start.getDayOfMonth());
                Date event_start_calendar = calendar.getTime();

                DatePicker event_reg_end = binding.registrationEndInput;
                calendar.set(event_reg_end.getYear(), event_reg_end.getMonth(), event_reg_end.getDayOfMonth());
                Date event_end_calendar = calendar.getTime();

                String event_description = binding.descriptionInput.getText().toString();

                Event new_event = new Event(event_name, event_address, maximum_candidates, attendance_price, event_description, event_start_calendar, event_end_calendar, user.getID());
                dsm.createNewEvent(new_event, (event) -> {
                    // now we add the new event to our internal list of events
                    List<Event> events = cvm.getEvents().getValue();
                    events.add(event);
                    cvm.setEvents(events);
                    cvm.setNewFragment(Home.class, null);
                    }, (__)->{});
            });
        });




    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}