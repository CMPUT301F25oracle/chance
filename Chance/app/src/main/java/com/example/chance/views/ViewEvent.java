package com.example.chance.views;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chance.ChanceViewModel;
import com.example.chance.R;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.controller.EventController;
import com.example.chance.controller.QRCodeHandler;
import com.example.chance.databinding.ViewEventBinding;
import com.example.chance.model.Event;
import com.example.chance.model.User;
import com.example.chance.views.base.ChanceFragment;
import com.google.rpc.context.AttributeContext;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.encoder.QRCode;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class ViewEvent extends ChanceFragment {
    private ViewEventBinding binding;

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
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            String eventID = bundle.getString("event_id");
            cvm.getEvents().observe(getViewLifecycleOwner(), events -> {
                Event event = events.stream().filter(ev -> Objects.equals(ev.getID(), eventID)).findFirst().orElse(null);
                if (event == null) {
                    dsm.getEvent(eventID, retrieved_event -> {
                        // NOTE: Keeping the organizerButtons visibility check here for existing logic
                        if (retrieved_event.getOrganizerUID().equals(user.getID())) {
                            binding.organizerButtons.setVisibility(VISIBLE);
                        }
                        loadEventInformation(retrieved_event, user);
                    });
                } else {
                    if (event.getOrganizerUID().equals(user.getID())) {
                        binding.organizerButtons.setVisibility(VISIBLE);
                    }
                    loadEventInformation(event, user);
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

        // now we load the events unique QRCode
        Bitmap unique_qrcode;
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
        }, __->{
            // Setup removal when banner fails to load (does not exist)
            setupBannerRemoval(event, user, false);
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

//package com.example.chance.views;
//
//import static android.view.View.INVISIBLE;
//
//import android.graphics.Bitmap;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.example.chance.ChanceViewModel;
//import com.example.chance.R;
//import com.example.chance.controller.DataStoreManager;
//import com.example.chance.controller.QRCodeHandler;
//import com.example.chance.databinding.ViewEventBinding;
//import com.example.chance.model.Event;
//import com.example.chance.model.User;
//import com.example.chance.views.base.ChanceFragment;
//import com.google.rpc.context.AttributeContext;
//import com.google.zxing.WriterException;
//import com.google.zxing.qrcode.encoder.QRCode;
//
//import java.net.URI;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//import java.util.Objects;
//
//
//public class ViewEvent extends ChanceFragment {
//    private ViewEventBinding binding;
//
//    private Drawable buttonBackground;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        binding = ViewEventBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        Bundle bundle = getArguments();
//        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
//            String eventID = bundle.getString("event_id");
//            cvm.getEvents().observe(getViewLifecycleOwner(), events -> {
//                Event event = events.stream().filter(ev -> Objects.equals(ev.getID(), eventID)).findFirst().orElse(null);
//                if (event == null) {
//                    dsm.getEvent(eventID, retrieved_event -> {
//                        if (retrieved_event.getOrganizerUID().equals(user.getID())) {
//                            binding.organizerButtons.setVisibility(INVISIBLE);
//                        }
//                        loadEventInformation(retrieved_event, user);
//                    });
//                } else {
//                    loadEventInformation(event, user);
//                }
//            });
//        });
//    }
//
//    public void loadEventInformation(Event event, User user) {
//        assert event != null;
//        if (event.getWaitingList().contains(user.getID())) {
//            setLotteryButtonAppearance(true);
//        }
//
//        binding.eventName.setText(event.getName());
//        binding.eventInformation.setText(
//                String.format("* %d users currently in waiting list  /  $%.2f per person.\n%s",
//                        event.getWaitingList().size(), event.getPrice(), event.getLocation()));
//        binding.eventOverview.setText(event.getDescription());
//
//        // Format the end date from Firebase
//        String formattedEndDate = formatDate(event.getEndDate());
//
//        // Set availability text with formatted date
//        binding.availabilityText.setText(
//                String.format("The event is now available. You can sign up for the event and wait for a poll for %d candidates until %s.",
//                        event.getMaxInvited(), formattedEndDate));
//
//        // now we load the events unique QRCode
//        Bitmap unique_qrcode;
//        try {
//            unique_qrcode = QRCodeHandler.generateQRCode(event.getID());
//        } catch (WriterException e) {
//            throw new RuntimeException(e);
//        }
//        binding.qrcodeButton.setImageBitmap(unique_qrcode);
//        dsm.getEventBannerFromID(event.getID(), imageBitmap -> {
//            binding.eventBanner.setImageBitmap(imageBitmap);
//        }, __->{});
//        binding.enterLotteryButton.setOnClickListener(__ -> {
//            if (event.getWaitingList().contains(user.getID())) {
//                dsm.event(event).leaveLottery(user);
//                setLotteryButtonAppearance(false);
//            } else {
//                dsm.event(event).enterLottery(user);
//                setLotteryButtonAppearance(true);
//            }
//
//        });
//    }
//
//    private String formatDate(Date date) {
//        if (date == null) {
//            return "the event end date";
//        }
//        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
//        return sdf.format(date);
//    }
//
//    public void setLotteryButtonAppearance(Boolean enteredInLottery) {
//        if (enteredInLottery) {
//            binding.enterLotteryButton.setText("Leave Lottery");
//        } else {
//            binding.enterLotteryButton.setText("Enter Lottery");
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//
//}
//
//
//
//
//
//
////package com.example.chance.views;
////
////import android.graphics.Bitmap;
////import android.graphics.drawable.Drawable;
////import android.net.Uri;
////import android.os.Bundle;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////
////import androidx.annotation.NonNull;
////import androidx.annotation.Nullable;
////import androidx.fragment.app.Fragment;
////import androidx.lifecycle.ViewModelProvider;
////
////import com.example.chance.ChanceViewModel;
////import com.example.chance.R;
////import com.example.chance.controller.DataStoreManager;
////import com.example.chance.controller.QRCodeHandler;
////import com.example.chance.databinding.ViewEventBinding;
////import com.example.chance.model.Event;
////import com.example.chance.model.User;
////import com.google.rpc.context.AttributeContext;
////import com.google.zxing.WriterException;
////import com.google.zxing.qrcode.encoder.QRCode;
////
////import java.net.URI;
////import java.util.Objects;
////
////
////public class ViewEvent extends Fragment {
////    private ViewEventBinding binding;
////    private ChanceViewModel cvm;
////    private DataStoreManager dsm;
////
////    private Drawable buttonBackground;
////
////    @Nullable
////    @Override
////    public View onCreateView(@NonNull LayoutInflater inflater,
////                             @Nullable ViewGroup container,
////                             @Nullable Bundle savedInstanceState) {
////        binding = ViewEventBinding.inflate(inflater, container, false);
////        cvm = new ViewModelProvider(requireActivity()).get(ChanceViewModel.class);
////        dsm = DataStoreManager.getInstance();
////
////
////        return binding.getRoot();
////    }
////
////    @Override
////    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
////        super.onViewCreated(view, savedInstanceState);
////        Bundle bundle = getArguments();
////        cvm.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
////            String eventID = bundle.getString("event_id");
////            cvm.getEvents().observe(getViewLifecycleOwner(), events -> {
////                Event event = events.stream().filter(ev -> Objects.equals(ev.getID(), eventID)).findFirst().orElse(null);
////                if (event == null) {
////                    dsm.getEvent(eventID, retrieved_event -> {
////                        loadEventInformation(retrieved_event, user);
////                    });
////                } else {
////                    loadEventInformation(event, user);
////                }
////            });
////        });
////
////
////    }
////
////
////
////
////
////    public void loadEventInformation(Event event, User user) {
////        assert event != null;
////        if (event.getWaitingList().contains(user.getID())) {
////            setLotteryButtonAppearance(true);
////        }
////
////        binding.eventName.setText(event.getName());
////        binding.eventInformation.setText(
////                String.format("* ? users currently in waiting list  /  $%.2f per person.\n%s", event.getPrice(), event.getLocation()));
////        binding.eventOverview.setText(event.getDescription());
////        // now we load the events unique QRCode
////        Bitmap unique_qrcode;
////        try {
////            unique_qrcode = QRCodeHandler.generateQRCode(event.getID());
////        } catch (WriterException e) {
////            throw new RuntimeException(e);
////        }
////        binding.qrcodeButton.setImageBitmap(unique_qrcode);
////        dsm.getEventBannerFromID(event.getID(), imageBitmap -> {
////            binding.eventBanner.setImageBitmap(imageBitmap);
////        }, __->{});
////        binding.enterLotteryButton.setOnClickListener(__ -> {
////            if (event.getWaitingList().contains(user.getID())) {
////                dsm.event(event).leaveLottery(user);
////                setLotteryButtonAppearance(false);
////            } else {
////                dsm.event(event).enterLottery(user);
////                setLotteryButtonAppearance(true);
////            }
////
////        });
////    }
////
////    public void setLotteryButtonAppearance(Boolean enteredInLottery) {
////        if (enteredInLottery) {
////            binding.enterLotteryButton.setText("Leave Lottery");
////        } else {
////            binding.enterLotteryButton.setText("Enter Lottery");
////        }
////    }
////
////    @Override
////    public void onDestroyView() {
////        super.onDestroyView();
////        binding = null;
////    }
////
////}
//
//
//
