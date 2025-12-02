package com.example.chance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chance.R;

import java.util.ArrayList;
import java.util.List;

public class AdminPhotosAdapter extends RecyclerView.Adapter<AdminPhotosAdapter.PhotoViewHolder> {

    public static class PhotoItem {
        public String url;
        public String storagePath;
        public boolean selected;

        public PhotoItem(String url, String storagePath) {
            this.url = url;
            this.storagePath = storagePath;
            this.selected = false;
        }
    }

    private final Context context;
    private final List<PhotoItem> photos;

    public AdminPhotosAdapter(Context context, List<PhotoItem> photos) {
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        PhotoItem item = photos.get(position);

        try {
            // 1. Check if the "url" field is actually a Base64 string (which it is now)
            byte[] decodedString = android.util.Base64.decode(item.url, android.util.Base64.DEFAULT);

            // 2. Convert bytes into a Bitmap image
            android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            // 3. Set the bitmap directly to the ImageView
            holder.imageView.setImageBitmap(decodedByte);

        } catch (Exception e) {
            // Fallback: If it fails (e.g. maybe it IS a real URL in some legacy cases), try Glide
            Glide.with(context)
                    .load(item.url)
                    .into(holder.imageView);
        }

        holder.selectionIndicator.setVisibility(item.selected ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            item.selected = !item.selected;
            notifyItemChanged(holder.getBindingAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public List<PhotoItem> getSelectedItems() {
        List<PhotoItem> selected = new ArrayList<>();
        for (PhotoItem item : photos) {
            if (item.selected) {
                selected.add(item);
            }
        }
        return selected;
    }

    public void removeItems(List<PhotoItem> toRemove) {
        photos.removeAll(toRemove);
        notifyDataSetChanged();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView selectionIndicator;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_photo);
            selectionIndicator = itemView.findViewById(R.id.image_selected_indicator);
        }
    }
}

/**
 * ==================== AdminPhotosAdapter.java Comments ====================
 *
 * === AdminPhotosAdapter Class ===
 * Adapter for displaying photos in a RecyclerView for administrative purposes.
 * This adapter handles the display of images, which can be loaded from a URL or a Base64 encoded string.
 * It also supports single-item selection.
 *
 * === PhotoItem Inner Class ===
 * Represents a single photo item in the RecyclerView.
 * Contains the image URL or Base64 string, the storage path, and its selection state.
 *
 * --- PhotoItem Constructor ---
 * Constructs a new PhotoItem.
 * @param url The URL or Base64 string of the photo.
 * @param storagePath The storage path of the photo.
 *
 * === AdminPhotosAdapter Constructor ===
 * Constructs the AdminPhotosAdapter.
 * @param context The context for inflating views and using Glide.
 * @param photos The list of photo items to display.
 *
 * === onCreateViewHolder Method ===
 * Called when RecyclerView needs a new {@link PhotoViewHolder} of the given type to represent an item.
 * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
 * @param viewType The view type of the new View.
 * @return A new PhotoViewHolder that holds a View of the given view type.
 *
 * === onBindViewHolder Method ===
 * Called by RecyclerView to display the data at the specified position.
 * This method updates the contents of the {@link PhotoViewHolder#itemView} to reflect the item at the given position.
 * @param holder The PhotoViewHolder which should be updated to represent the contents of the item at the given position in the data set.
 * @param position The position of the item within the adapter's data set.
 *
 * === getItemCount Method ===
 * Returns the total number of items in the data set held by the adapter.
 * @return The total number of items in this adapter.
 *
 * === getSelectedItems Method ===
 * Retrieves a list of all currently selected photo items.
 * @return A list containing all items marked as selected.
 *
 * === removeItems Method ===
 * Removes a list of items from the adapter's data set.
 * @param toRemove The list of PhotoItem objects to remove.
 *
 * === PhotoViewHolder Inner Class ===
 * ViewHolder for a photo item.
 * Caches views to avoid repeated and expensive `findViewById` calls.
 *
 * --- PhotoViewHolder Constructor ---
 * Constructs a new PhotoViewHolder.
 * @param itemView The view that you inflated in {@link #onCreateViewHolder(ViewGroup, int)}.
 */
