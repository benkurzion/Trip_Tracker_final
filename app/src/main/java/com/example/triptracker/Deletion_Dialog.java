package com.example.triptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Deletion_Dialog extends AppCompatActivity {

    private TextView warning;
    private Button continue_Btn;
    private Button cancel_Btn;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deletion_dialog);

        warning = findViewById(R.id.warning_tv);
        warning.setText("Deleting this location is irrevocable. Data will be lost.");

        continue_Btn = findViewById(R.id.continue_btn);
        continue_Btn.setText("CONTINUE");

        cancel_Btn = findViewById(R.id.cancel_btn);
        cancel_Btn.setText("CANCEl");

        context = this;

        Intent previous = getIntent();

        String currentLocation = previous.getStringExtra("currentLocation");

        continue_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.removeHeadLocation(currentLocation);
                Intent next = new Intent(context, MainActivity.class);
                startActivity(next);
            }
        });

        cancel_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Data was NOT deleted", Toast.LENGTH_SHORT).show();
                Intent next = new Intent(context, MainActivity.class);
                startActivity(next);
            }
        });
    }
}