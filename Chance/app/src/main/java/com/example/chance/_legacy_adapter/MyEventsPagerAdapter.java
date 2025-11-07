package com.example.chance._legacy_adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.chance.view.fragments.MyEventsFragment;

/**
 * Pager adapter for "My Events" tabs.
 * Used in activity_my_events.xml with TabLayout + ViewPager2.
 */
public class MyEventsPagerAdapter extends FragmentStateAdapter {

    public MyEventsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Each position represents a category of events
        switch (position) {
            case 0:
                return MyEventsFragment.newInstance("waiting");
            case 1:
                return MyEventsFragment.newInstance("selected");
            case 2:
                return MyEventsFragment.newInstance("history");
            default:
                return MyEventsFragment.newInstance("waiting");
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Waiting, Selected, History
    }
}
