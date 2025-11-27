package com.example.chance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance.model.User;

import java.util.List;

/**
 * Adapter for displaying users in the admin panel RecyclerView.
 * Provides options to delete users and remove their profile images.
 */
public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private final Context context;
    private final List<User> userList;
    private final OnUserActionListener listener;

    /**
     * Interface for handling user action callbacks.
     */
    public interface OnUserActionListener {
        void onDeleteUser(User user, int position);
        void onRemoveProfileImage(User user, int position);
    }

    /**
     * Constructor for AdminUserAdapter.
     *
     * @param context The context
     * @param userList List of users to display
     * @param listener Listener for user actions
     */
    public AdminUserAdapter(Context context, List<User> userList, OnUserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user, position);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * ViewHolder class for user items in the RecyclerView.
     */
    class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewUserName;
        private final TextView textViewUserEmail;
        private final TextView textViewUserRole;
        private final TextView textViewUserId;
        private final Button buttonDeleteUser;
        private final Button buttonRemoveImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewUserEmail = itemView.findViewById(R.id.textViewUserEmail);
            textViewUserRole = itemView.findViewById(R.id.textViewUserRole);
            textViewUserId = itemView.findViewById(R.id.textViewUserId);
            buttonDeleteUser = itemView.findViewById(R.id.buttonDeleteUser);
            buttonRemoveImage = itemView.findViewById(R.id.buttonRemoveImage);
        }

        /**
         * Binds user data to the view holder.
         *
         * @param user The user to display
         * @param position The position in the list
         */
        public void bind(User user, int position) {
            // Set user information
            textViewUserName.setText(user.getName() != null ? user.getName() : "Unknown User");
            textViewUserEmail.setText(user.getEmail() != null ? user.getEmail() : "No email");
            textViewUserId.setText("ID: " + (user.getUserId() != null ? user.getUserId() : "N/A"));

            // Determine user role
            String role = "Entrant"; // Default role
            if (user.isOrganizer()) {
                role = "Organizer";
            } else if (user.isAdmin()) {
                role = "Administrator";
            }
            textViewUserRole.setText("Role: " + role);

            // Set up delete button
            buttonDeleteUser.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteUser(user, position);
                }
            });

            // Set up remove image button
            buttonRemoveImage.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveProfileImage(user, position);
                }
            });
        }
    }
}