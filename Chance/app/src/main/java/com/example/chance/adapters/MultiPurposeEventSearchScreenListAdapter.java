package com.example.chance.adapters;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.TypedValue;
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
import com.google.android.flexbox.FlexboxLayout;

import java.util.Date;


/**
 * Adapter for displaying Event cards in the multi-purpose search RecyclerView.
 * Handles list updates and view binding for event items.
 */
public class MultiPurposeEventSearchScreenListAdapter extends ListAdapter<Event, MultiPurposeEventSearchScreenListAdapter.EventViewHolder> {

    /**
     * DiffUtil callback to calculate updates between old and new lists.
     */
    public static final DiffUtil.ItemCallback<Event> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Event>() {
                /**
                 * Checks if items refer to the same entity using IDs.
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
                 * Checks if the content of the items matches.
                 */
                @Override
                public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
                    // Compare relevant fields for UI. Adjust as your model evolves.
                    return java.util.Objects.equals(oldItem.getName(), newItem.getName())
                            && java.util.Objects.equals(oldItem.getDescription(), newItem.getDescription());
                }
            };

    /**
     * Constructor initializing the adapter with the Diff callback.
     */
    public MultiPurposeEventSearchScreenListAdapter() {
        super(DIFF_CALLBACK);
    }

    /**
     * Inflates the multi-purpose event pill layout and returns the ViewHolder.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View eventPillView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.multi_purpose_event_search_screen_event_pill, parent, false);

        return new EventViewHolder(eventPillView);
    }

    /**
     * Binds event data to the views in the ViewHolder.
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
     * ViewHolder class to hold UI references for the event item.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView banner;

        /**
         * Initializes UI components from the view.
         */
        public EventViewHolder(@NonNull View eventPillView) {
            super(eventPillView);
            title = eventPillView.findViewById(R.id.title);
            description = eventPillView.findViewById(R.id.description);
            banner = eventPillView.findViewById(R.id.banner);
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