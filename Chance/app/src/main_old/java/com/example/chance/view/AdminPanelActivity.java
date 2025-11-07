package com.example.chance.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance.adapter.AdminListAdapter;
import com.example.chance.adapter.AdminListAdapter.AdminRow;
import com.example.chance.controller.EventController;
import com.example.chance.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin dashboard for managing events and users.
 */
public class AdminPanelActivity extends AppCompatActivity {

    private RecyclerView recyclerAdmin;
    private AdminListAdapter adapter;
    private EventController eventController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        recyclerAdmin = findViewById(R.id.recycler_admin);
        recyclerAdmin.setLayoutManager(new LinearLayoutManager(this));

        eventController = new EventController();
        loadAdminEvents();
    }

    private void loadAdminEvents() {
        eventController.getAllEvents(events -> {
            List<AdminRow> adminRows = new ArrayList<>();
            for (Event event : events) {
                adminRows.add(new AdminRow(
                        event.getId(),
                        event.getName(),
                        "Capacity: " + event.getCapacity(),
                        R.drawable.ic_event
                ));
            }

            adapter = new AdminListAdapter(this, adminRows, new AdminListAdapter.OnAdminItemListener() {
                @Override
                public void onRowClick(AdminRow item) {
                    Toast.makeText(AdminPanelActivity.this, "Clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDeleteClick(AdminRow item) {
                    eventController.deleteEvent(item.getId(),
                            v -> Toast.makeText(AdminPanelActivity.this, "Deleted " + item.getTitle(), Toast.LENGTH_SHORT).show(),
                            e -> Toast.makeText(AdminPanelActivity.this, "Delete failed", Toast.LENGTH_SHORT).show());
                }
            });

            recyclerAdmin.setAdapter(adapter);
        }, e -> Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show());
    }
}
