package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.ChanceViewModel;
import com.example.chance.adapters.MainEventSearchListAdapter;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.HomeBinding;
import com.example.chance.model.Event;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

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

        binding.buttonEventCreated.setOnClickListener(__ -> {
            cvm.setNewFragment(MultiPurposeEventSearchScreen.class, null);
        });
        binding.buttonCreateEvent.setOnClickListener(__ -> {
            cvm.setNewFragment(CreateEvent.class, null);
        });
        
        // now we set up our event adapter
        RecyclerView eventsContainerLeft = binding.eventsContainerLeft;
        RecyclerView eventsContainerRight = binding.eventsContainerRight;
        MainEventSearchListAdapter eventsAdapterLeft = new MainEventSearchListAdapter();
        MainEventSearchListAdapter eventsAdapterRight = new MainEventSearchListAdapter();
        eventsContainerLeft.setAdapter(eventsAdapterLeft);
        eventsContainerRight.setAdapter(eventsAdapterRight);

        // next we make sure flexbox is configured on the recyclerview
        FlexboxLayoutManager layoutManagerLeft = new FlexboxLayoutManager(getContext());
        layoutManagerLeft.setFlexDirection(FlexDirection.COLUMN);
        layoutManagerLeft.setFlexWrap(FlexWrap.WRAP);
        layoutManagerLeft.setJustifyContent(JustifyContent.FLEX_START);
        layoutManagerLeft.setAlignItems(AlignItems.STRETCH);
        binding.eventsContainerLeft.setLayoutManager(layoutManagerLeft);

        FlexboxLayoutManager layoutManagerRight = new FlexboxLayoutManager(getContext());
        layoutManagerRight.setFlexDirection(FlexDirection.COLUMN);
        layoutManagerRight.setFlexWrap(FlexWrap.WRAP);
        layoutManagerRight.setJustifyContent(JustifyContent.FLEX_START);
        layoutManagerRight.setAlignItems(AlignItems.STRETCH);
        binding.eventsContainerRight.setLayoutManager(layoutManagerRight);



        // now we load the event data (if there is any)

        cvm.getEvents().observe(getViewLifecycleOwner(), events -> {
            List<Event> leftEventList = new ArrayList<>();
            List<Event> rightEventList = new ArrayList<>();
            for (int i = 0; i < events.size(); i++) {
                if (i % 2 == 0) {
                    leftEventList.add(events.get(i));
                } else {
                    rightEventList.add(events.get(i));
                }
            }
            eventsAdapterLeft.submitList(leftEventList);
            eventsAdapterRight.submitList(rightEventList);
        });

        eventsContainerLeft.addOnItemTouchListener(new androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener() {
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

        eventsContainerRight.addOnItemTouchListener(new androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener() {
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