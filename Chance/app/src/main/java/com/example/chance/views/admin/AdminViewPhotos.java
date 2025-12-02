package com.example.chance.views.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.adapters.AdminPhotosAdapter;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.AdminViewPhotosBinding;
import com.example.chance.model.EventImage;
import com.example.chance.views.base.ChanceFragment;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminViewPhotos extends ChanceFragment {

    private AdminViewPhotosBinding binding;
    private AdminPhotosAdapter photosAdapter;
    private final List<AdminPhotosAdapter.PhotoItem> photoItems = new ArrayList<>();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = AdminViewPhotosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView setup
        RecyclerView photosRecycler = binding.photosRecyclerView;
        photosAdapter = new AdminPhotosAdapter(requireContext(), photoItems);
        photosRecycler.setAdapter(photosAdapter);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext());
        layoutManager.setFlexDirection(FlexDirection.ROW); // wrap like a grid
        photosRecycler.setLayoutManager(layoutManager);

        loadAllPhotosFromFirebase();

        binding.deleteSelectedPhotosButton.setOnClickListener(v -> deleteSelectedPhotos());
    }

    /**
     * Loads all images from a folder in Firebase Storage.
     * Adjust "profile_images" to your actual folder where you store images.
     */
    private void loadAllPhotosFromFirebase() {
        // 1. Get the DataStoreManager instance
        DataStoreManager dsm = DataStoreManager.getInstance();
        // 2. Clear the list and notify adapter
        photoItems.clear();
        photosAdapter.notifyDataSetChanged();

        // 3. Call the new method
        dsm.getAllEventBanners(new OnSuccessListener<List<EventImage>>() {
            @Override
            public void onSuccess(List<EventImage> eventImages) {
                if (eventImages == null || eventImages.isEmpty()) {
                    return;
                }

                for (EventImage image : eventImages) {
                    photoItems.add(new AdminPhotosAdapter.PhotoItem(
                            image.getEventImage(), // Passing the Base64 string or URL
                            image.getID()          // Passing the ID/Path
                    ));
                }

                // 4. Update UI
                photosAdapter.notifyDataSetChanged();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                android.util.Log.e("AdminViewPhotos", "Error loading banners", e);
            }
        });
    }

    /**
     * Deletes all selected photos from Firebase Storage and removes them from the list.
     */
    private void deleteSelectedPhotos() {
        // 1. Get selected items from the adapter
        List<AdminPhotosAdapter.PhotoItem> selectedItems = photosAdapter.getSelectedItems();
        if (selectedItems.isEmpty()) {
            return; // Nothing selected
        }

        DataStoreManager dsm = DataStoreManager.getInstance();
        List<AdminPhotosAdapter.PhotoItem> itemsToRemove = new ArrayList<>();

        for (AdminPhotosAdapter.PhotoItem item : selectedItems) {

            String documentId = item.storagePath;

            dsm.deleteEventBanner(documentId, aVoid -> {
                // SUCCESS: Add to list of items to remove from UI
                itemsToRemove.add(item);

                if (itemsToRemove.size() == selectedItems.size()) {
                    // Check if fragment is still attached to avoid crashes
                    if (isAdded() && getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            photosAdapter.removeItems(itemsToRemove);
                        });
                    }
                }

            }, e -> {
                android.util.Log.e("AdminViewPhotos", "Failed to delete image: " + documentId, e);
            });
        }

    }

}