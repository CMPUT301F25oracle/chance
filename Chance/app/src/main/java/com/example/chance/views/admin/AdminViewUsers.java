package com.example.chance.views.admin;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.model.Event;
import com.example.chance.views.base.ChanceFragment;
import com.example.chance.views.multiuse.MultiPurposeProfileSearchScreen;

import java.util.List;
import java.util.Objects;

public class AdminViewUsers extends MultiPurposeProfileSearchScreen {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dsm.getAllUsers(users -> {
            submitList(users);
        }, e->{});
    }

}
