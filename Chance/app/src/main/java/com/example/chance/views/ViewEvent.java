package com.example.chance.views;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chance.ChanceViewModel;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.controller.QRCodeHandler;
import com.example.chance.databinding.ViewEventBinding;
import com.example.chance.model.Event;
import com.example.chance.model.User;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.encoder.QRCode;

import java.net.URI;
import java.util.Objects;


public class ViewEvent extends Fragment {
    private ViewEventBinding binding;
    private ChanceViewModel cvm;
    private DataStoreManager dsm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ViewEventBinding.inflate(inflater, container, false);
        cvm = new ViewModelProvider(requireActivity()).get(ChanceViewModel.class);
        dsm = DataStoreManager.getInstance();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        String eventID = bundle.getString("event_id");
        cvm.getEvents().observe(getViewLifecycleOwner(), events -> {
            Event event = events.stream().filter(ev -> Objects.equals(ev.getID(), eventID)).findFirst().orElse(null);
            if (event == null) {
                dsm.getEvent(eventID, this::loadEventInformation);
            } else {
                loadEventInformation(event);
            }
        });
    }

    public void loadEventInformation(Event event) {
        assert event != null;
        binding.eventName.setText(event.getName());
        binding.eventInformation.setText(
                String.format("* ? users currently in waiting list  /  $%.2f per person.\n%s", event.getPrice(), event.getLocation()));
        binding.eventOverview.setText(event.getDescription());
        // now we load the events unique QRCode
        Bitmap unique_qrcode;
        try {
            unique_qrcode = QRCodeHandler.generateQRCode(event.getID());
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        binding.qrcodeButton.setImageBitmap(unique_qrcode);

//        binding.enterLotteryButton.setOnClickListener(() -> {
//
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
