package com.example.chance.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance.model.Event;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Binds event data to RecyclerView items in the event list screen.
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.VH> {

    public interface OnEventClick { void onClick(Event event); }

    private final List<Event> data = new ArrayList<>();
    private final OnEventClick onClick;
    private final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

    public EventListAdapter(OnEventClick onClick) { this.onClick = onClick; }

    public void submitList(List<Event> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Event e = data.get(pos);
        h.tvName.setText(e.getName());
        String meta = (e.getLocation() == null ? "" : e.getLocation()) +
                (e.getDate() == null ? "" : " • " + df.format(e.getDate()));
        h.tvLocationDate.setText(meta);
        String priceCap = "$" + String.format("%.2f", e.getPrice()) + " • cap " + e.getCapacity();
        h.tvPriceCapacity.setText(priceCap);
        h.itemView.setOnClickListener(v -> onClick.onClick(e));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvLocationDate, tvPriceCapacity;
        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvLocationDate = itemView.findViewById(R.id.tvLocationDate);
            tvPriceCapacity = itemView.findViewById(R.id.tvPriceCapacity);
        }
    }
}
