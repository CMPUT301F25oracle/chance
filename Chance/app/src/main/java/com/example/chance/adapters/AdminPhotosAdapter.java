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

        Glide.with(context)
                .load(item.url)
                .into(holder.imageView);

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
