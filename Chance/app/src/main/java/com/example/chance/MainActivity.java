
package com.example.chance;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.window.OnBackInvokedDispatcher;

import com.example.chance.controller.DataStoreManager;
import com.example.chance.model.Event;
import com.example.chance.model.User;
import com.example.chance.util.Tuple3;
import com.example.chance.views.Authentication;
import com.example.chance.views.Home;
import com.example.chance.views.Profile;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.chance.databinding.ActivityMainBinding;
import com.example.chance.views.QrcodeScanner;
import com.example.chance.views.SplashScreen;
import com.example.chance.views.ViewEvent;
import com.example.chance.views.base.ChanceFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private User user;
    private ActivityMainBinding binding;
    private ChanceViewModel chanceViewModel;

    private List<BackstackFragment> backstackHistory = new ArrayList<>();


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
            List<Event> emptyList = new ArrayList<>();
            chanceViewModel.setEvents(emptyList);
            DataStoreManager.getInstance().getAllEvents((events) -> {
                chanceViewModel.setEvents(events);
            });
        });
        chanceViewModel.getNewFragment().observe(this, this::getNewFragmentCallback);
        chanceViewModel.getLoadMainUI().observe(this, shouldLoad -> {
            backstackHistory.clear();
            // first we add some styling to the main content view
            int visibility;
            int backgroundResource;
            // used to make sure we don't overlap the android status bar
            //int titleBarOffset = insets.getInsetsIgnoringVisibility(WindowInsets.Type.statusBars()).top;
            if (shouldLoad) {
                visibility = VISIBLE;
                backgroundResource = R.drawable.reusable_view_rounding;
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
            bundle.putString("eventID", eventId);
            chanceViewModel.setNewFragment(ViewEvent.class, bundle, "");
        });
        //endregion

        //region: back button handler
        OnBackPressedCallback backCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                int fragmentHistorySize = backstackHistory.size();
                if (fragmentHistorySize > 0) {
                    BackstackFragment nextBackstackFragment = backstackHistory.removeLast();
                    Class<? extends ChanceFragment> fragmentClass = nextBackstackFragment.fragmentClass;
                    Bundle metaBundle = nextBackstackFragment.metaBundle;
                    metaBundle.putBoolean("addToBackStack", false);
                    chanceViewModel.setNewFragment(fragmentClass, metaBundle, "fade");
                } else {
                    finish();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, backCallback);
        //endregion
    }



    private void setupNavBar() {
        View navbar = binding.getRoot().findViewById(R.id.nav_bar);
        navbar.findViewById(R.id.navbar_home_button).setOnClickListener((v) -> {
            chanceViewModel.setNewFragment(Home.class, null, "fade");
        });
        navbar.findViewById(R.id.navbar_qr_button).setOnClickListener(v -> {
            chanceViewModel.setNewFragment(QrcodeScanner.class, null, "circular:300");
        });
        navbar.findViewById(R.id.navbar_profile_button).setOnClickListener((v) -> {
            chanceViewModel.setNewFragment(Profile.class, null, "fade");
        });

    }


    private void getNewFragmentCallback(Tuple3<Class<? extends ChanceFragment>, Bundle, String> fragmentData) {
        Class<? extends ChanceFragment> fragmentClass = fragmentData.x;
        Bundle bundle = fragmentData.y;
        String transitionType = fragmentData.z;

        //region: !!!TEMPORARY FIX!!!
        if (bundle == null) {
            bundle = new Bundle();
        }
        //endregion

        boolean addToBackStack = bundle.getBoolean("addToBackStack", true);

        ChanceFragment currentFragment = (ChanceFragment) getSupportFragmentManager().findFragmentById(R.id.content_view);
        if (currentFragment != null && addToBackStack) {
            Class<? extends ChanceFragment> currentFragmentClass = currentFragment.getClass();
            backstackHistory.addLast(new BackstackFragment(currentFragmentClass, currentFragment.meta));
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            ChanceFragment fragment = fragmentClass.newInstance();
            fragment.meta = bundle;

            fragment.setArguments(bundle);
            animateFragmentTransition(transaction, (ChanceFragment) fragment, transitionType);
            // commit MUST always occur here for consistency
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set of custom animations for fragment transitions
     * @param transaction
     * @param newFragment
     * @param transitionType
     */
    private void animateFragmentTransition(FragmentTransaction transaction, ChanceFragment newFragment, String transitionType) {
        // we need to parse transitionType in case it mentions time in milliseconds
        String[] transitionTypeComponents = transitionType.split(":");
        String transition = transitionTypeComponents[0];
        int duration = 500;
        if (transitionTypeComponents.length > 1) {
            duration = Integer.parseInt(transitionTypeComponents[1]);
        }

        switch (transition) {
            case "fade": {
                transaction.setCustomAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                );
                transaction.replace(R.id.content_view, newFragment);
                newFragment.chanceEnterTransitionComplete();
                break;
            }
            case "circular": {
                Log.d("Circular", "We're alive");
                circularRevealAnimation(transaction, newFragment, duration);
                break;
            }
            default: {
                transaction.replace(R.id.content_view, newFragment);
                newFragment.chanceEnterTransitionComplete();
                break;
            }
        }
        // make sure any waiting fragment code is executed now

    }

    private void circularRevealAnimation(FragmentTransaction transaction, ChanceFragment newFragment, int duration) {
        ChanceFragment currentFragment = (ChanceFragment) getSupportFragmentManager().findFragmentById(R.id.content_view);
        transaction.add(R.id.content_view, newFragment);

        View mainView = findViewById(R.id.content_view);
        int viewCenterX = mainView.getWidth() / 2;
        int viewCenterY = mainView.getHeight() / 2;
        float finalRadius = (float) Math.hypot(viewCenterX, viewCenterY);

        transaction.runOnCommit(() -> {
            View newFragmentView = newFragment.getView();
            newFragmentView.setVisibility(INVISIBLE);
            mainView.post(() -> {
                android.animation.Animator circularReveal = android.view.ViewAnimationUtils.createCircularReveal(
                        newFragment.getView()
                        ,viewCenterX
                        ,viewCenterY
                        ,0
                        ,finalRadius
                );
                circularReveal.setDuration(duration);
                circularReveal.addListener(new android.animation.Animator.AnimatorListener() {
                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
                        newFragment.chanceEnterTransitionComplete();
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {
                    }
                });
                circularReveal.start();
                newFragmentView.setVisibility(VISIBLE);
            });

        });

    }


    private static class BackstackFragment {
        public Class<? extends ChanceFragment> fragmentClass;
        public Bundle metaBundle;

        BackstackFragment(Class<? extends ChanceFragment> fragmentClass, Bundle metaBundle) {
            this.fragmentClass = fragmentClass;
            this.metaBundle = metaBundle;
        }
    }
}