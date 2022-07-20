package com.example.triptracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    //save image variables
    private String key;
    private String path;



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
        key = null;

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
        saveToInternalStorage(photoGenerated);
        //code to check if everything is empty --> dont save. show message.
        //if there is at least one thing entered, save to liststruc.
        String userGenName = nameGenerated.getText().toString();
        String userGenDesc = descriptionGenerated.getText().toString();
        if (!btnGetLocation.isClickable() && !userGenName.equals("") && !takeImage.isClickable()) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("Specific Name", userGenName);
            if(userGenDesc != "")
                i.putExtra("Specific Description", userGenDesc);
            i.putExtra("key", key);
            i.putExtra("path", path);
            i.putExtra("Latitude", retrievedLocation.getLatitude());
            i.putExtra("Longitude", retrievedLocation.getLongitude());
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

    private void saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/TripTracker/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        key = String.valueOf(System.currentTimeMillis());
        File mypath = new File(directory,key + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        path = directory.getAbsolutePath();
    }


}