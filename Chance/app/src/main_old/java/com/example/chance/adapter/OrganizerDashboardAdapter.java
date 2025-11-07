package com.example.chance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance.model.Event;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * Adapter for displaying organizer's own events in the dashboard.
 * Uses organizer_event_item.xml layout.
 */
public class OrganizerDashboardAdapter extends RecyclerView.Adapter<OrganizerDashboardAdapter.EventViewHolder> {

    private final Context context;
    private final List<Event> events;
    private final OnOrganizerEventClickListener listener;

    /**
     * Interface for handling click actions on buttons in each organizer card.
     */
    public interface OnOrganizerEventClickListener {
        void onManageClicked(Event event);
        void onCreateClicked(Event event);
    }

    public OrganizerDashboardAdapter(Context context, List<Event> events, OnOrganizerEventClickListener listener) {
        this.context = context;
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.organizer_event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);

        holder.tvEventName.setText(event.getName());
        holder.tvWaitlistInfo.setText("Waitlist: " + event.getCapacity() + " seats");

        holder.btnManage.setOnClickListener(v -> {
            if (listener != null) listener.onManageClicked(event);
        });

        holder.btnCreate.setOnClickListener(v -> {
            if (listener != null) listener.onCreateClicked(event);
        });
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvWaitlistInfo;
        MaterialButton btnCreate, btnManage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tv_event_name);
            tvWaitlistInfo = itemView.findViewById(R.id.tv_waitlist_info);
            btnCreate = itemView.findViewById(R.id.btn_event_create);
            btnManage = itemView.findViewById(R.id.btn_event_manage);
        }
    }
}
