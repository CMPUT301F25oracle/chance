//package com.example.chance.views;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.DatePicker;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.PickVisualMediaRequest;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.bumptech.glide.Glide;
//import com.example.chance.ChanceViewModel;
//import com.example.chance.R;
//import com.example.chance.controller.DataStoreManager;
//import com.example.chance.databinding.CreateEventBinding;
//import com.example.chance.model.Event;
//import com.example.chance.model.EventImage;
//import com.example.chance.model.User;
//import com.example.chance.views.base.ChanceFragment;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.util.Base64;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
///**
// * Fragment that handles the creation of new events.
// * Manages user input, image selection, and data submission.
// */
//public class CreateEvent extends ChanceFragment {
//
//    private CreateEventBinding binding;
//    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
//    private EventImage selectedEventBanner;
//
//    /**
//     * Inflates the layout and initializes the media picker result launcher.
//     */
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        binding = CreateEventBinding.inflate(inflater, container, false);
//
//        pickMedia =
//                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
//                    if (uri != null) {
//                        try {
//                            // lets grab the image from the uri
//                            InputStream bannerFileStream = getContext().getContentResolver().openInputStream(uri);
//
//                            byte[] bannerBytes = inputStreamToPNGByteArray(bannerFileStream);
//                            String bannerBase64 = Base64.getEncoder().encodeToString(bannerBytes);
//                            selectedEventBanner = new EventImage(bannerBase64);
//
//                        } catch (FileNotFoundException e) {
//                            throw new RuntimeException(e);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }
//                });
//
//        return binding.getRoot();
//    }
//
//    /**
//     * Sets up UI listeners to capture event details and submit the new event.
//     */
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        binding.addBannerButton.setOnClickListener(__ -> {
//            promptImageFromUser();
//            Log.d("CreateEvent", "Add banner clicked");
//        });
//        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
//            binding.submitButton.setOnClickListener(v -> {
//                Calendar calendar = Calendar.getInstance();
//
//                String event_name = binding.eventNameInput.getText().toString();
//                String event_address = binding.eventAddressInput.getText().toString();
//                int maximum_candidates = Integer.parseInt(binding.candidateMaximumInput.getText().toString());
//                int maximum_waitinglist = Integer.parseInt(binding.waitinglistRestriction.getText().toString());
//                float attendance_price = Float.parseFloat(binding.priceInput.getText().toString());
//
//                DatePicker event_reg_start = binding.registrationStartInput;
//                calendar.set(event_reg_start.getYear(), event_reg_start.getMonth(), event_reg_start.getDayOfMonth());
//                Date event_start_calendar = calendar.getTime();
//
//                DatePicker event_reg_end = binding.registrationEndInput;
//                calendar.set(event_reg_end.getYear(), event_reg_end.getMonth(), event_reg_end.getDayOfMonth());
//                Date event_end_calendar = calendar.getTime();
//
//                String event_description = binding.descriptionInput.getText().toString();
//
//                Event new_event = new Event(event_name, event_address, maximum_candidates, attendance_price, event_description, event_start_calendar, event_end_calendar, user.getID(), maximum_waitinglist);
//                dsm.createNewEvent(new_event, (event) -> {
//                    if (selectedEventBanner != null) {
//                        selectedEventBanner.setID(event.getID());
//                        dsm.eventImage(selectedEventBanner).save((__)->{
//                            cvm.setNewFragment(Home.class, null, "");
//                        }, (__)->{});
//                    } else {
//                        cvm.setNewFragment(Home.class, null, "");
//                    }
//                }, (__)->{});
//            });
//        });
//    }
//
//    /**
//     * Launches the system photo picker for the user to select an event banner.
//     */
//    public void promptImageFromUser() {
//        String mimeType = "image/gif";
//        pickMedia.launch(new PickVisualMediaRequest.Builder()
//                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
//                .build());
//    }
//
//    /**
//     * Utility method to convert an InputStream into a PNG byte array.
//     */
//    public byte[] inputStreamToPNGByteArray(InputStream inputStream) throws IOException {
//        Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
//        ByteArrayOutputStream pngImageStream = new ByteArrayOutputStream();
//        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, pngImageStream);
//        return pngImageStream.toByteArray();
//    }
//
//    /**
//     * Cleans up view binding when the fragment is destroyed.
//     */
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//}

package com.example.chance.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.chance.databinding.CreateEventBinding;
import com.example.chance.model.Event;
import com.example.chance.model.EventImage;
import com.example.chance.views.base.ChanceFragment;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

/**
 * Fragment that handles the creation of new events.
 * Manages user input, image selection, and data submission.
 */
public class CreateEvent extends ChanceFragment {

    private CreateEventBinding binding;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private EventImage selectedEventBanner;

    // Launcher for handling the permission request result.
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. You can now access the location.
                    Log.d("CreateEvent", "Location permission granted.");
                    // You could call a method here to get the user's location.
                } else {
                    // Permission denied. Explain to the user why the feature is unavailable.
                    Log.d("CreateEvent", "Location permission denied.");
                    Toast.makeText(getContext(), "Location permission is required to tag the event location.", Toast.LENGTH_LONG).show();
                }
            });

    /**
     * Inflates the layout and initializes the media picker result launcher.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = CreateEventBinding.inflate(inflater, container, false);

        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        try {
                            // lets grab the image from the uri
                            InputStream bannerFileStream = getContext().getContentResolver().openInputStream(uri);

                            byte[] bannerBytes = inputStreamToPNGByteArray(bannerFileStream);
                            String bannerBase64 = Base64.getEncoder().encodeToString(bannerBytes);
                            selectedEventBanner = new EventImage(bannerBase64);

                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        return binding.getRoot();
    }

    /**
     * Sets up UI listeners to capture event details and submit the new event.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Request location permission when the view is created
        requestLocationPermission();

        binding.addBannerButton.setOnClickListener(__ -> {
            promptImageFromUser();
            Log.d("CreateEvent", "Add banner clicked");
        });

        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            binding.submitButton.setOnClickListener(v -> {
                String event_name = binding.eventNameInput.getText().toString();
                String event_address = binding.eventAddressInput.getText().toString();
                String max_candidates_str = binding.candidateMaximumInput.getText().toString();
                String max_waitinglist_str = binding.waitinglistRestriction.getText().toString();
                String attendance_price_str = binding.priceInput.getText().toString();
                String event_description = binding.descriptionInput.getText().toString();

                if (TextUtils.isEmpty(event_name) || TextUtils.isEmpty(event_address) ||
                        TextUtils.isEmpty(max_candidates_str) || TextUtils.isEmpty(max_waitinglist_str) ||
                        TextUtils.isEmpty(attendance_price_str) || TextUtils.isEmpty(event_description)) {

                    cvm.setBannerMessage("Please fill out all required fields");
                    return;
                }

                Calendar calendar = Calendar.getInstance();

                int maximum_candidates = Integer.parseInt(max_candidates_str);
                int maximum_waitinglist = Integer.parseInt(max_waitinglist_str);
                float attendance_price = Float.parseFloat(attendance_price_str);

                DatePicker event_reg_start = binding.registrationStartInput;
                calendar.set(event_reg_start.getYear(), event_reg_start.getMonth(), event_reg_start.getDayOfMonth());
                Date event_start_calendar = calendar.getTime();

                DatePicker event_reg_end = binding.registrationEndInput;
                calendar.set(event_reg_end.getYear(), event_reg_end.getMonth(), event_reg_end.getDayOfMonth());
                Date event_end_calendar = calendar.getTime();

                Event new_event = new Event(event_name, event_address, maximum_candidates, attendance_price, event_description, event_start_calendar, event_end_calendar, user.getID(), maximum_waitinglist);
                dsm.createNewEvent(new_event, (event) -> {
                    Log.d("Definitely created", "def");
                    if (selectedEventBanner != null) {
                        selectedEventBanner.setID(event.getID());
                        dsm.eventImage(selectedEventBanner).save((__)->{
                            cvm.setNewFragment(Home.class, null, "");
                        }, (__)->{});
                    } else {
                        cvm.setNewFragment(Home.class, null, "");
                    }
                }, (__)->{});
            });
        });
    }

    /**
     * Checks for location permission and requests it if not granted.
     */
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            Log.d("CreateEvent", "Location permission is already granted.");
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            Log.d("CreateEvent", "Requesting location permission.");
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Launches the system photo picker for the user to select an event banner.
     */
    public void promptImageFromUser() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    /**
     * Utility method to convert an InputStream into a PNG byte array.
     */
    public byte[] inputStreamToPNGByteArray(InputStream inputStream) throws IOException {
        Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream pngImageStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, pngImageStream);
        return pngImageStream.toByteArray();
    }

    /**
     * Cleans up view binding when the fragment is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

//package com.example.chance.views;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.DatePicker;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.PickVisualMediaRequest;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.example.chance.databinding.CreateEventBinding;
//import com.example.chance.model.Event;
//import com.example.chance.model.EventImage;
//import com.example.chance.views.base.ChanceFragment;
//
//import java.io.ByteArrayOutputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Base64;
//import java.util.Calendar;
//import java.util.Date;
//
///**
// * Fragment that handles the creation of new events.
// * Manages user input, image selection, and data submission.
// */
//public class CreateEvent extends ChanceFragment {
//
//    private CreateEventBinding binding;
//    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
//    private EventImage selectedEventBanner;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        binding = CreateEventBinding.inflate(inflater, container, false);
//
//        pickMedia =
//                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
//                    if (uri != null) {
//                        try {
//                            InputStream bannerFileStream = getContext().getContentResolver().openInputStream(uri);
//                            byte[] bannerBytes = inputStreamToPNGByteArray(bannerFileStream);
//                            String bannerBase64 = Base64.getEncoder().encodeToString(bannerBytes);
//                            selectedEventBanner = new EventImage(bannerBase64);
//
//                        } catch (FileNotFoundException e) {
//                            throw new RuntimeException(e);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                });
//
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        binding.addBannerButton.setOnClickListener(__ -> {
//            promptImageFromUser();
//            Log.d("CreateEvent", "Add banner clicked");
//        });
//
//        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
//            binding.submitButton.setOnClickListener(v -> {
//                String event_name = binding.eventNameInput.getText().toString();
//                String event_address = binding.eventAddressInput.getText().toString();
//                String max_candidates_str = binding.candidateMaximumInput.getText().toString();
//                String max_waitinglist_str = binding.waitinglistRestriction.getText().toString();
//                String attendance_price_str = binding.priceInput.getText().toString();
//                String event_description = binding.descriptionInput.getText().toString();
//
//                if (TextUtils.isEmpty(event_name) || TextUtils.isEmpty(event_address) ||
//                        TextUtils.isEmpty(max_candidates_str) || TextUtils.isEmpty(max_waitinglist_str) ||
//                        TextUtils.isEmpty(attendance_price_str) || TextUtils.isEmpty(event_description)) {
//
//                    cvm.setBannerMessage("Please fill out all required fields");
//                    return;
//                }
//
//                Calendar calendar = Calendar.getInstance();
//
//                int maximum_candidates = Integer.parseInt(max_candidates_str);
//                int maximum_waitinglist = Integer.parseInt(max_waitinglist_str);
//                float attendance_price = Float.parseFloat(attendance_price_str);
//
//                DatePicker event_reg_start = binding.registrationStartInput;
//                calendar.set(event_reg_start.getYear(), event_reg_start.getMonth(), event_reg_start.getDayOfMonth());
//                Date event_start_calendar = calendar.getTime();
//
//                DatePicker event_reg_end = binding.registrationEndInput;
//                calendar.set(event_reg_end.getYear(), event_reg_end.getMonth(), event_reg_end.getDayOfMonth());
//                Date event_end_calendar = calendar.getTime();
//
//                Event new_event = new Event(event_name, event_address, maximum_candidates, attendance_price, event_description, event_start_calendar, event_end_calendar, user.getID(), maximum_waitinglist);
//                dsm.createNewEvent(new_event, (event) -> {
//                    if (selectedEventBanner != null) {
//                        selectedEventBanner.setID(event.getID());
//                        dsm.eventImage(selectedEventBanner).save((__)->{
//                            cvm.setNewFragment(Home.class, null, "");
//                        }, (__)->{});
//                    } else {
//                        cvm.setNewFragment(Home.class, null, "");
//                    }
//                }, (__)->{});
//            });
//        });
//    }
//
//    public void promptImageFromUser() {
//        pickMedia.launch(new PickVisualMediaRequest.Builder()
//                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
//                .build());
//    }
//
//    public byte[] inputStreamToPNGByteArray(InputStream inputStream) throws IOException {
//        Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
//        ByteArrayOutputStream pngImageStream = new ByteArrayOutputStream();
//        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, pngImageStream);
//        return pngImageStream.toByteArray();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//}
//
