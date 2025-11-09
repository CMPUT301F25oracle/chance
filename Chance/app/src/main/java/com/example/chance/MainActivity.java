
package com.example.chance;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.View;

import com.example.chance.controller.ChanceState;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.views.Home;
import com.example.chance.views.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.chance.databinding.ActivityMainBinding;
import com.example.chance.views.Authentication;
import com.example.chance.ChanceViewModel;
import com.example.chance.views.QrcodeScanner;
import com.example.chance.views.ViewEvent;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ChanceViewModel chanceViewModel;
    private DataStoreManager dsm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dsm = DataStoreManager.getInstance();

        // hides the default action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // we initially hide the title and navigation bars
        // in case we're going to login screen
        binding.getRoot().findViewById(R.id.title_bar).setVisibility(GONE);
        binding.getRoot().findViewById(R.id.nav_bar).setVisibility(GONE);

        // Load the Login fragment into the content_view container
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_view, new Authentication());
            transaction.commit();
        }
        
        // Set up bottom navigation
        setupNavBar();

        //region: viewmodel callbacks
        chanceViewModel = new ViewModelProvider(this).get(ChanceViewModel.class);

        chanceViewModel.getNewFragment().observe(this, fragmentData -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Class<? extends Fragment> fragmentClass = fragmentData.x;
            Bundle bundle = fragmentData.y;
            try {
                Fragment fragment = fragmentClass.newInstance();
                fragment.setArguments(bundle);
                transaction.replace(R.id.content_view, fragment);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            transaction.commit();
        });
        chanceViewModel.getNavBarVisible().observe(this, visible -> {
            int visibility = visible ? View.VISIBLE : View.GONE;
            binding.getRoot().findViewById(R.id.title_bar).setVisibility(visibility);
            binding.getRoot().findViewById(R.id.nav_bar).setVisibility(visibility);
        });
        chanceViewModel.getEventToOpen().observe(this, eventId -> {
            Bundle bundle = new Bundle();
            bundle.putString("event_id", eventId);
            chanceViewModel.setNewFragment(ViewEvent.class, bundle);
        });
        //endregion: viewmodel callbacks
    }
    
    private void setupNavBar() {
        View navbar = binding.getRoot().findViewById(R.id.nav_bar);
        navbar.findViewById(R.id.navbar_home_button).setOnClickListener((v) -> {
            chanceViewModel.setNewFragment(Home.class, null);
        });
        navbar.findViewById(R.id.navbar_qr_button).setOnClickListener(v -> {
            chanceViewModel.setNewFragment(QrcodeScanner.class, null);
        });
        navbar.findViewById(R.id.navbar_profile_button).setOnClickListener((v) -> {
            chanceViewModel.setNewFragment(Profile.class, null);
        });

        

//        FlexboxLayout navView = binding.getRoot().findViewById(R.id.nav_bar);
//
//        navView.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//
//            if (itemId == R.id.profile_button) {
//                // Navigate to Home fragment
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.content_view, new Login())
//                        .commit();
//                return true;
//            }
////            } else if (itemId == R.id.nav) {
////                // Navigate to Events fragment
////                // getSupportFragmentManager().beginTransaction()
////                //     .replace(R.id.content_view, new Events())
////                //     .commit();
////                return true;
////            }
////            // Add more cases for other menu items
//
//            return false;
    }
    
    // Call this method when user successfully logs in
//    public void showMainContent() {
//        binding.getRoot().findViewById(R.id.title_bar).setVisibility(View.VISIBLE);
//        binding.getRoot().findViewById(R.id.nav_bar).setVisibility(View.VISIBLE);
//
//        // Load the default fragment (Home)
//        getSupportFragmentManager().beginTransaction()
//            .replace(R.id.content_view, new Home())
//            .commit();
//    }

}