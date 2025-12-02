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
        binding.submitButton.setActivated(true);
        binding.submitButton.setText("Create Event");

        // Request location permission when the view is created
        requestLocationPermission();

        binding.addBannerButton.setOnClickListener(__ -> {
            promptImageFromUser();
            Log.d("CreateEvent", "Add banner clicked");
        });

        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            binding.submitButton.setOnClickListener(v -> {
                binding.submitButton.setActivated(false);
                binding.submitButton.setText("Please Wait...");
                String event_name = binding.eventNameInput.getText().toString();
                String event_address = binding.eventAddressInput.getText().toString();
                String max_candidates_str = binding.candidateMaximumInput.getText().toString();
                String max_waitinglist_str = binding.waitinglistRestriction.getText().toString();
                String attendance_price_str = binding.priceInput.getText().toString();
                String event_description = binding.descriptionInput.getText().toString();

                if (TextUtils.isEmpty(event_name) || TextUtils.isEmpty(event_address) ||
                        TextUtils.isEmpty(max_candidates_str) || TextUtils.isEmpty(attendance_price_str) || TextUtils.isEmpty(event_description)) {
                    cvm.setBannerMessage("Please fill out all required fields");
                    return;
                }

                Calendar calendar = Calendar.getInstance();

                int maximum_candidates = Integer.parseInt(max_candidates_str);
                int maximum_waitinglist;
                if (max_waitinglist_str.contains("")) {
                    maximum_waitinglist = Integer.MAX_VALUE;
                }
                else {
                    maximum_waitinglist = Integer.parseInt(max_waitinglist_str);
                }
                float attendance_price = Float.parseFloat(attendance_price_str);

                DatePicker event_reg_start = binding.eventStartInput;
                calendar.set(event_reg_start.getYear(), event_reg_start.getMonth(), event_reg_start.getDayOfMonth());
                Date event_start_calendar = calendar.getTime();

                DatePicker event_reg_end = binding.eventEndInput;
                calendar.set(event_reg_end.getYear(), event_reg_end.getMonth(), event_reg_end.getDayOfMonth());
                Date event_end_calendar = calendar.getTime();

                DatePicker reg_start = binding.registrationStartInput;
                calendar.set(reg_start.getYear(), reg_start.getMonth(), reg_start.getDayOfMonth());
                Date reg_start_calendar = calendar.getTime();

                DatePicker reg_end = binding.registrationEndInput;
                calendar.set(reg_end.getYear(), reg_end.getMonth(), reg_end.getDayOfMonth());
                Date reg_end_calendar = calendar.getTime();

                if (event_end_calendar.before(event_start_calendar) || reg_end_calendar.before(reg_start_calendar)) {
                    cvm.setBannerMessage("End date must be after start date");
                    return;
                }

                Event new_event = new Event(event_name, event_address, maximum_candidates, attendance_price, event_description, event_start_calendar, event_end_calendar, user.getID(), maximum_waitinglist);
                new_event.setOrganizerUID(user.getID());

                cvm.getEvents().observe(getViewLifecycleOwner(), events -> {
                    for (Event event : events) {
                        Log.d("CreateEvent", "Event ID: " + event.getID());
                        if (event.getID().equals(new_event.getID())) {
                            cvm.setNewFragment(Home.class, null, "");
                            return;
                        }
                    }
                });

                dsm.event(new_event).create(none -> {
                    if (selectedEventBanner != null) {
                        selectedEventBanner.setID(new_event.getID());
                        dsm.eventImage(selectedEventBanner).save((__)->{
                        }, (__)->{});
                    }
                }, e -> {});

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
