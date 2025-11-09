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

import com.example.chance.controller.ChanceState;
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
    private DataStoreManager dsm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ViewEventBinding.inflate(inflater, container, false);
        dsm = DataStoreManager.getInstance();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        String eventID = bundle.getString("event_id");
        dsm.getEvent(eventID, (event) -> {
            if (event == null) {
                throw new RuntimeException("Event not found");
            }
            binding.eventName.setText(event.getName());
            binding.eventInformation.setText(
                    String.format("* ? users currently in waiting list  /  $%.2f per person.\n%s", event.getPrice(), event.getLocation()));
            binding.eventOverview.setText(event.getDescription());
            // now we load the events unique QRCode
            Bitmap unique_qrcode;
            try {
                unique_qrcode = QRCodeHandler.generateQRCode(event.getId());
            } catch (WriterException e) {
                throw new RuntimeException(e);
            }

            binding.qrcodeButton.setImageBitmap(unique_qrcode);

        });

        //
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
