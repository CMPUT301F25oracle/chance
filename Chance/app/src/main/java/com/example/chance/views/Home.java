package com.example.chance.views;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.adapters.MainEventSearchListAdapter;
import com.example.chance.databinding.HomeBinding;
import com.example.chance.model.Event;
import com.example.chance.model.Notification;
import com.example.chance.views.admin.Admin;
import com.example.chance.views.base.ChanceFragment;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Fragment representing the main Home dashboard.
 * Displays a staggered feed of events and provides navigation to core features.
 */
public class Home extends ChanceFragment {

    private HomeBinding binding;
    // defined here to later clean up the observer
    private Disposable eventsDisposable;

    /**
     * Inflates the Home layout using ViewBinding.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = HomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Initializes the UI, sets up Flexbox layouts for the event feed, and handles data observation.
     */
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
            dsm.user(user).postNotification(new Notification("asdda", 1, "adaads", new Date(), new byte[0]), v -> {
                Log.d("Notification", "Notification posted successfully.");
            },e -> {});

            //region: admin tools

            // we want to show the admin button if the user is an admin
            if (Objects.equals(user.getUsername(), "admin")) {
                binding.adminButton.setVisibility(VISIBLE);
                binding.adminButton.setOnClickListener(__ -> {
                    cvm.setNewFragment(Admin.class, null, "fade");
                });
            }
            //endregion

        });

        // Setup navigation listeners
        binding.buttonRegistered.setOnClickListener(__ -> {
            cvm.setNewFragment(RegisteredEvents.class, null, "fade");
        });
        binding.buttonEventCreated.setOnClickListener(__ -> {
            cvm.setNewFragment(CreatedEvents.class, null, "fade");
        });
        binding.buttonCreateEvent.setOnClickListener(__ -> {
            cvm.setNewFragment(CreateEvent.class, null, "fade");
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
        List<Event> leftEventList = new ArrayList<>();
        List<Event> rightEventList = new ArrayList<>();
        eventsAdapterLeft.submitList(leftEventList);
        eventsAdapterRight.submitList(rightEventList);

        // here's the problem: ui work is expensive, and freezes the ui thread
        // (technically since we're testing on a single core system, the *whole*
        // thread, resulting in very choppy animations between context switches.
        // How can we resolve this? *RxJava*. Using it, we're able to keep the
        // async operations, and add an interval between processing array elements
        // so we can gradually populate the visible events while not making
        // animations unbearable to the user.

        AtomicInteger leftIdx = new AtomicInteger();
        AtomicInteger rightIdx = new AtomicInteger();

        // RxJava stream to populate the left and right columns with a stagger effect
        cvm.getEvents().observe(getViewLifecycleOwner(), events -> {
            leftEventList.clear();
            rightEventList.clear();
            leftIdx.set(0);
            rightIdx.set(0);
            eventsAdapterLeft.notifyDataSetChanged();
            eventsAdapterRight.notifyDataSetChanged();
            eventsDisposable = io.reactivex.rxjava3.core.Observable
                    .fromIterable(events)
                    .concatMap(ev ->
                            io.reactivex.rxjava3.core.Observable
                                    .just(ev)
                                    .delay(50, java.util.concurrent.TimeUnit.MILLISECONDS)
                    )
                    .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(event -> {
                        if ((leftIdx.get() + rightIdx.get()) % 2 == 0) {
                            leftEventList.add(event);
                            eventsAdapterLeft.notifyItemInserted((leftEventList.size() + leftIdx.getAndIncrement()) % leftEventList.size());
                        } else {
                            rightEventList.add(event);
                            eventsAdapterRight.notifyItemInserted((rightEventList.size() + rightIdx.getAndIncrement()) % rightEventList.size());
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                    });
        });
//        cvm.getEvents().observe(getViewLifecycleOwner(), events -> {
//            for (int i = 0; i < events.size(); i++) {
//                if (i % 2 == 0) {
//                    leftEventList.add(events.get(i));
//                    eventsAdapterLeft.notifyItemChanged(i);
//                } else {
//                    rightEventList.add(events.get(i));
//                    eventsAdapterRight.notifyItemChanged(i);
//                }
//            }
//        });

        // Touch listener for the left column to handle event clicks
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

        // Touch listener for the right column to handle event clicks
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

    /**
     * Cleans up RxJava disposables and view bindings when view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        eventsDisposable.dispose();
        binding = null;
    }
}