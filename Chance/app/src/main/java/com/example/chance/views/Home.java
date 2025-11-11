package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.ChanceViewModel;
import com.example.chance.R;
import com.example.chance.adapters.EventListAdapter;
import com.example.chance.controller.ChanceState;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.HomeBinding;
import com.example.chance.model.Event;
import com.example.chance.model.User;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

public class Home extends Fragment {

    private HomeBinding binding;
    private DataStoreManager dsm;
    private ChanceViewModel cvm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = HomeBinding.inflate(inflater, container, false);
        dsm = DataStoreManager.getInstance();
        cvm = new ViewModelProvider(requireActivity()).get(ChanceViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Observe the current user LiveData so UI updates when user is available.
        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                // No user yet: show placeholder, loading state, or navigate to auth
                binding.homeSystemMessage.setText("Hello, ...");
                return;
            }
            // Update UI once we have a user
            binding.homeSystemMessage.setText("Hello, " + user.getUsername());
        });

        binding.buttonCreateEvent.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_view, new CreateEvent())
                    .commit();
        });
        
        // now we set up our event adapter
        RecyclerView eventsContainer = binding.eventsContainer;
        EventListAdapter eventsAdapter = new EventListAdapter();
        eventsContainer.setAdapter(eventsAdapter);

        // next we make sure flexbox is configured on the recyclerview
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.COLUMN);
        layoutManager.setJustifyContent(JustifyContent.FLEX_END);
        eventsContainer.setLayoutManager(layoutManager);

        // now we load the event data (if there is any)
        cvm.getEvents().observe(getViewLifecycleOwner(), events -> {
            eventsAdapter.submitList(events);
        });

        eventsContainer.addOnItemTouchListener(new androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView viewManager, @NonNull MotionEvent touchEvent) {
                View eventPill = viewManager.findChildViewUnder(touchEvent.getX(), touchEvent.getY());
                if (eventPill != null) {
                    if (touchEvent.getAction() == MotionEvent.ACTION_UP) {
                        // we only act when the user lifts their thumb to give more
                        // "natural" feedback
                        String eventId = (String) eventPill.getTag();
                        cvm.requestOpenEvent(eventId);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}