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
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.example.chance.R;
import com.example.chance.controller.EventController;
import com.example.chance.controller.QRCodeHandler;
import com.example.chance.databinding.ViewEventBinding;
import com.example.chance.model.Event;
import com.example.chance.model.User;
import com.example.chance.views.base.ChanceFragment;
import com.example.chance.views.base.MultiPurposeProfileSearchScreen;
import com.google.android.gms.location.FusedLocationProviderClient;
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

    private Drawable buttonBackground;
    private EventController eventController;
    private String csvContentToSave;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Event currentEvent;
    private User currentUser;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            joinLotteryWithLocation();
        } else {
            showErrorDialog("Location Permission Required", "Location permission is required to join the waiting list.");
        }
    });

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
                            showErrorDialog("Export Failed", "Failed to write CSV file: " + e.getMessage());
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
        eventController = new EventController();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.organizerButtons.setVisibility(GONE);

        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            currentUser = user;
            String eventID = meta.getString("eventID");
            if (eventID == null) {
                throw new RuntimeException("Event ID cannot be null");
            }
            cvm.getEvents().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
                @Override
                public void onChanged(List<Event> events) {
                    Event event = events.stream().filter(ev -> ev != null && Objects.equals(ev.getID(), eventID)).findFirst().orElse(null);
                    currentEvent = event;
                    if (event != null) {
                        loadEventInformation(event, user);
                        if (event.getOrganizerUID().equals(user.getID())) {
                            binding.organizerButtons.setVisibility(VISIBLE);
                        }
                    }

                    cvm.getEvents().removeObserver(this);
                }
            });
        });
    }

    public void loadEventInformation(Event event, User user) {
        assert event != null;
        boolean isUserInWaitingList = event.getWaitingList().stream().anyMatch(entry -> entry.getUserId().equals(user.getID()));
        setLotteryButtonAppearance(isUserInWaitingList);

        binding.eventName.setText(event.getName());
        binding.eventInformation.setText(
                String.format("* %d users currently in waiting list  /  $%.2f per person.\n%s",
                        event.viewWaitingListEntrantsCount(), event.getPrice(), event.getLocation()));
        binding.eventOverview.setText(event.getDescription());

        String formattedEndDate = formatDate(event.getEndDate());

        binding.availabilityText.setText(
                String.format("The event is now available. You can sign up for the event and wait for a poll for %d candidates until %s.",
                        event.getMaxInvited(), formattedEndDate));

        try {
            unique_qrcode = QRCodeHandler.generateQRCode(event.getID());
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        binding.qrcodeButton.setImageBitmap(unique_qrcode);

        dsm.getEventBannerFromID(event.getID(), imageBitmap -> {
            binding.eventBanner.setImageBitmap(imageBitmap);
            setupBannerRemoval(event, user, true);
        }, __ -> {
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


        binding.enterLotteryButton.setOnClickListener(__ -> {
            boolean isUserInList = currentEvent.getWaitingList().stream().anyMatch(entry -> entry.getUserId().equals(currentUser.getID()));
            if (isUserInList) {
                dsm.leaveWaitingList(currentEvent, currentUser.getID(), aVoid -> {
                    setLotteryButtonAppearance(false);
                }, e -> {
                    showErrorDialog("Error Leaving Lottery", "Could not leave the waiting list. Please try again. Firebase error: " + e.getMessage());
                });
            } else {
                joinLotteryWithLocation();
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
                showErrorDialog("Error Removing Event", "Failed to remove the event: " + failure.getMessage());
            });
        });

        binding.drawEntrantsButton.setOnClickListener(__ -> {
            dsm.event(event).drawEntrants();
        });

        binding.drawReplacementButton.setOnClickListener(__ -> {
            dsm.event(event).drawEntrants();
        });

        binding.viewFinalEntrantsButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            ArrayList<String> waitingUsersArrayList = new ArrayList<>(event.viewWaitingListEntrants());
            bundle.putStringArrayList("users", waitingUsersArrayList);
            cvm.setNewPopup(MultiPurposeProfileSearchScreen.class, bundle);
        });

        binding.exportFinalEntrantsButton.setOnClickListener(v -> {
            List<String> waitingListIds = event.viewWaitingListEntrants();
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
                showErrorDialog("Export Error", "Could not get user data for export: " + e.getMessage());
                Log.e("ViewEvent", "Failed to fetch all users for CSV export.", e);
            });
        });

        binding.viewOnMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OrganizerMapView.class);
            intent.putExtra("eventID", event.getID());
            startActivity(intent);
        });

        binding.removeUnregisteredEntrantsButton.setOnClickListener(v -> {

        });
    }

    private void joinLotteryWithLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    dsm.joinWaitingList(currentEvent, currentUser.getID(), location.getLatitude(), location.getLongitude(), aVoid -> {
                        setLotteryButtonAppearance(true);
                    }, e -> {
                        showErrorDialog("Error Joining Lottery", "Could not join the waiting list. Please try again. Firebase error: " + e.getMessage());
                    });
                } else {
                    showErrorDialog("Location Error", "Could not retrieve your location. Please ensure location services are enabled and try again.");
                }
            });
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void setupBannerRemoval(Event event, User user, boolean bannerExists) {

        if (bannerExists) {
            binding.removeBannerButton.setVisibility(VISIBLE);
        } else {
            binding.removeBannerButton.setVisibility(INVISIBLE);
        }

        binding.removeBannerButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Remove Event Banner")
                    .setMessage("Are you sure you want to permanently remove the event banner? This action is irreversible.")
                    .setPositiveButton("Remove", (dialog, which) -> {
                        eventController.removeEventBanner(
                                event.getID(),
                                aVoid -> {
                                    Toast.makeText(requireContext(), "Event banner removed successfully.", Toast.LENGTH_SHORT).show();
                                    binding.eventBanner.setImageResource(R.drawable.placeholder_banner);
                                    binding.removeBannerButton.setVisibility(INVISIBLE);
                                },
                                e -> {
                                    showErrorDialog("Banner Removal Failed", "Could not remove banner: " + e.getMessage());
                                    Log.e("ViewEvent", "Banner removal failed: " + e.getMessage(), e);
                                }
                        );
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        });
    }

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

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
