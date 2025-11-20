package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.ChanceViewModel;
import com.example.chance.adapters.EventSearchScreenListAdapter;
import com.example.chance.adapters.MultiPurposeEventSearchScreenListAdapter;
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

abstract public class MultiPurposeEventSearchScreen extends ChanceFragment {
    private MultiPurposeEventSearchScreenBinding binding;
    private MultiPurposeEventSearchScreenListAdapter eventsAdapter;

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
        RecyclerView eventsContainer = binding.eventsContainer;
        eventsAdapter = new MultiPurposeEventSearchScreenListAdapter();
        eventsContainer.setAdapter(eventsAdapter);


        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        eventsContainer.setLayoutManager(layoutManager);

        eventsContainer.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            GestureDetector gestureHandler = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent __, MotionEvent ___, float velocityX, float velocityY) {
                    // Let RecyclerView handle the fling
                    eventsContainer.fling((int) velocityX, (int) velocityY);
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    View eventPill = eventsContainer.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                    if (eventPill != null) {
                        String eventId = (String) eventPill.getTag();
                        cvm.requestOpenEvent(eventId);
                        return true;
                    }
                    return false;
                }
            });
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView viewManager, @NonNull MotionEvent touchEvent) {
                gestureHandler.onTouchEvent(touchEvent);
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
