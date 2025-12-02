package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.adapters.EventRegistrationHistoryPopupListAdapter;
import com.example.chance.adapters.MultiPurposeProfileSearchScreenListAdapter;
import com.example.chance.databinding.EventRegistrationHistoryPopupBinding;
import com.example.chance.databinding.NotSelectedForEventPopupBinding;
import com.example.chance.views.base.ChancePopup;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventRegistrationHistoryPopup extends ChancePopup {
    private EventRegistrationHistoryPopupBinding binding;
    private EventRegistrationHistoryPopupListAdapter historyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EventRegistrationHistoryPopupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<String> names = meta.getStringArrayList("names");
        ArrayList<String> IDs = meta.getStringArrayList("IDs");
        ArrayList<Map<String, String>> historyMapList = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            HashMap<String, String> historyMap = new HashMap<>();
            historyMap.put("name", names.get(i));
            historyMap.put("ID", IDs.get(i));
            historyMapList.add(historyMap);
        }

        RecyclerView historyContainer = binding.historyContainer;
        historyAdapter = new EventRegistrationHistoryPopupListAdapter();
        historyContainer.setAdapter(historyAdapter);
        historyAdapter.submitList(historyMapList);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        historyContainer.setLayoutManager(layoutManager);
    }
}
