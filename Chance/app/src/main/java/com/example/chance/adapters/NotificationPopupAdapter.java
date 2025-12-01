package com.example.chance.adapters;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance.model.Notification;

import java.util.Map;


/**
 * Adapter for displaying User profiles in the multi-purpose search RecyclerView.
 * Manages the list of users shown in the search results.
 */
public class NotificationPopupAdapter extends ListAdapter<Notification, NotificationPopupAdapter.NotificationViewHolder> {

    /**
     * DiffUtil callback to determine changes between User lists efficiently.
     */
    public static final DiffUtil.ItemCallback<Notification> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Notification>() {
            /**
             * Checks if two items are the same User based on ID.
             */
            @Override
            public boolean areItemsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
                // Compare unique IDs if available
                if (oldItem.getID() != null && newItem.getID() != null) {
                    return oldItem.getID().equals(newItem.getID());
                }
                return oldItem == newItem;
            }

            /**
             * Checks if the content of two User items is identical.
             */
            @Override
            public boolean areContentsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
                // Compare relevant fields for UI. Adjust as your model evolves.
                return java.util.Objects.equals(oldItem.getID(), newItem.getID());
            }
        };

    /**
     * Constructor initializing the adapter with the Diff callback.
     */
    public NotificationPopupAdapter() {
        super(DIFF_CALLBACK);
    }

    /**
     * Inflates the profile pill layout and returns a new ViewHolder.
     */
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View notificationPillView = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.notification_popup_pill, parent, false);

        return new NotificationViewHolder(notificationPillView);
    }

    /**
     * Binds the User data to the views in the ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = getItem(position);
        int type = notification.getType();
        Map<String, String> notificationMeta = notification.getMeta();
        if (notification == null) return;
        switch (type) {
            case -1: {
                holder.itemView.setVisibility(View.GONE);
                break;
            }
            default: {
                holder.title.setText(notificationMeta.getOrDefault("title", "No title provided."));
                holder.description.setText(notificationMeta.getOrDefault("description", "Click here to view information."));
                break;
            }
        }
        holder.itemView.setTag(notification);
    }

    /**
     * ViewHolder class to hold references to the profile UI components.
     */
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        /**
         * Constructor to find and assign UI views.
         */
        public NotificationViewHolder(@NonNull View notificationPillView) {
            super(notificationPillView);
            title = notificationPillView.findViewById(R.id.title);
            description = notificationPillView.findViewById(R.id.description);
        }
    }

    /**
     * Utility method to convert density-independent pixels to actual pixels.
     */
    private static int dpToPx(View view, float dpValue) {
        float densityPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, view.getResources().getDisplayMetrics());
        return Math.round(densityPx);
    }
}
