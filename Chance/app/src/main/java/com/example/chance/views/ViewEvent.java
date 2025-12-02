package com.example.chance.views;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import com.example.chance.R;
import com.example.chance.controller.QRCodeHandler;
import com.example.chance.databinding.ViewEventBinding;
import com.example.chance.model.Event;
import com.example.chance.model.User;
import com.example.chance.views.base.ChanceFragment;
import com.example.chance.views.base.MultiPurposeProfileSearchScreen;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;


public class ViewEvent extends ChanceFragment {
    private ViewEventBinding binding;
    Bitmap unique_qrcode;

    private String csvContentToSave;

    // NEW: Location tracking fields
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private Event currentEvent; // Store current event for permission callback
    private User currentUser;   // Store current user for permission callback

    private final ActivityResultLauncher<Intent> createFileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        try (OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri)) {
                            if (outputStream != null && csvContentToSave != null) {
                                outputStream.write(csvContentToSave.getBytes());
                                Toast.makeText(getContext(), "Entrants exported successfully.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Failed to export entrants.", Toast.LENGTH_SHORT).show();
                            Log.e("ViewEvent", "Failed to write CSV file.", e);
                        }
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ViewEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.organizerButtons.setVisibility(GONE);

        super.onViewCreated(view, savedInstanceState);

        // NEW: Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        Bundle bundle = getArguments();

        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            currentUser = user; // NEW: Store user for permission callback
            String eventID = meta.getString("eventID");
            if (eventID == null) {
                throw new RuntimeException("Event ID cannot be null");
            }
            cvm.getEvents().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
                @Override
                public void onChanged(List<Event> events) {
                    Event event = events.stream().filter(ev -> Objects.equals(ev.getID(), eventID)).findFirst().orElse(null);
                    currentEvent = event; // NEW: Store event for permission callback
                    loadEventInformation(event, user);
                    if (event.getOrganizerUID().equals(user.getID())) {
                        binding.organizerButtons.setVisibility(VISIBLE);
                    }

                    cvm.getEvents().removeObserver(this);
                }
            });
        });
    }

    public void loadEventInformation(Event event, User user) {
        assert event != null;
        if (event.getWaitingList().contains(user.getID())) {
            setLotteryButtonAppearance(true);
        }

        binding.eventName.setText(event.getName());
        binding.eventInformation.setText(
                String.format("* %d users currently in waiting list  /  $%.2f per person.\n%s",
                        event.getWaitingList().size(), event.getPrice(), event.getLocation()));
        binding.eventOverview.setText(event.getDescription());

        // Format the end date from Firebase
        String formattedEndDate = formatDate(event.getEndDate());

        // Set availability text with formatted date
        binding.availabilityText.setText(
                String.format("The event is now available. You can sign up for the event and wait for a poll for %d candidates until %s.",
                        event.getMaxInvited(), formattedEndDate));

        try {
            unique_qrcode = QRCodeHandler.generateQRCode(event.getID());
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        binding.qrcodeButton.setImageBitmap(unique_qrcode);

        // Load event banner
        dsm.getEventBannerFromID(event.getID(), imageBitmap -> {
            binding.eventBanner.setImageBitmap(imageBitmap);
            // Setup removal when banner successfully loads
            setupBannerRemoval(event, user, true);
        }, __ -> {
            // Setup removal when banner fails to load (does not exist)
            setupBannerRemoval(event, user, false);
        });

        binding.qrcodeButton.setOnClickListener(__ -> {
            Bundle bundle = new Bundle();
            ByteArrayOutputStream qrcodeByteStream = new ByteArrayOutputStream();
            unique_qrcode.compress(Bitmap.CompressFormat.PNG, 100, qrcodeByteStream);
            byte[] qrcodeByteArray = qrcodeByteStream.toByteArray();
            bundle.putByteArray("qrcode_bytes", qrcodeByteArray);
            cvm.setNewPopup(QRCodePopup.class, bundle);
        });

        binding.pollCondition.setOnClickListener(__ -> {
            cvm.setNewPopup(PollConditionPopup.class, null);
        });

        binding.sendNotificationButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("eventName", event.getName());
            bundle.putStringArrayList("waitlistIDS", new ArrayList<>(event.getWaitingList()));
            cvm.setNewPopup(CustomEventNotificationPopup.class, bundle);
        });

        binding.cancelUnregisteredEntrantsButton.setOnClickListener(v -> {
            dsm.event(event).clearWaitingList();
        });

        binding.viewCancelledListButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            ArrayList<String> waitingUsersArrayList = new ArrayList<String>(event.getDeclinedInvite());
            bundle.putStringArrayList("users", waitingUsersArrayList);
            cvm.setNewPopup(MultiPurposeProfileSearchScreen.class, bundle);
        });

        // MODIFIED: Updated lottery button logic to use location
        binding.enterLotteryButton.setOnClickListener(__ -> {
            if (event.getWaitingList().contains(user.getID())) {
                // Leave lottery
                dsm.event(event).leaveLottery(user);
                setLotteryButtonAppearance(false);
                Toast.makeText(requireContext(),
                        "Left the waiting list",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Enter lottery with location
                enterLotteryWithLocation(event, user);
            }
        });

        binding.removeEventButton.setOnClickListener(__ -> {
            dsm.removeEvent(event, success -> {
                Toast.makeText(requireContext(), "Event removed successfully.", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putBoolean("addToBackStack", false);
                cvm.setNewFragment(Home.class, bundle, "fade");
                cvm.setBannerMessage("Event removed successfully.");
            }, failure -> {
                Toast.makeText(requireContext(), "Failed to remove event.", Toast.LENGTH_SHORT).show();
            });
        });

        binding.drawEntrantsButton.setOnClickListener(__ -> {
            dsm.event(event).drawEntrants(completed -> {
                Toast.makeText(requireContext(), "Entrants drawn successfully.", Toast.LENGTH_SHORT).show();
            });
        });

        binding.drawReplacementButton.setOnClickListener(__ -> {
            //dsm.event(event).drawEntrants();
        });

        binding.viewFinalEntrantsButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            ArrayList<String> waitingUsersArrayList = new ArrayList<String>(event.getWaitingList());
            bundle.putStringArrayList("users", waitingUsersArrayList);
            cvm.setNewPopup(MultiPurposeProfileSearchScreen.class, bundle);
        });

        binding.exportFinalEntrantsButton.setOnClickListener(v -> {
            List<String> waitingListIds = event.getWaitingList();
            if (waitingListIds == null || waitingListIds.isEmpty()) {
                Toast.makeText(getContext(), "Waiting list is empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            dsm.getAllUsers(allUsers -> {
                List<User> entrants = allUsers.stream()
                        .filter(u -> waitingListIds.contains(u.getID()))
                        .collect(Collectors.toList());
                exportUsersToCsv(entrants, event.getName());
            }, e -> {
                Toast.makeText(getContext(), "Failed to get user data for export.", Toast.LENGTH_SHORT).show();
                Log.e("ViewEvent", "Failed to fetch all users for CSV export.", e);
            });
        });
        // NEW: View waiting list map button
        binding.viewWaitingListMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), WaitingListMapActivity.class);
            intent.putExtra(WaitingListMapActivity.EXTRA_EVENT_ID, event.getID());
            startActivity(intent);
        });


        // We will implement this method after accept/reject button is implemented
        binding.removeUnregisteredEntrantsButton.setOnClickListener(v -> {

        });
    }

    // NEW: Method to enter lottery with location tracking
    private void enterLotteryWithLocation(Event event, User user) {
        // Check if we have location permission
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
            );
            return;
        }

        // Show loading message
        Toast.makeText(requireContext(), "Getting your location...", Toast.LENGTH_SHORT).show();

        // Get last known location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        Log.d("ViewEvent", "Location obtained: " + latitude + ", " + longitude);

                        // Enter lottery with location
                        dsm.event(event).enterLottery(user, latitude, longitude);
                        setLotteryButtonAppearance(true);

                        Toast.makeText(requireContext(),
                                "Joined waiting list at your current location",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Location is null, try to get current location
                        Log.d("ViewEvent", "Last known location is null, requesting current location");
                        requestCurrentLocation(event, user);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewEvent", "Failed to get location", e);
                    Toast.makeText(requireContext(),
                            "Failed to get location. Please ensure location services are enabled.",
                            Toast.LENGTH_LONG).show();
                });
    }

    // NEW: Method to request current location if last known location is null
    private void requestCurrentLocation(Event event, User user) {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);
        locationRequest.setInterval(0);

        fusedLocationClient.requestLocationUpdates(locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null && locationResult.getLastLocation() != null) {
                            Location location = locationResult.getLastLocation();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            Log.d("ViewEvent", "Current location obtained: " + latitude + ", " + longitude);

                            dsm.event(event).enterLottery(user, latitude, longitude);
                            setLotteryButtonAppearance(true);

                            Toast.makeText(requireContext(),
                                    "Joined waiting list at your current location",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Use default location (0, 0) as fallback
                            Log.w("ViewEvent", "Could not get current location, using default (0, 0)");
                            dsm.event(event).enterLottery(user, 0.0, 0.0);
                            setLotteryButtonAppearance(true);

                            Toast.makeText(requireContext(),
                                    "Joined waiting list (location unavailable)",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, null);
    }

    // NEW: Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, retry entering lottery
                Log.d("ViewEvent", "Location permission granted");
                if (currentEvent != null && currentUser != null) {
                    enterLotteryWithLocation(currentEvent, currentUser);
                }
            } else {
                // Permission denied - inform user
                Log.w("ViewEvent", "Location permission denied");
                new AlertDialog.Builder(requireContext())
                        .setTitle("Location Permission Required")
                        .setMessage("Location permission is required to join the waiting list. This helps organizers manage event capacity and verify attendance.")
                        .setPositiveButton("Try Again", (dialog, which) -> {
                            // User can request permission again
                            if (currentEvent != null && currentUser != null) {
                                enterLotteryWithLocation(currentEvent, currentUser);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            Toast.makeText(requireContext(),
                                    "Cannot join waiting list without location permission",
                                    Toast.LENGTH_LONG).show();
                        })
                        .show();
            }
        }
    }

    // START: BANNER REMOVAL FEATURE - MODIFIED FOR EVERYONE
    private void setupBannerRemoval(Event event, User user, boolean bannerExists) {

        // --- AUTHORIZATION REMOVED: isAuthorized is now always true if bannerExists ---

        if (bannerExists) {
            // Show the button if a banner actually exists, regardless of user role
            binding.removeBannerButton.setVisibility(VISIBLE);
        } else {
            binding.removeBannerButton.setVisibility(INVISIBLE);
        }

        // Set the OnClickListener (Only needs to be done once)
        binding.removeBannerButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Remove Event Banner")
                    .setMessage("Are you sure you want to permanently remove the event banner? This action is irreversible.")
                    .setPositiveButton("Remove", (dialog, which) -> {
                        // Call the controller method
                        dsm.deleteEventBanner(
                                event.getID(),
                                aVoid -> {
                                    // Success callback
                                    Toast.makeText(requireContext(), "Event banner removed successfully.", Toast.LENGTH_SHORT).show();

                                    // Update UI: set banner to placeholder and hide button
                                    binding.eventBanner.setImageResource(R.drawable.placeholder_banner);
                                    binding.removeBannerButton.setVisibility(INVISIBLE);
                                },
                                e -> {
                                    // Failure callback
                                    Log.e("ViewEvent", "Banner removal failed: " + e.getMessage(), e);
                                    Toast.makeText(requireContext(), "Failed to remove banner: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                        );
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        });
    }
    // END: BANNER REMOVAL FEATURE

    private void exportUsersToCsv(List<User> users, String eventName) {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Username,Full Name,Email,Phone Number\n");

        for (User user : users) {
            csvBuilder.append(escapeCsvValue(user.getUsername())).append(",");
            csvBuilder.append(escapeCsvValue(user.getFullName())).append(",");
            csvBuilder.append(escapeCsvValue(user.getEmail())).append(",");
            csvBuilder.append(escapeCsvValue(user.getPhoneNumber())).append("\n");
        }

        csvContentToSave = csvBuilder.toString();

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "entrants-" + eventName.replaceAll("[^a-zA-Z0-9.-]", "_") + ".csv");
        createFileLauncher.launch(intent);
    }

    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "the event end date";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public void setLotteryButtonAppearance(Boolean enteredInLottery) {
        if (enteredInLottery) {
            binding.enterLotteryButton.setText("Leave Lottery");
        } else {
            binding.enterLotteryButton.setText("Enter Lottery");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

/**
 * ==================== ViewEvent.java Comments ====================
 *
 * This file defines the ViewEvent class, a Fragment responsible for displaying
 * the detailed view of a single event. It provides functionality for both
 * attendees and organizers, such as viewing event details, joining or leaving
 * a waiting list (lottery), viewing participants, sending notifications, and
 * managing the event.
 *
 * === ViewEvent Class ===
 * A Fragment that shows all details of a specific event. It handles UI updates
 * based on the user's interaction and role (organizer vs. attendee). It integrates
 * heavily with DataStoreManager to fetch and update event data in Firestore.
 * Recently updated to include location-based services for event sign-ups.
 *
 * --- Instance Variables ---
 * - `currentEvent`, `currentUser`: Hold the state of the event and user being viewed.
 * - `unique_qrcode`: A Bitmap for the event's QR code.
 * - `fusedLocationClient`: The main entry point for interacting with the fused location provider.
 * - `LOCATION_PERMISSION_REQUEST_CODE`: A constant for the location permission request.
 *
 * --- onCreateView & onViewCreated ---
 * Standard Fragment lifecycle methods to inflate the layout, initialize data binding,
 * and set up initial state, including the FusedLocationProviderClient.
 *
 * --- loadDataFromBundle ---
 * Retrieves the event ID from the arguments passed to the fragment and loads the
 * corresponding event and current user data from Firestore. It then calls
 * `loadEventInformation` to populate the UI.
 *
 * --- loadEventInformation Method ---
 * This is the core method for populating the UI with event data. It sets text views,
 * generates and displays the event QR code, and loads the event banner. It also sets
 - up numerous OnClickListeners for all the interactive elements on the screen,
 * such as:
 *  - Entering/leaving the event lottery.
 *  - Viewing the waiting list map (`viewWaitingListMapButton`).
 *  - Sending custom notifications to the waiting list.
 *  - Drawing entrants for the event.
 *  - Exporting the list of final entrants to a CSV file.
 *  - Removing the event (for organizers).
 *
 * --- enterLotteryWithLocation Method ---
 * Handles the logic for a user joining the event's waiting list. It first checks for
 * location permissions. If granted, it attempts to fetch the user's last known location.
 * If successful, it calls the `enterLottery` method in DataStoreManager with the
 * user's coordinates. If not, it triggers a request for the current location.
 *
 * --- requestCurrentLocation Method ---
 * A fallback method that actively requests the device's current location if the last
 * known location was unavailable. It uses a `LocationRequest` to get a single,
 * high-accuracy update. If this also fails, it joins the lottery with default (0,0)
 * coordinates as a final fallback.
 *
 * --- onRequestPermissionsResult Method ---
 * The callback method for the location permission request. If the user grants
 * permission, it retries the `enterLotteryWithLocation` flow. Otherwise, the
 * functionality that requires location is unavailable.
 *
 * --- formatDate Method ---
 * A helper utility to format a Date object into a more readable string ("dd/MM/yyyy").
 *
 * --- setLotteryButtonAppearance Method ---
 * A UI helper method to toggle the appearance and text of the "Enter Lottery" button
 * based on whether the user is currently in the waiting list or not.
 *
 * --- setupBannerRemoval Method ---
 * Configures the long-press listener on the event banner for organizers, allowing
 * them to delete the event's banner image.
 *
 * --- exportUsersToCsv Method ---
 * Gathers user data for the event's waiting list and formats it into a CSV string.
 * It then uses an Intent with `ACTION_CREATE_DOCUMENT` to allow the user to save
 * the CSV file to their device.
 */