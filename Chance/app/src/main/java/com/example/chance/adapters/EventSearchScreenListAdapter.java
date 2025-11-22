package com.example.chance.adapters;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.model.Event;

import java.util.Date;

/**
 * Adapter for displaying Event cards in RecyclerView.
 * Manages the list of events shown in the search screen.
 */
public class EventSearchScreenListAdapter extends ListAdapter<Event, EventSearchScreenListAdapter.EventViewHolder> {

    /**
     * DiffUtil callback to calculate updates between old and new lists efficiently.
     */
    public static final DiffUtil.ItemCallback<Event> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Event>() {
                /**
                 * Checks if two items represent the same object based on ID.
                 */
                @Override
                public boolean areItemsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
                    // Compare unique IDs if available
                    if (oldItem.getID() != null && newItem.getID() != null) {
                        return oldItem.getID().equals(newItem.getID());
                    }
                    return oldItem == newItem;
                }

                /**
                 * Checks if the content of two items is exactly the same for UI updates.
                 */
                @Override
                public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
                    // Compare relevant fields for UI. Adjust as your model evolves.
                    return java.util.Objects.equals(oldItem.getName(), newItem.getName())
                            && java.util.Objects.equals(oldItem.getDescription(), newItem.getDescription())
                            && java.util.Objects.equals(oldItem.getStartDate(), newItem.getStartDate())
                            && java.util.Objects.equals(oldItem.getEndDate(), newItem.getEndDate());
                }
            };

    /**
     * Constructor initializes the adapter with the DiffUtil callback.
     */
    public EventSearchScreenListAdapter() {
        super(DIFF_CALLBACK);
    }

    /**
     * Inflates the event search layout and creates a new ViewHolder.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View eventPillView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.event_search, parent, false);
        return new EventViewHolder(eventPillView);
    }

    /**
     * Binds data from the Event object to the views in the ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = getItem(position);
        if (event == null) return;

        holder.title.setText(event.getName());
        holder.description.setText(event.getDescription());
        holder.itemView.setTag(event.getID());

        DataStoreManager.getInstance().getEventBannerFromID(event.getID(), eventBitmap -> {
            holder.banner.setImageBitmap(eventBitmap);
        }, __ -> {});
    }

    /**
     * ViewHolder class to hold references to the UI views for a single list item.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView banner;

        /**
         * Initializes the UI components from the provided view.
         */
        public EventViewHolder(@NonNull View eventPillView) {
            super(eventPillView);
            title = eventPillView.findViewById(R.id.event_title);
            description = eventPillView.findViewById(R.id.event_description);
            banner = eventPillView.findViewById(R.id.event_image);
        }
    }
}