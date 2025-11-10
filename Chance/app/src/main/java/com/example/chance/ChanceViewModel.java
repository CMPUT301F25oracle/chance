package com.example.chance;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chance.model.User;
import com.example.chance.util.Tuple;


public class ChanceViewModel extends ViewModel {
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> authenticationSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navBarVisible = new MutableLiveData<>();
    private final MutableLiveData<Tuple<Class<? extends Fragment>, Bundle>> newFragment = new MutableLiveData<>();
    private final MutableLiveData<String> requestedEventID = new MutableLiveData<>();

    // Message communication
    public LiveData<String> getMessage() {
        return message;
    }

    public void sendMessage(String msg) {
        message.postValue(msg);
    }

    // User data
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser.postValue(user);
    }

    public LiveData<Boolean> getLoadMainUI() {
        return navBarVisible;
    }

    public void setLoadMainUI(boolean visible) {
        navBarVisible.postValue(visible);
    }

    public MutableLiveData<Tuple<Class<? extends Fragment>, Bundle>> getNewFragment() {
        return newFragment;
    }

    public void setNewFragment(Class<? extends Fragment> fragment, Bundle bundle) {
        newFragment.postValue(new Tuple<>(fragment, bundle));
    }

    public void requestOpenEvent(String eventID) {
        Log.d("ChanceViewModel", "requestOpenEvent: " + eventID);
        requestedEventID.postValue(eventID);
    }

    public LiveData<String> getEventToOpen() {
        return requestedEventID;
    }

    // Authentication state
    public LiveData<Boolean> getAuthenticationSuccess() {
        return authenticationSuccess;
    }


    public void setAuthenticationSuccess(boolean success) {
        authenticationSuccess.postValue(success);
    }


}
