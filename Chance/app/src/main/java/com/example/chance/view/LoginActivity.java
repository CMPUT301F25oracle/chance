package com.example.chance.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chance.R;
import com.example.chance.controller.SignUpController;
import com.example.chance.model.SignUp;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Handles user login and registration.
 * Connects to login.xml layout.
 */
public class LoginActivity extends AppCompatActivity {

    // UI Components
    private EditText etUsername, etPassword;
    private Button btnLogin, btnSignUp;

    // Controllers
    private FirebaseAuth auth;
    private SignUpController signUpController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initializeComponents();
        setupListeners();
    }

    /**
     * Initialize UI components and controllers.
     */
    private void initializeComponents() {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        signUpController = new SignUpController();

        // Connect UI elements
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login_button);
        btnSignUp = findViewById(R.id.sign_up_button);
    }

    /**
     * Setup button click listeners.
     */
    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        btnSignUp.setOnClickListener(v -> handleSignUp());
    }

    /**
     * Handle user login with Firebase Authentication.
     */
    private void handleLogin() {
        String email = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Authenticate with Firebase
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Handle new user registration with Firebase Authentication.
     */
    private void handleSignUp() {
        String email = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Firebase Auth account
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    // Create user profile in Firestore
                    SignUp newUser = new SignUp(email, email, "entrant", uid);

                    signUpController.registerUser(newUser,
                            doc -> {
                                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                navigateToHome();
                            },
                            e -> Toast.makeText(this, "Failed to save user profile", Toast.LENGTH_SHORT).show()
                    );
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Navigate to home screen after successful authentication.
     */
    private void navigateToHome() {
        Toast.makeText(this, "Login successful - home screen pending UI", Toast.LENGTH_LONG).show();
        // Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
        // startActivity(intent);
        // finish();
    }
}