package com.example.chance.view;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chance.R;
import com.example.chance.controller.ProfileController;
import com.example.chance.model.Entrant;

/**
 * Displays user's profile information.
 */
public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvDeviceId;
    private ProfileController profileController;
    private String userId = "mockEntrant"; // Replace with FirebaseAuth.getUid() later

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        tvDeviceId = findViewById(R.id.tv_profile_device);

        profileController = new ProfileController();
        loadProfile();
    }

    private void loadProfile() {
        profileController.getEntrant(userId, entrant -> {
            if (entrant != null) {
                tvName.setText(entrant.getName());
                tvEmail.setText(entrant.getEmail());
                tvDeviceId.setText(entrant.getDeviceId());
            }
        }, e -> Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show());
    }
}
