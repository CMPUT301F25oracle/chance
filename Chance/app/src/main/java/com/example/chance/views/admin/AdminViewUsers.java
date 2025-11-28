package com.example.chance.views.admin;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.model.User;
import com.example.chance.views.base.MultiPurposeProfileSearchScreen;

/**
 * Fragment for displaying a searchable list of all users for the Admin.
 * Extends MultiPurposeProfileSearchScreen to reuse list functionality.
 */
public class AdminViewUsers extends MultiPurposeProfileSearchScreen {

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
    public void onUserClicked(User user) {
        Bundle bundle = new Bundle();
        bundle.putString("userID", user.getID());
        cvm.setNewFragment(AdminViewUserProfile.class, bundle, "fade");
    }

}