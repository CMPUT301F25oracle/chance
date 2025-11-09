package com.example.chance.controller;
import android.app.Application;
import android.content.Context;

import com.example.chance.model.Event;
import com.example.chance.model.User;

public class ChanceState extends Application {
    private static ChanceState instance;
    private User user;

    private Event loadableEvent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * get the `ChanceState` instance.
     * @return
     */
    public static ChanceState getInstance() {
        return instance;
    }

    /**
     * set the loggined in user
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * get the currently logged in user
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * @deprecated
     * @return
     */
    public Event getLoadableEvent() {
        return loadableEvent;
    }

    /**
     * @deprecated
     * @param loadableEvent
     */
    public void setLoadableEvent(Event loadableEvent) {
        this.loadableEvent = loadableEvent;
    }
}

