package com.example.chance.controller;
import android.app.Application;
import android.content.Context;

import com.example.chance.model.Event;
import com.example.chance.model.User;

public class ChanceState extends Application {
    private static ChanceState instance;
    private User user;

    private Event loadable_event;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static ChanceState getInstance() {
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Event getLoadable_event() {
        return loadable_event;
    }

    public void setLoadable_event(Event loadable_event) {
        this.loadable_event = loadable_event;
    }
}

