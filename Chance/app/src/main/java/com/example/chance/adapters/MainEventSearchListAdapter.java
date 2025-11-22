package com.example.chance.adapters;

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


/**
 * Adapter for displaying Event cards in the main search RecyclerView.
 * Uses _r_event_pill layout for individual items.
 */
public class MainEventSearchListAdapter extends ListAdapter<Event, MainEventSearchListAdapter.EventViewHolder> {

    /**
     * DiffUtil callback to efficiently determine updates between list versions.
     */
    public static final DiffUtil.ItemCallback<Event> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Event>() {
                /**
                 * Checks if two items are the same entity based on ID.
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
                 * Checks if the visual content of two items matches.
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
     * Constructor initializing the adapter with the Diff callback.
     */
    public MainEventSearchListAdapter() {
        super(DIFF_CALLBACK);
    }

    /**
     * Inflates the item layout and returns a new ViewHolder.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View eventPillView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout._r_event_pill, parent, false);
        return new EventViewHolder(eventPillView);
    }

    /**
     * Binds the event data to the view elements for the specific position.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = getItem(position);
        if (event == null) return;

        holder.title.setText(event.getName());
        holder.description.setText(event.getDescription());

        DataStoreManager.getInstance().getEventBannerFromID(event.getID(), eventBitmap -> {
            holder.banner.setImageBitmap(eventBitmap);
        }, __ -> {});
        holder.itemView.setTag(event.getID());
    }

    /**
     * ViewHolder class to cache view references.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView banner;

        /**
         * Constructor to find and assign UI components.
         */
        public EventViewHolder(@NonNull View eventPillView) {
            super(eventPillView);
            title = eventPillView.findViewById(R.id.event_title);
            description = eventPillView.findViewById(R.id.event_description);
            banner = eventPillView.findViewById(R.id.event_image);

        }
    }
}