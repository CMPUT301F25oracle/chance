
package com.example.chance;

import static android.view.View.GONE;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.chance.databinding.ActivityMainBinding;
import com.example.chance.views.Login;

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
            transaction.replace(R.id.content_view, new Login());
            transaction.commit();
        }
    }

}