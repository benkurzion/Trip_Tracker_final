package com.example.triptracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddLogActivity extends AppCompatActivity {


    private Button saveBtn;
    private Button takeImage;
    private Button btnGetLocation;
    private TextView nameGenerated;
    private TextView descriptionGenerated;
    private Location retrievedLocation;
    private Bitmap photoGenerated;
    private Context context;
    //CAMERA_PERMISSION_CODE variables
    private final int CAMERA_PERMISSION_CODE = 2;
    //location variables
    private static final int REQUEST_LOCATION_CODE = 100;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_log);
        saveBtn = findViewById(R.id.saveLog);
        nameGenerated = findViewById(R.id.nameBox);
        descriptionGenerated = findViewById(R.id.descriptionBox);
        takeImage = findViewById(R.id.takeImg);
        btnGetLocation = findViewById(R.id.bt_getLocation);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        context = this;
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                retrievedLocation = locationResult.getLastLocation();
            }
        };

        takeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImage.setBackgroundColor(Color.DKGRAY);
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_PERMISSION_CODE);
                } else {
                    ActivityCompat.requestPermissions(AddLogActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                }
                view.setClickable(false);
            }
        });

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGetLocation.setBackgroundColor(Color.DKGRAY);
                getLastLocation();
                view.setClickable(false);
            }
        });

    }

    public void saveNewLog(View v) {
        //code to check if everything is empty --> dont save. show message.
        //if there is at least one thing entered, save to liststruc.
        String userGenName = nameGenerated.getText().toString();
        String userGenDesc = descriptionGenerated.getText().toString();
        if (retrievedLocation != null && !userGenName.equals("") && photoGenerated != null) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("Specific Name", userGenName);
            if(userGenDesc != "")
                i.putExtra("Specific Description", userGenDesc);
            try{
                String fileName = "bitmap.png";
                FileOutputStream stream = this.openFileOutput(fileName, Context.MODE_PRIVATE);
                photoGenerated.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
                i.putExtra("Photo Taken", fileName);
            }catch (Exception e){
                e.printStackTrace();
            }

            i.putExtra("Location Taken", retrievedLocation);

            //i.putExtra("New Log", new MainActivity.Head.SpecificLog(userGenName, photoGenerated, userGenDesc, retrievedLocation));
            startActivity(i);
        } else {
            Toast.makeText(context, "Please enter in the name, location, and photo for this log before saving", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_LOCATION_CODE) {//location
                getLastLocation();
            } else if (requestCode == CAMERA_PERMISSION_CODE) {//CAMERA_PERMISSION_CODE
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_PERMISSION_CODE);
            }
        } else {
            Toast.makeText(context, "Please provide the necessary permissions", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_PERMISSION_CODE) {
                photoGenerated = (Bitmap) data.getExtras().get("data");
            }
        }
    }


    private boolean checkPermissions(){
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if(checkPermissions()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if(location == null){
                            Toast.makeText(context, "Location is null", Toast.LENGTH_SHORT).show();
                            // requestNewLocationData();
                        }else{
                            retrievedLocation = location;
                        }
                    }
                });
            } else{
                Toast.makeText(context, "Please turn on location permissions", Toast.LENGTH_SHORT).show();
                startActivity( new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        } else{
            requestPermission();
        }
    }


}