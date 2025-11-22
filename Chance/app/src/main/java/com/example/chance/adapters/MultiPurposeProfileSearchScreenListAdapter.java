package com.example.chance.adapters;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chance.R;
import com.example.chance.model.User;


/**
 * Adapter for displaying Event cards in RecyclerView.
 * Used in activity_event_list.xml and event_card.xml layout.
 */
public class MultiPurposeProfileSearchScreenListAdapter extends ListAdapter<User, MultiPurposeProfileSearchScreenListAdapter.ProfileViewHolder> {

    public static final DiffUtil.ItemCallback<User> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<User>() {
            @Override
            public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                // Compare unique IDs if available
                if (oldItem.getID() != null && newItem.getID() != null) {
                    return oldItem.getID().equals(newItem.getID());
                }
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
                // Compare relevant fields for UI. Adjust as your model evolves.
                return java.util.Objects.equals(oldItem.getID(), newItem.getID());
            }
        };

    public MultiPurposeProfileSearchScreenListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View eventPillView = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.multi_purpose_profile_search_screen_profile_pill, parent, false);

        return new ProfileViewHolder(eventPillView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        User user = getItem(position);
        if (user == null) return;

        holder.username.setText(user.getUsername());
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView username;

        public ProfileViewHolder(@NonNull View userPillView) {
            super(userPillView);
            username = userPillView.findViewById(R.id.username);
        }
    }

    private static int dpToPx(View view, float dpValue) {
        float densityPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, view.getResources().getDisplayMetrics());
        return Math.round(densityPx);
    }
}


