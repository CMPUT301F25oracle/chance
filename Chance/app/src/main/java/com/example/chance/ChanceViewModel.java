package com.example.chance;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chance.model.User;

public class ChanceViewModel extends ViewModel {
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> authenticationSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navBarVisible = new MutableLiveData<>();
    private final MutableLiveData<Class<? extends Fragment>> newFragment = new MutableLiveData<>();

    // Message communication
    public LiveData<String> getMessage() {
        return message;
    }

    public void sendMessage(String msg) {
        message.setValue(msg);
    }

    // User data
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser.setValue(user);
    }

    public LiveData<Boolean> getNavBarVisible() {
        return navBarVisible;
    }

    public void setNavBarVisible(boolean visible) {
        navBarVisible.setValue(visible);
    }

    public LiveData<Class<? extends Fragment>> getNewFragment() {
        return newFragment;
    }

    public void setNewFragment(Class<? extends Fragment> fragment) {
        newFragment.setValue(fragment);
    }

    // Authentication state
    public LiveData<Boolean> getAuthenticationSuccess() {
        return authenticationSuccess;
    }


    public void setAuthenticationSuccess(boolean success) {
        authenticationSuccess.setValue(success);
    }


}
