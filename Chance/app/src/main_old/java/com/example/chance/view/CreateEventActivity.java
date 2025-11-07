package com.example.chance.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chance.R;
import com.example.chance.controller.EventController;
import com.example.chance.model.Event;

import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etName, etLocation, etPrice, etCapacity, etDescription;
    private Button btnCreate;
    private EventController eventController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        etName = findViewById(R.id.et_event_name);
        etLocation = findViewById(R.id.et_event_location);
        etPrice = findViewById(R.id.et_event_price);
        etCapacity = findViewById(R.id.et_event_capacity);
        etDescription = findViewById(R.id.et_event_description);
        btnCreate = findViewById(R.id.btn_create_event);

        eventController = new EventController();

        btnCreate.setOnClickListener(v -> createEvent());
    }

    private void createEvent() {
        try {
            Event event = new Event(
                    etName.getText().toString(),
                    etLocation.getText().toString(),
                    Integer.parseInt(etCapacity.getText().toString()),
                    Double.parseDouble(etPrice.getText().toString()),
                    etDescription.getText().toString(),
                    new Date()
            );

            eventController.createEvent(event, doc ->
                            Toast.makeText(this, "Event created successfully!", Toast.LENGTH_SHORT).show(),
                    e -> Toast.makeText(this, "Failed to create event", Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
        }
    }
}
