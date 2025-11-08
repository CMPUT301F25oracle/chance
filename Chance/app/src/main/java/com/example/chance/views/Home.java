package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chance.R;
import com.example.chance.controller.ChanceState;
import com.example.chance.databinding.HomeBinding;
import com.example.chance.model.User;

public class Home extends Fragment {

    private HomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = HomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = ChanceState.getInstance().getUser();
        binding.homeSystemMessage.setText("Hello, " + user.getUsername());
        //region button press handlers
        binding.createEventButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_view, new CreateEvent())
                    .commit();
        });

        //endregion


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}