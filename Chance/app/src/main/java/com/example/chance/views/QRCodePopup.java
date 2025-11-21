package com.example.chance.views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chance.databinding.QrcodePopupBinding;
import com.example.chance.views.base.ChancePopup;

public class QRCodePopup extends ChancePopup {
    private QrcodePopupBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = QrcodePopupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        byte[] qrcodeByteArray = meta.getByteArray("qrcode_bytes");
        Bitmap qrcodeBitmap = BitmapFactory.decodeByteArray(qrcodeByteArray,0,qrcodeByteArray.length);
        binding.qrcode.setImageBitmap(qrcodeBitmap);
    }
}
