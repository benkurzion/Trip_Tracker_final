package com.example.triptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;

public class ChangeLocActivity extends AppCompatActivity {

    private String currentLocation;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_loc);

    }

    public void getText(View v){
        currentLocation = ((TextView)findViewById(R.id.input1box)).getText().toString();
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("Current Location As Inputted", currentLocation);
        startActivity(i);
    }







}