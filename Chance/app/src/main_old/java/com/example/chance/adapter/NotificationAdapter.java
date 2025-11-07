package com.example.chance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.google.android.material.button.MaterialButton;
import com.example.chance.model.NotificationItem;

import java.util.List;

/**
 * Adapter for displaying notifications in RecyclerView.
 * Works with notification_item.xml layout.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final Context context;
    private final List<NotificationItem> notifications;
    private final OnNotificationActionListener listener;

    /**
     * Interface for handling notification actions.
     */
    public interface OnNotificationActionListener {
        void onAcceptClicked(NotificationItem notification);
        void onDeclineClicked(NotificationItem notification);
    }

    public NotificationAdapter(Context context, List<NotificationItem> notifications, OnNotificationActionListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem notification = notifications.get(position);

        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        holder.tvTime.setText(notification.getTimeAgo());

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAcceptClicked(notification);
        });

        holder.btnDecline.setOnClickListener(v -> {
            if (listener != null) listener.onDeclineClicked(notification);
        });
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        MaterialButton btnAccept, btnDecline;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_notification_title);
            tvMessage = itemView.findViewById(R.id.tv_notification_message);
            tvTime = itemView.findViewById(R.id.tv_notification_time);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnDecline = itemView.findViewById(R.id.btn_decline);
        }
    }
}
