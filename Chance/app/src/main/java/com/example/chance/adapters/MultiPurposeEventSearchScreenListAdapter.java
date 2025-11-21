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
 * Adapter for displaying Event cards in RecyclerView.
 * Used in activity_event_list.xml and event_card.xml layout.
 */
public class MultiPurposeEventSearchScreenListAdapter extends ListAdapter<Event, MultiPurposeEventSearchScreenListAdapter.EventViewHolder> {

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
            && java.util.Objects.equals(oldItem.getDescription(), newItem.getDescription());
        }
    };

    public MultiPurposeEventSearchScreenListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View eventPillView = LayoutInflater
        .from(parent.getContext())
        .inflate(R.layout.multi_purpose_event_search_screen_event_pill, parent, false);

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
            title = eventPillView.findViewById(R.id.title);
            description = eventPillView.findViewById(R.id.description);
            banner = eventPillView.findViewById(R.id.banner);
        }
    }

    private static int dpToPx(View view, float dpValue) {
        float densityPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, view.getResources().getDisplayMetrics());
        return Math.round(densityPx);
    }
}


