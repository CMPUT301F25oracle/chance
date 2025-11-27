package com.example.chance.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance.adapter.AdminUserAdapter;
import com.example.chance.controller.AdminController;
import com.example.chance.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for administrators to view and manage all users in the system.
 * Displays a list of users with options to delete them.
 */
public class AdminViewUsersActivity extends AppCompatActivity implements AdminUserAdapter.OnUserActionListener {

    private static final String TAG = "AdminViewUsersActivity";

    private RecyclerView recyclerViewUsers;
    private AdminUserAdapter userAdapter;
    private ProgressBar progressBar;

    private AdminController adminController;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_users);

        // Initialize controller
        adminController = new AdminController();

        // Initialize UI components
        initializeViews();

        // Load users from database
        loadUsers();
    }

    /**
     * Initializes all UI components and sets up the RecyclerView.
     */
    private void initializeViews() {
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        progressBar = findViewById(R.id.progressBar);

        // Set up RecyclerView
        userList = new ArrayList<>();
        userAdapter = new AdminUserAdapter(this, userList, this);

        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);

        // Set up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Users");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Loads all users from the database via AdminController.
     */
    private void loadUsers() {
        showLoading(true);

        adminController.getAllUsers(new AdminController.UserListCallback() {
            @Override
            public void onSuccess(List<User> users) {
                showLoading(false);
                userList.clear();
                userList.addAll(users);
                userAdapter.notifyDataSetChanged();

                if (users.isEmpty()) {
                    Toast.makeText(AdminViewUsersActivity.this,
                            "No users found", Toast.LENGTH_SHORT).show();
                }

                Log.d(TAG, "Loaded " + users.size() + " users");
            }

            @Override
            public void onFailure(String errorMessage) {
                showLoading(false);
                Toast.makeText(AdminViewUsersActivity.this,
                        "Error loading users: " + errorMessage, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to load users: " + errorMessage);
            }
        });
    }

    /**
     * Shows or hides the loading indicator.
     *
     * @param show true to show loading, false to hide
     */
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewUsers.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * Called when the delete button is clicked for a user.
     * Shows a confirmation dialog before proceeding with deletion.
     *
     * @param user The user to delete
     * @param position The position of the user in the list
     */
    @Override
    public void onDeleteUser(User user, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + user.getName() + "?\n\n" +
                        "This action cannot be undone and will remove all user data.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteUser(user, position);
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Performs the actual user deletion after confirmation.
     *
     * @param user The user to delete
     * @param position The position of the user in the list
     */
    private void deleteUser(User user, int position) {
        showLoading(true);

        adminController.deleteUser(user.getUserId(), new AdminController.DeleteCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    showLoading(false);
                    userList.remove(position);
                    userAdapter.notifyItemRemoved(position);
                    Toast.makeText(AdminViewUsersActivity.this,
                            "User deleted successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Successfully deleted user: " + user.getUserId());
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(AdminViewUsersActivity.this,
                            "Failed to delete user: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to delete user: " + errorMessage);
                });
            }
        });
    }

    /**
     * Called when the remove profile image button is clicked.
     *
     * @param user The user whose profile image to remove
     * @param position The position of the user in the list
     */
    @Override
    public void onRemoveProfileImage(User user, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Profile Image")
                .setMessage("Remove profile image for " + user.getName() + "?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    removeProfileImage(user, position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Removes only the profile image of a user without deleting the account.
     *
     * @param user The user whose profile image to remove
     * @param position The position of the user in the list
     */
    private void removeProfileImage(User user, int position) {
        adminController.removeUserProfileImageOnly(user.getUserId(),
                new AdminController.DeleteCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(AdminViewUsersActivity.this,
                                    "Profile image removed successfully", Toast.LENGTH_SHORT).show();
                            // Optionally refresh the user data or update the UI
                            userAdapter.notifyItemChanged(position);
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(AdminViewUsersActivity.this,
                                    "Failed to remove image: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}