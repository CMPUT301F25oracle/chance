package com.example.chance.views;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.chance.views.base.ChanceFragment;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.MultiFormatReader;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Scheduler;

/**
 * Fragment responsible for scanning QR codes using CameraX.
 * Analyzes camera frames to detect event IDs and navigate to them.
 */
public class QrcodeScanner extends ChanceFragment {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final String TAG = "QrcodeScanner";

    private QrcodeScannerBinding binding;
    ActivityResultLauncher<String> requestPermissionPopup;

    static Preview cameraPreview = null;
    static ImageAnalysis qrcodeAnalyzer = null;
    private ExecutorService cameraExecutor;

    /**
     * Inflates the QR code scanner layout using ViewBinding.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = QrcodeScannerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Registers the camera permission launcher to initialize the scanner upon approval.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // check for camera permissions, and attempt to load scanner routine
        requestPermissionPopup = registerForActivityResult(new ActivityResultContracts.RequestPermission(), (granted) -> {
            if (granted) {
                QRCodeScannerRoutine();
            } else {
                throw new RuntimeException("QRCode scanner failed to start.");
            }
        });
        //requestPermissionPopup.launch(android.Manifest.permission.CAMERA);
    }

    /**
     * Launches the permission request after the entering transition completes to prevent UI lag.
     */
    @Override
    public void chanceEnterTransitionComplete() {
        super.chanceEnterTransitionComplete();
        requestPermissionPopup.launch(android.Manifest.permission.CAMERA);
    }

    /**
     * Initializes CameraX, binds the preview to the layout, and attaches the image analyzer.
     */
    @SuppressLint("CheckResult")
    private void QRCodeScannerRoutine() {
        Scheduler ioSchedulerSubscribable = io.reactivex.rxjava3.schedulers.Schedulers.io();
        Scheduler androidSchedulerObservable = AndroidSchedulers.mainThread();
        com.google.common.util.concurrent.ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        Executor qrcodeInitThread = Executors.newSingleThreadExecutor();
        Handler workHandler = new Handler(Looper.getMainLooper());


        io.reactivex.rxjava3.core.Single.fromFuture(cameraProviderFuture)
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io()) // run the future/get on IO thread
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread()) // back to main for UI work
                .subscribe(cameraProvider -> {
                    if (cameraPreview == null) {
                        cameraPreview = new Preview.Builder().build();
                    }

                    try {
                        cameraPreview.setSurfaceProvider(binding.cameraPreview.getSurfaceProvider());

                    } catch (Exception e) {
                        // view was likely destroyed, just return
                        return;
                    }

                    if (cameraProvider != null) {
                        cameraProvider.unbindAll();
                    }
                    // now that we have a working camera view, start looking for qr codes in video frames
                    if (qrcodeAnalyzer == null) {
                        qrcodeAnalyzer = new ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();
                    }
                    cameraExecutor = Executors.newSingleThreadExecutor();
                    qrcodeAnalyzer.setAnalyzer(cameraExecutor, new QrCodeAnalyzer());

                    cameraProvider.bindToLifecycle(
                            getViewLifecycleOwner(),
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            cameraPreview,
                            qrcodeAnalyzer
                    );
                }, throwable -> {
                    // handle error (execution/interrupt or other)
                    throw new RuntimeException("QRCode scanner failed to run.", throwable);
                });
        Log.d("fish", "Bottom lol");
    }

    /**
     * Inner class that analyzes individual camera frames to detect QR codes.
     */
    private class QrCodeAnalyzer implements ImageAnalysis.Analyzer {
        private final long timeSinceLastDecode = 0;

        /**
         * Converts the image proxy to a bitmap, decodes the QR string, and navigates to the event.
         */
        @Override
        public void analyze(ImageProxy image_frame) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - timeSinceLastDecode < 500) {
                image_frame.close();
                return;
            }
            Bitmap image_bitmap = image_frame.toBitmap();
            String event_id = QRCodeHandler.decodeQRCode(image_bitmap);
            if (event_id != null) {
                cvm.requestOpenEvent(event_id);
            }
            image_frame.close();
        }
    }

    /**
     * Cleans up camera executors and binding references when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        qrcodeAnalyzer.clearAnalyzer();
        cameraExecutor.shutdownNow();
    }
}