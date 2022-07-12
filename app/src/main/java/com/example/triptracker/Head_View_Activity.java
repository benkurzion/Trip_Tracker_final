package com.example.triptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Head_View_Activity extends AppCompatActivity {

    private Button deleteBtn;
    private Button expandBtn;
    private TextView name;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_view);
        name = findViewById(R.id.headNameView);
        deleteBtn = findViewById(R.id.deleteButton);
        expandBtn = findViewById(R.id.expandButton);
        context = this;

        System.out.println("Creating view for head labeled " + name);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, Deletion_Dialog.class);
                i.putExtra("currentLocation", name.getText().toString());
                startActivity(i);
            }
        });

        expandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void openDeletionDialog(View v){
        Intent i = new Intent(context, Deletion_Dialog.class);
        i.putExtra("currentLocation", name.getText().toString());
        startActivity(i);
    }

    private void expandToSpecificList(View v){

    }
}