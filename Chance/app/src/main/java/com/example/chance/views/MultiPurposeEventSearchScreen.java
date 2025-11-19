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
import com.example.chance.adapters.EventSearchScreenListAdapter;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.MultiPurposeEventSearchScreenBinding;
import com.example.chance.model.Event;
import com.example.chance.views.base.ChanceFragment;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.List;

public class MultiPurposeEventSearchScreen extends ChanceFragment {
    private MultiPurposeEventSearchScreenBinding binding;
    private EventSearchScreenListAdapter eventsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MultiPurposeEventSearchScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // now we set up our event adapter
        RecyclerView eventsContainer = binding.eventsSearchContainer;
        eventsAdapter = new EventSearchScreenListAdapter();
        eventsContainer.setAdapter(eventsAdapter);

        // next we make sure flexbox is configured on the recyclerview
//        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
//        layoutManager.setFlexDirection(FlexDirection.COLUMN);
//        layoutManager.setFlexWrap(FlexWrap.NOWRAP);
//        layoutManager.setJustifyContent(JustifyContent.CENTER);
//        layoutManager.setAlignItems(AlignItems.CENTER);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.COLUMN);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setAlignItems(AlignItems.STRETCH);

        eventsContainer.setLayoutManager(layoutManager);

        eventsContainer.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
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

    public void submitList(List<Event> events) {
        eventsAdapter.submitList(events);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
