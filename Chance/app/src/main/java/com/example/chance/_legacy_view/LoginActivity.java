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
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etName;
    private Button btnLogin, btnRegister;
    private FirebaseAuth auth;
    private SignUpController signUpController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etName = findViewById(R.id.et_name);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();
        signUpController = new SignUpController();

        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void loginUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, EventListActivity.class));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void registerUser() {
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();
                    SignUp user = new SignUp(name, email, "entrant", uid);
                    signUpController.registerUser(user,
                            doc -> Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show(),
                            e -> Toast.makeText(this, "Failed to save user", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
