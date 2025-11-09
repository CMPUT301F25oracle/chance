package com.example.chance.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chance.ChanceViewModel;
import com.example.chance.R;
import com.example.chance.controller.ChanceState;
import com.example.chance.controller.DataStoreManager;
import com.example.chance.databinding.HomeBinding;
import com.example.chance.model.Event;
import com.example.chance.model.User;

public class Home extends Fragment {

    private HomeBinding binding;
    private ChanceViewModel cvm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = HomeBinding.inflate(inflater, container, false);
        cvm = new ViewModelProvider(requireActivity()).get(ChanceViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ChanceState state = ChanceState.getInstance();
        User user = state.getUser();
        binding.homeSystemMessage.setText("Hello, " + user.getUsername());
        //region button press handlers
        binding.buttonCreateEvent.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_view, new CreateEvent())
                    .commit();
        });

        // now we load events
        ViewGroup event_container = binding.homeEventContainer;
        DataStoreManager.getInstance().getAllEvents((events) -> {
            LayoutInflater inflater = LayoutInflater.from(requireContext());

            for (Event event : events) {
                // Inflate your event_pill.xml
                View pill = inflater.inflate(R.layout._r_event_pill, event_container, false);
                ((TextView) pill.findViewById(R.id.event_title)).setText(event.getName());
                ((TextView) pill.findViewById(R.id.event_description)).setText(event.getDescription());
                assert event.getID() != null;
                pill.setTag(event.getID());
                pill.setOnClickListener(v -> {
                    assert (String) v.getTag() != null;
                    cvm.requestOpenEvent((String) v.getTag());
                });
//                // Optionally bind data to the pill (e.g., set text, images)
//                TextView title = pill.findViewById(R.id.eventTitle);
//                title.setText(event.getTitle());
//
//                // Add the pill to the container
                event_container.addView(pill);
            }
            //event_container.add
        });



        //endregion


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}