package com.example.chance.views.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chance.ChanceViewModel;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.HomeBinding;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

abstract public class ChanceFragment extends Fragment {
    protected DataStoreManager dsm;
    protected ChanceViewModel cvm;
    public Bundle meta = new Bundle();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dsm = DataStoreManager.getInstance();
        cvm = new ViewModelProvider(requireActivity()).get(ChanceViewModel.class);

    }

    /**
     * this method is called when the transition animation has completed
     */
    public void chanceEnterTransitionComplete(){

    }
}
