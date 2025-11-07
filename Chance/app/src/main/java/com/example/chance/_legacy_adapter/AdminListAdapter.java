package com.example.chance._legacy_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;

import java.util.List;

/**
 * Generic admin list adapter.
 * Works with admin_list_item.xml to render rows for Events, Users, or Images.
 */
public class AdminListAdapter extends RecyclerView.Adapter<AdminListAdapter.AdminViewHolder> {

    public interface OnAdminItemListener {
        void onRowClick(AdminRow item);
        void onDeleteClick(AdminRow item);
    }

    /** Lightweight row model for the Admin panel. */
    public static class AdminRow {
        private final String id;          // Firestore doc id or unique id
        private final String title;       // e.g., "Event: Coding Marathon"
        private final String subtitle;    // e.g., "Created: Nov 5, 2025"
        private final Integer iconResId;  // optional drawable res (nullable)

        public AdminRow(String id, String title, String subtitle, Integer iconResId) {
            this.id = id;
            this.title = title;
            this.subtitle = subtitle;
            this.iconResId = iconResId;
        }
        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getSubtitle() { return subtitle; }
        public Integer getIconResId() { return iconResId; }
    }

    private final Context context;
    private final List<AdminRow> items;
    private final OnAdminItemListener listener;

    public AdminListAdapter(Context context, List<AdminRow> items, OnAdminItemListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.admin_list_item, parent, false);
        return new AdminViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        AdminRow item = items.get(position);

        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubtitle());

        if (item.getIconResId() != null) {
            holder.icon.setImageResource(item.getIconResId());
            holder.icon.setVisibility(View.VISIBLE);
        } else {
            holder.icon.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRowClick(item);
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, subtitle;
        ImageButton btnDelete;

        AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.admin_item_icon);         // from admin_list_item.xml
            title = itemView.findViewById(R.id.admin_item_title);       // from admin_list_item.xml
            subtitle = itemView.findViewById(R.id.admin_item_subtitle); // from admin_list_item.xml
            btnDelete = itemView.findViewById(R.id.btn_delete_item);    // from admin_list_item.xml
        }
    }
}
