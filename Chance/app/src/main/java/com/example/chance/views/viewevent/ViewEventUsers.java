package com.example.chance.views.viewevent;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.views.base.MultiPurposeProfileSearchScreen;

public class ViewEventUsers extends MultiPurposeProfileSearchScreen {

    /**
     * Fetches all users from the data store and populates the list upon view creation.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dsm.getAllUsers(users -> {
            submitList(users);
        }, e->{});
    }
}
