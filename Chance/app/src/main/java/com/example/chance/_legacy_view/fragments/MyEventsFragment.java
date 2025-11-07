package com.example.chance.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance._legacy_adapter.EventListAdapter;
import com.example.chance.controller.FirebaseManager;
import com.example.chance.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays user's events by category: Waiting, Selected, or History.
 */
public class MyEventsFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";
    private String category;
    private RecyclerView recyclerView;
    private EventListAdapter adapter;
    private final List<Event> events = new ArrayList<>();
    private FirebaseManager firebaseManager;

    public static MyEventsFragment newInstance(String category) {
        MyEventsFragment fragment = new MyEventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);
        recyclerView = view.findViewById(R.id.recycler_my_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseManager = FirebaseManager.getInstance();

        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY, "waiting");
        }

        adapter = new EventListAdapter(getContext(), events, event -> {
            // Handle item click (optional)
        });
        recyclerView.setAdapter(adapter);

        loadUserEvents(category);

        return view;
    }

    /**
     * Loads Firestore events for this tab category.
     */
    private void loadUserEvents(String category) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "mockUserId"; // Replace for debug

        String collection = "events"; // Firestore collection name

        // Example logic:
        // waiting  -> events where userId in waitingList[]
        // selected -> events where userId in winners[]
        // history  -> events where event.date < today

        firebaseManager.getDb()
                .collection(collection)
                .get()
                .addOnSuccessListener((QuerySnapshot snapshot) -> {
                    events.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Event event = doc.toObject(Event.class);
                        if (event == null) continue;

                        switch (category.toLowerCase()) {
                            case "waiting":
                                if (event.getDescription() != null && event.getDescription().contains("wait"))
                                    events.add(event);
                                break;
                            case "selected":
                                if (event.getDescription() != null && event.getDescription().contains("select"))
                                    events.add(event);
                                break;
                            case "history":
                                // Simplified example: mark past events manually or check event.date
                                events.add(event);
                                break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load events: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}
