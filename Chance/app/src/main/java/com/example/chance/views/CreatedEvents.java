package com.example.chance.views;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CreatedEvents extends MultiPurposeEventSearchScreen {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cvm.getEvents().observe(getViewLifecycleOwner(), this::submitList);
    }
}
