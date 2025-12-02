package com.example.chance.views;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.adapters.MultiPurposeEventSearchScreenListAdapter;
import com.example.chance.adapters.NotificationPopupAdapter;
import com.example.chance.databinding.NotificationPopupBinding;
import com.example.chance.model.Notification;
import com.example.chance.views.base.ChancePopup;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.Comparator;

/**
 * Popup fragment for displaying user notifications.
 */
public class NotificationPopup extends ChancePopup {

    private NotificationPopupBinding binding;
    private NotificationPopupAdapter notificationsAdapter;

    /**
     * Inflates the notification popup layout using ViewBinding.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = NotificationPopupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Initializes the popup view components.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView notificationsContainer = binding.notificationsContainer;
        notificationsAdapter = new NotificationPopupAdapter();
        notificationsContainer.setAdapter(notificationsAdapter);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        notificationsContainer.setLayoutManager(layoutManager);

        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            dsm.user(user).getNotifications(notificationList->{
                if (notificationList.size() > 0) {
                    binding.noNotificationsOverlay.setVisibility(GONE);
                    notificationList.sort(new Comparator<Notification>() {
                        @Override
                        public int compare(Notification n1, Notification n2) {
                            return Math.toIntExact(n2.getCreationDate().getTime() - n1.getCreationDate().getTime());
                        }
                    });
                    notificationsAdapter.submitList(notificationList);
                } else {
                    binding.notificationLoadingMessage.setText("No Notifications Found.");
                    binding.noNotificationsOverlay.setVisibility(VISIBLE);
                }
            }, __ -> {});
        });

        notificationsContainer.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            final GestureDetector gestureHandler = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent __, MotionEvent ___, float velocityX, float velocityY) {
                    // Let RecyclerView handle the fling
                    notificationsContainer.fling((int) velocityX, (int) velocityY);
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    View notificationPill = notificationsContainer.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                    if (notificationPill != null) {
                        Notification notificationInstance = (Notification) notificationPill.getTag();
                        int type = notificationInstance.getType();
                        Bundle bundle = new Bundle();
                        bundle.putString("eventID", notificationInstance.getMeta().getOrDefault("eventID", ""));
                        switch (type) {
                            case 0: {
                                cvm.setNewPopup(InvitedToEventPopup.class, bundle);
                                break;
                            }
                            case 1: {
                                cvm.setNewPopup(NotSelectedForEventPopup.class, bundle);
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                        //cvm.requestOpenEvent(eventId);
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
}