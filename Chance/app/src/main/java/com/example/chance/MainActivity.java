package com.example.chance;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chance.view.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Main entry point - checks authentication status and redirects accordingly.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        Intent intent;
        if (currentUser != null) {
            // User is logged in - go to home (when ready)
            intent = new Intent(this, LoginActivity.class);
        } else {
            // User not logged in - go to login
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}