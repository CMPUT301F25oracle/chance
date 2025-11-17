package com.example.chance.views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.example.chance.ChanceViewModel;
import com.example.chance.R;
import com.example.chance.controller.QRCodeHandler;
import com.example.chance.databinding.QrcodeScannerBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.MultiFormatReader;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QrcodeScanner extends Fragment {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final String TAG = "QrcodeScanner";
    
    private QrcodeScannerBinding binding;
    private ChanceViewModel cvm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = QrcodeScannerBinding.inflate(inflater, container, false);
        cvm = new ViewModelProvider(requireActivity()).get(ChanceViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // check for camera permissions, and attempt to load scanner routine
        ActivityResultLauncher<String> requestPermissionPopup = registerForActivityResult(new ActivityResultContracts.RequestPermission(), (granted) -> {
            if (granted) {
                QRCodeScannerRoutine();
            } else {
                throw new RuntimeException("QRCode scanner failed to start.");
            }
        });

        requestPermissionPopup.launch(android.Manifest.permission.CAMERA);



    }

    private void QRCodeScannerRoutine() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview cameraPreview = new Preview.Builder().build();
                cameraPreview.setSurfaceProvider(binding.cameraPreview.getSurfaceProvider());

                if (cameraProvider != null) {
                    cameraProvider.unbindAll();
                }
                assert cameraProvider != null;

                // now that we have a working camera view, start looking for qr codes in video frames
                ImageAnalysis qrcodeAnalyzer = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();
                qrcodeAnalyzer.setAnalyzer(cameraExecutor, new QrCodeAnalyzer());

                cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        cameraPreview,
                        qrcodeAnalyzer
                );



            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException("QRCode scanner failed to run.");
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class QrCodeAnalyzer implements ImageAnalysis.Analyzer {
        @Override
        public void analyze(ImageProxy image_frame) {
            Bitmap image_bitmap = image_frame.toBitmap();
            String event_id = QRCodeHandler.decodeQRCode(image_bitmap);
            if (event_id != null) {
                cvm.requestOpenEvent(event_id);
            }
            image_frame.close();
        }
    }
}
