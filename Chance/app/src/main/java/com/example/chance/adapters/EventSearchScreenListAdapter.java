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
 * Used in activity_event_list.xml and event_card.xml layout.
 */
public class EventSearchScreenListAdapter extends ListAdapter<Event, EventSearchScreenListAdapter.EventViewHolder> {

    public static final DiffUtil.ItemCallback<Event> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Event>() {
                @Override
                public boolean areItemsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
                    // Compare unique IDs if available
                    if (oldItem.getID() != null && newItem.getID() != null) {
                        return oldItem.getID().equals(newItem.getID());
                    }
                    return oldItem == newItem;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
                    // Compare relevant fields for UI. Adjust as your model evolves.
                    return java.util.Objects.equals(oldItem.getName(), newItem.getName())
                            && java.util.Objects.equals(oldItem.getDescription(), newItem.getDescription())
                            && java.util.Objects.equals(oldItem.getStartDate(), newItem.getStartDate())
                            && java.util.Objects.equals(oldItem.getEndDate(), newItem.getEndDate());
                }
            };

    public EventSearchScreenListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View eventPillView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.event_search, parent, false);
        return new EventViewHolder(eventPillView);
    }

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

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView banner;

        public EventViewHolder(@NonNull View eventPillView) {
            super(eventPillView);
            title = eventPillView.findViewById(R.id.event_title);
            description = eventPillView.findViewById(R.id.event_description);
            banner = eventPillView.findViewById(R.id.event_image);
        }
    }
}
