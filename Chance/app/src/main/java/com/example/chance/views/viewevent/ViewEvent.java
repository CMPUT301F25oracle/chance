package com.example.chance.views.viewevent;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.chance.R;
import com.example.chance.controller.EventController;
import com.example.chance.controller.QRCodeHandler;
import com.example.chance.databinding.ViewEventBinding;
import com.example.chance.model.Event;
import com.example.chance.model.User;
import com.example.chance.views.Home;
import com.example.chance.views.QRCodePopup;
import com.example.chance.views.base.ChanceFragment;
import com.example.chance.views.base.MultiPurposeProfileSearchScreen;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class ViewEvent extends ChanceFragment {
    private ViewEventBinding binding;
    Bitmap unique_qrcode;

    private Drawable buttonBackground;
    private EventController eventController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ViewEventBinding.inflate(inflater, container, false);
        eventController = new EventController();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.organizerButtons.setVisibility(GONE);

        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            String eventID = meta.getString("eventID");
            if (eventID == null) {
                throw new RuntimeException("Event ID cannot be null");
            }
            cvm.getEvents().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
                @Override
                public void onChanged(List<Event> events) {
                    Event event = events.stream().filter(ev -> Objects.equals(ev.getID(), eventID)).findFirst().orElse(null);
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

        binding.enterLotteryButton.setOnClickListener(__ -> {
            if (event.getWaitingList().contains(user.getID())) {
                dsm.event(event).leaveLottery(user);
                setLotteryButtonAppearance(false);
            } else {
                dsm.event(event).enterLottery(user);
                setLotteryButtonAppearance(true);
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
            dsm.event(event).drawEntrants();
        });

        binding.viewFinalEntrantsButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            ArrayList<String> waitingUsersArrayList = new ArrayList<String>(event.getWaitingList());
            bundle.putStringArrayList("users", waitingUsersArrayList);
            cvm.setNewPopup(MultiPurposeProfileSearchScreen.class, bundle);
        });
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
                        eventController.removeEventBanner(
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

//    private void setupEventRemoval(Event event, User user) {
//        binding.removeEventButton.setOnClickListener(v -> {
//            new AlertDialog.Builder(requireContext())
//                    .setTitle("Remove Event")
//                    .setMessage("Are you sure you want to permanently remove the event? This action is irreversible.")
//                    .setPositiveButton("Remove", (dialog, which) -> {
//                        dsm.removeEvent(event, success -> {
//                            Toast.makeText(requireContext(), "Event removed successfully.", Toast.LENGTH_SHORT).show();
//                            Bundle bundle = new Bundle();
//                            bundle.putBoolean("addToBackStack", false);
//                            cvm.setNewFragment(Home.class, bundle, "circular:300");
//                            //NavHostFragment.findNavController(this).navigate(R.id.navigation_home);
//                        }, failure -> {
//                            Toast.makeText(requireContext(), "Failed to remove event.", Toast.LENGTH_SHORT).show();
//                        });
//                    })
//                    .setNegativeButton(android.R.string.cancel, null)
//                    .show();
//        });
//    }

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