
package com.example.chance;

import static com.google.common.collect.ComparisonChain.start;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;

import com.example.chance.controller.DataStoreManager;
import com.example.chance.model.User;
import com.example.chance.util.Tuple3;
import com.example.chance.views.Home;
import com.example.chance.views.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.chance.databinding.ActivityMainBinding;
import com.example.chance.views.QrcodeScanner;
import com.example.chance.views.SplashScreen;
import com.example.chance.views.ViewEvent;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    private User user;
    private ActivityMainBinding binding;
    private ChanceViewModel chanceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        chanceViewModel = new ViewModelProvider(this).get(ChanceViewModel.class);
        setContentView(binding.getRoot());

        // hides the default action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // we initially hide the title and navigation bars
        // in case we're going to login screen
        chanceViewModel.setLoadMainUI(false);


        // Load the splash screen fragment into the content_view container
        if (savedInstanceState == null) {
            chanceViewModel.setNewFragment(SplashScreen.class, null, "none");
        }

        // Set up bottom navigation
        setupNavBar();


        //region: viewmodel callbacks
        chanceViewModel.getAuthenticationSuccess().observe(this, user -> {
            this.user = user;
            chanceViewModel.setCurrentUser(user);
            // now we load the list of events from firestore
            DataStoreManager.getInstance().getAllEvents((events) -> {
                chanceViewModel.setEvents(events);
            });
        });
        chanceViewModel.getNewFragment().observe(this, this::getNewFragmentCallback);
        chanceViewModel.getLoadMainUI().observe(this, shouldLoad -> {
            // first we add some styling to the main content view
            int visibility;
            int backgroundResource;
            if (shouldLoad) {
                visibility = View.VISIBLE;
                backgroundResource = R.drawable.reusable_main_view_rounding;
            } else {
                visibility = View.GONE;
                backgroundResource = 0;
            }
            binding.contentView.setBackgroundResource(backgroundResource);
            binding.getRoot().findViewById(R.id.title_bar).setVisibility(visibility);
            binding.getRoot().findViewById(R.id.nav_bar).setVisibility(visibility);
        });
        chanceViewModel.getEventToOpen().observe(this, eventId -> {
            Bundle bundle = new Bundle();
            bundle.putString("event_id", eventId);
            chanceViewModel.setNewFragment(ViewEvent.class, bundle, "");
        });
        //endregion
    }

    private void setupNavBar() {
        View navbar = binding.getRoot().findViewById(R.id.nav_bar);
        navbar.findViewById(R.id.navbar_home_button).setOnClickListener((v) -> {
            chanceViewModel.setNewFragment(Home.class, null, "fade");
        });
        navbar.findViewById(R.id.navbar_qr_button).setOnClickListener(v -> {
            chanceViewModel.setNewFragment(QrcodeScanner.class, null, "fade");
        });
        navbar.findViewById(R.id.navbar_profile_button).setOnClickListener((v) -> {
            chanceViewModel.setNewFragment(Profile.class, null, "fade");
        });

    }


    private void getNewFragmentCallback(Tuple3<Class<? extends Fragment>, Bundle, String> fragmentData) {
        Class<? extends Fragment> fragmentClass = fragmentData.x;
        Bundle bundle = fragmentData.y;
        String transitionType = fragmentData.z;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            Fragment fragment = fragmentClass.newInstance();
            fragment.setArguments(bundle);
            animateFragmentTransition(transaction, fragment, transitionType);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void animateFragmentTransition(FragmentTransaction transaction, Fragment newFragment, String transitionType) {
        switch (transitionType) {
            case "fade": {
                transaction.setCustomAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                );
                break;
            }
            case "circular": {
                transaction.setCustomAnimations(R.anim.pop_in, 0);
                break;
            }
        }
        transaction.replace(R.id.content_view, newFragment);
    }

//    private void animateFragmentTransition(FragmentTransaction transaction, Fragment newFragment, String transitionType, OnSuccessListener<Void> onComplete) {
//        // Set animations BEFORE calling replace
//        if ("fade".equals(transitionType)) {
//            transaction.setCustomAnimations(
//                android.R.anim.fade_in,
//                android.R.anim.fade_out
//            );
//        } else if ("circular".equals(transitionType)) {
//            // No XML animations for circular reveal - we'll handle it manually
//            // Using fade_in as a fallback to prevent blank screen
//            transaction.setCustomAnimations(android.R.anim.fade_in, 0);
//        } else if (!"none".equals(transitionType)) {
//            // Default animation for anything other than "none"
//            transaction.setCustomAnimations(
//                android.R.anim.fade_in,
//                android.R.anim.fade_out
//            );
//        }
//
//        // Now replace the fragment
//        transaction.replace(R.id.content_view, newFragment);
//
//        // Apply circular reveal after fragment is attached and laid out
//        if ("circular".equals(transitionType)) {
//            transaction.runOnCommit(() -> {
//                View fragmentView = newFragment.getView();
//                if (fragmentView != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    // Wait for the view to be laid out
//                    fragmentView.post(() -> applyCircularReveal(fragmentView, newFragment));
//                }
//            });
//        }
//
//        if (onComplete != null) {
//            onComplete.onSuccess(null);
//        }
//    }

//    private void applyCircularReveal(View view, Fragment fragment) {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            // Make sure the view has dimensions
//            if (view.getWidth() == 0 || view.getHeight() == 0) {
//                return;
//            }
//
//            Bundle args = fragment.getArguments();
//
//            // Get reveal coordinates from bundle, or use center as default
//            int centerX = view.getWidth() / 2;
//            int centerY = view.getHeight() / 2;
//
//            if (args != null && args.containsKey("reveal_x") && args.containsKey("reveal_y")) {
//                int[] contentLocation = new int[2];
//                view.getLocationOnScreen(contentLocation);
//                centerX = args.getInt("reveal_x") - contentLocation[0];
//                centerY = args.getInt("reveal_y") - contentLocation[1];
//            }
//
//            // Calculate the final radius to cover the entire view
//            float finalRadius = (float) Math.hypot(
//                Math.max(centerX, view.getWidth() - centerX),
//                Math.max(centerY, view.getHeight() - centerY)
//            );
//
//            // Make view visible but invisible initially for the animation
//            view.setVisibility(View.VISIBLE);
//
//            // Create and start the animation
//            android.animation.Animator circularReveal = android.view.ViewAnimationUtils.createCircularReveal(
//                view,
//                centerX,
//                centerY,
//                0,
//                finalRadius
//            );
//            circularReveal.setDuration(500);
//            circularReveal.start();
//        }
//    }
}