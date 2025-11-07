package com.example.chance.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance._legacy_adapter.NotificationAdapter;
import com.example.chance.model.NotificationItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays all user notifications (lottery results, updates, etc.).
 */
public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationItem> notifications;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recyclerView = findViewById(R.id.recycler_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadMockNotifications();
    }

    private void loadMockNotifications() {
        notifications = new ArrayList<>();
        notifications.add(new NotificationItem("1", "Lottery Result", "You were selected!", "2 hours ago"));
        notifications.add(new NotificationItem("2", "Event Update", "New location announced.", "Yesterday"));

        adapter = new NotificationAdapter(this, notifications, new NotificationAdapter.OnNotificationActionListener() {
            @Override
            public void onAcceptClicked(NotificationItem notification) {
                Toast.makeText(NotificationActivity.this, "Accepted: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeclineClicked(NotificationItem notification) {
                Toast.makeText(NotificationActivity.this, "Declined: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }
}
