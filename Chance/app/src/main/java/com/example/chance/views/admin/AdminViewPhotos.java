package com.example.chance.views.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.adapters.AdminPhotosAdapter;
import com.example.chance.databinding.AdminViewPhotosBinding;
import com.example.chance.views.base.ChanceFragment;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
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
        StorageReference imagesRef = storage.getReference().child("profile_images");

        imagesRef.listAll()
                .addOnSuccessListener((ListResult listResult) -> {
                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            // item.getPath() gives something like "profile_images/username"
                            photoItems.add(new AdminPhotosAdapter.PhotoItem(
                                    uri.toString(),
                                    item.getPath()
                            ));
                            photosAdapter.notifyItemInserted(photoItems.size() - 1);
                        });
                    }
                });
        // Optional: addOnFailureListener for error handling.
    }

    /**
     * Deletes all selected photos from Firebase Storage and removes them from the list.
     */
    private void deleteSelectedPhotos() {
        List<AdminPhotosAdapter.PhotoItem> selected = photosAdapter.getSelectedItems();
        if (selected.isEmpty()) {
            return; // nothing selected
        }

        List<AdminPhotosAdapter.PhotoItem> toDelete = new ArrayList<>(selected);
        List<AdminPhotosAdapter.PhotoItem> successfullyDeleted = new ArrayList<>();
        AtomicInteger remaining = new AtomicInteger(toDelete.size());

        for (AdminPhotosAdapter.PhotoItem item : toDelete) {
            StorageReference ref = storage.getReference().child(item.storagePath);
            ref.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    successfullyDeleted.add(item);
                } else {
                    // Optional: show toast/log error
                }

                if (remaining.decrementAndGet() == 0) {
                    // All tasks are complete
                    if (!successfullyDeleted.isEmpty()) {
                        photosAdapter.removeItems(successfullyDeleted);
                    }
                }
            });
        }
    }
}