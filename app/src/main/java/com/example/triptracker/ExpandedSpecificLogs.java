package com.example.triptracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ExpandedSpecificLogs extends AppCompatActivity {

    private TextView specificTitle;
    private RecyclerView specificRV;
    private ActivityAdapterSpecific activityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_specific_logs);
        specificTitle = findViewById(R.id.specificTitle);
        specificTitle.setText("Saved Locations For: " + getIntent().getStringExtra("name"));
        specificRV = findViewById(R.id.specificRV);

        this.activityAdapter = new ActivityAdapterSpecific(this, getIntent().getStringExtra("name"));
        specificRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        specificRV.setAdapter(activityAdapter);
        specificRV.setVisibility(View.VISIBLE);
    }
}