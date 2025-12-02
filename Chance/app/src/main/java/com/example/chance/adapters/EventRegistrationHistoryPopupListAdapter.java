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

import java.util.Map;


/**
 * Adapter for displaying User profiles in the multi-purpose search RecyclerView.
 * Manages the list of users shown in the search results.
 */
public class EventRegistrationHistoryPopupListAdapter extends ListAdapter<Map<String, String>, EventRegistrationHistoryPopupListAdapter.EventRegistrationHistoryPopupViewHolder> {

    /**
     * DiffUtil callback to determine changes between User lists efficiently.
     */
    public static final DiffUtil.ItemCallback<Map<String, String>> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Map<String, String>>() {
            /**
             * Checks if two items are the same User based on ID.
             */
            @Override
            public boolean areItemsTheSame(@NonNull Map oldItem, @NonNull Map newItem) {
                // Compare unique IDs if available
                if (oldItem.get("ID") != null && newItem.get("ID") != null) {
                    return oldItem.get("ID").equals(newItem.get("ID"));
                }
                return oldItem == newItem;
            }

            /**
             * Checks if the content of two User items is identical.
             */
            @Override
            public boolean areContentsTheSame(@NonNull Map oldItem, @NonNull Map newItem) {
                // Compare relevant fields for UI. Adjust as your model evolves.
                return java.util.Objects.equals(oldItem.get("ID"), newItem.get("ID"));
            }
        };

    /**
     * Constructor initializing the adapter with the Diff callback.
     */
    public EventRegistrationHistoryPopupListAdapter() {
        super(DIFF_CALLBACK);
    }

    /**
     * Inflates the profile pill layout and returns a new ViewHolder.
     */
    @NonNull
    @Override
    public EventRegistrationHistoryPopupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View notificationPillView = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.event_registration_history_popup_pill, parent, false);

        return new EventRegistrationHistoryPopupViewHolder(notificationPillView);
    }

    /**
     * Binds the User data to the views in the ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull EventRegistrationHistoryPopupViewHolder holder, int position) {
        Map<String, String> eventHistoryDetails = getItem(position);
        holder.title.setText(eventHistoryDetails.getOrDefault("name", "No event name found."));
        holder.id.setText(eventHistoryDetails.getOrDefault("ID", "No event ID found."));
    }

    /**
     * ViewHolder class to hold references to the profile UI components.
     */
    public static class EventRegistrationHistoryPopupViewHolder extends RecyclerView.ViewHolder {
        TextView title, id;

        /**
         * Constructor to find and assign UI views.
         */
        public EventRegistrationHistoryPopupViewHolder(@NonNull View registrationHistoryPillView) {
            super(registrationHistoryPillView);
            title = registrationHistoryPillView.findViewById(R.id.event_title);
            id = registrationHistoryPillView.findViewById(R.id.event_id);
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
