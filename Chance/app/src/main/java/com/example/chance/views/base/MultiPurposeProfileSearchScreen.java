package com.example.chance.views.base;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.adapters.MultiPurposeProfileSearchScreenListAdapter;
import com.example.chance.databinding.MultiPurposeProfileSearchScreenBinding;
import com.example.chance.model.User;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.disposables.Disposable;

public class MultiPurposeProfileSearchScreen extends ChanceFragment {
    private MultiPurposeProfileSearchScreenBinding binding;
    List<User> profileList = new ArrayList<>();
    private MultiPurposeProfileSearchScreenListAdapter profileAdapter;
    private Disposable profilesDisposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MultiPurposeProfileSearchScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // now we set up our event adapter
        RecyclerView profilesContainer = binding.profilesContainer;
        profileAdapter = new MultiPurposeProfileSearchScreenListAdapter();
        profilesContainer.setAdapter(profileAdapter);
        profileAdapter.submitList(profileList);


        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        profilesContainer.setLayoutManager(layoutManager);




        profilesContainer.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            final GestureDetector gestureHandler = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent __, MotionEvent ___, float velocityX, float velocityY) {
                    // Let RecyclerView handle the fling
                    profilesContainer.fling((int) velocityX, (int) velocityY);
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    View profilePill = profilesContainer.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                    if (profilePill != null) {
                        User userInstance = (User) profilePill.getTag();
                        interceptProfileClick(userInstance);
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

        List<String> userIDS = meta.getStringArrayList("users");
        if (userIDS != null) {
            dsm.getAllUsers(users -> {
                List<User> filteredUsers =  users.stream().filter(user->userIDS.contains(user.getID())).toList();
                submitList(filteredUsers);
            }, e->{});
        }

    }

    public void submitList(List<User> users) {
        //region: load events asynchronously

        // we create a new eventList to gradually load events into UI,
        // mitigating ui thread freezes
        AtomicInteger idx = new AtomicInteger();
        profilesDisposable = io.reactivex.rxjava3.core.Observable
            .fromIterable(users)
            .concatMap(ev ->
                io.reactivex.rxjava3.core.Observable
                    .just(ev)
                    .delay(50, java.util.concurrent.TimeUnit.MILLISECONDS)
            )
            .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(user -> {
                profileList.add(user);
                profileAdapter.notifyItemInserted(
                    idx.getAndIncrement());
            });
        //endregion
    }

    public void interceptProfileClick(User user) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        profilesDisposable.dispose();
        binding = null;
    }

}
