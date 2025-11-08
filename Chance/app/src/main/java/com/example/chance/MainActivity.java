
package com.example.chance;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.View;

import com.example.chance.views.Home;
import com.example.chance.views.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.chance.databinding.ActivityMainBinding;
import com.example.chance.views.Authentication;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }
    
    private void setupNavBar() {
        View navbar = binding.getRoot().findViewById(R.id.nav_bar);
        navbar.findViewById(R.id.navbar_home_button).setOnClickListener((v) -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_view, new Home());
            transaction.commit();

        });
        navbar.findViewById(R.id.navbar_profile_button).setOnClickListener((v) -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_view, new Profile());
            transaction.commit();
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