package com.example.triptracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

public class PreviousTrips extends AppCompatActivity {

    private ActivityAdapter activityAdapter;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_trips);
        //recycler view
        rv = findViewById(R.id.recyclerView);
        this.activityAdapter = new ActivityAdapter(this);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(activityAdapter);

        if(MainActivity.isEmpty()){
            ((TextView)findViewById(R.id.emptyList)).setVisibility(View.VISIBLE);
            rv.setVisibility(View.INVISIBLE);
        }else{
            ((TextView)findViewById(R.id.emptyList)).setVisibility(View.INVISIBLE);
            rv.setVisibility(View.VISIBLE);
        }
    }
}