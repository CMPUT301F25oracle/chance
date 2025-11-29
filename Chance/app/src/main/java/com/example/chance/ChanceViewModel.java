package com.example.chance;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chance.model.Event;
import com.example.chance.model.Notification;
import com.example.chance.model.User;
import com.example.chance.util.Tuple3;
import com.example.chance.views.base.ChanceFragment;
import com.example.chance.views.base.ChancePopup;

import java.util.ArrayList;
import java.util.List;


public class ChanceViewModel extends ViewModel {
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<User> authenticationSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navBarVisible = new MutableLiveData<>();
    private final MutableLiveData<Tuple3<Class<? extends ChanceFragment>, Bundle, String>> newFragment = new MutableLiveData<>();
    private final MutableLiveData<Tuple3<Class<? extends ChanceFragment>, Bundle, Void>> newPopup = new MutableLiveData<>();
    private final MutableLiveData<String> newBannerMessage = new MutableLiveData<>();
    private final MutableLiveData<String> requestedEventID = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> events = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Notification>> notifications = new MutableLiveData<>(new ArrayList<>());


    /**
     * gets the current user
     *
     * @return current user
     */
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    /**
     * sets the current user
     *
     * @param user
     */
    public void setCurrentUser(User user) {
        currentUser.postValue(user);
    }

    /**
     * gets if the main UI should be loaded
     *
     * @return
     */
    public LiveData<Boolean> getLoadMainUI() {
        return navBarVisible;
    }

    /**
     * sets if the main UI should be loaded
     *
     * @param visible
     */
    public void setLoadMainUI(boolean visible) {
        navBarVisible.postValue(visible);
    }

    /**
     * gets the new fragment to be loaded
     *
     * @return
     */
    public MutableLiveData<Tuple3<Class<? extends ChanceFragment>, Bundle, String>> getNewFragment() {
        return newFragment;
    }

    /**
     * sets the new fragment to be loaded
     *
     * @param fragment
     * @param bundle
     */
    public void setNewFragment(Class<? extends ChanceFragment> fragment, Bundle bundle, String transition) {
        newFragment.postValue(new Tuple3<>(fragment, bundle, transition));
    }

    /**
     * gets the list of all events
     *
     * @return list of events
     */
    public LiveData<List<Event>> getEvents() {
        return events;
    }

    /**
     * sets the list of all events
     *
     * @param events
     */
    public void setEvents(List<Event> events) {
        this.events.postValue(events);
    }

    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications.postValue(notifications);
    }

    public void addEvent(Event event) {
        events.getValue().add(event);
        events.postValue(events.getValue());
    }

    public void removeEvent(Event event) {
        events.getValue().remove(event);
        events.postValue(events.getValue());
    }

    public void requestOpenEvent(String eventID) {
        Log.d("ChanceViewModel", "requestOpenEvent: " + eventID);
        requestedEventID.postValue(eventID);
    }

    public LiveData<String> getEventToOpen() {
        return requestedEventID;
    }

    /**
     * gets if the user authentication was successful
     *
     * @return
     */
    public LiveData<User> getAuthenticationSuccess() {
        return authenticationSuccess;
    }

    /**
     * sets if the user authentication was successful
     *
     * @param user
     */
    public void setAuthenticationSuccess(User user) {
        authenticationSuccess.postValue(user);
    }

    public MutableLiveData<Tuple3<Class<? extends ChanceFragment>, Bundle, Void>> getNewPopup() {
        return newPopup;
    }

    public void setNewPopup(Class<? extends ChanceFragment> popup, Bundle bundle) {
        newPopup.postValue(new Tuple3<>(popup, bundle, null));
    }

    public MutableLiveData<String> getBannerMessage() {
        return newBannerMessage;
    }

    public void setBannerMessage(String message) {
        newBannerMessage.postValue(message);
    }
}
