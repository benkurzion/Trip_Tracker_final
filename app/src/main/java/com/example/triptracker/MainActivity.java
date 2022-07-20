package com.example.triptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private static TextView currentLocation;

    private static LinkedList<Head> allLogs;

    private Context context;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        currentLocation = ((TextView)findViewById(R.id.currTrip));


        //this object is used to store the Logs. only found in MainActivity
        allLogs = loadList();

        //if the current location has not been inputted, the load it. if has been inputted, keep the input and write it to file
        Intent intent = getIntent();
        if(intent.hasExtra("Current Location As Inputted")) {//came from changeLocActivity
            currentLocation.setText(intent.getStringExtra("Current Location As Inputted"));
            //System.out.println("On create, current Location text = " + currentLocation.getText());
            writeStringToFile("appInformation.txt");
            //System.out.println("Wrote the current location to file");

        } else { //did not come from changeLocActivity. get currentlocation from file
            String s = readLocationFromFile("appInformation.txt");
            if(s.equals("")){
                //System.out.println("the file was empty");
                currentLocation.setText("No Location Selected");
            }else{
                //System.out.println("the file was not null and currentLocation is set to " + s);
                currentLocation.setText(s);
                //System.out.println("check: " + currentLocation.getText());
            }
        }
        if(intent.hasExtra("Specific Name")){//came from Add Log Activity
            addHeadLocation(currentLocation.getText().toString());
            /*
            Bitmap bmp = null;
            String fileName = getIntent().getStringExtra("Photo Taken");
            try{
                FileInputStream is = this.openFileInput(fileName);
                bmp = BitmapFactory.decodeStream(is);
                is.close();
            }catch(Exception e){
                e.printStackTrace();
            }

             */
            if(intent.hasExtra("Specific Description")) {
                addSpecificLog(new Head.SpecificLog(intent.getStringExtra("Specific Name"), intent.getStringExtra("Specific Description"),
                        intent.getDoubleExtra("Latitude", Integer.MAX_VALUE), intent.getDoubleExtra("Longitude", Integer.MAX_VALUE),
                        intent.getStringExtra("key"), intent.getStringExtra("path")));
            }else{
                addSpecificLog(new Head.SpecificLog(intent.getStringExtra("Specific Name"), null,
                        intent.getDoubleExtra("Latitude", Integer.MAX_VALUE), intent.getDoubleExtra("Longitude", Integer.MAX_VALUE),
                        intent.getStringExtra("key"), intent.getStringExtra("path")));
            }
            storeList(allLogs);
        }
        if(intent.hasExtra("store list")){
            removeHeadLocation(intent.getStringExtra("store list"));
            storeList(allLogs);
        }
        if(intent.hasExtra("delete list specific")){
            removeSpecificLog(intent.getStringExtra("delete list specific"));
            storeList(allLogs);
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storeList(allLogs);
    }

    public void toActivityChangeLocation(View v){
        Intent i = new Intent(this, ChangeLocActivity.class);
        startActivity(i);
    }

    public void toActivityAddLog(View v){
        if(currentLocation.getText().toString().equals("No Location Selected")){
            Toast.makeText(context, "Please enter a valid current location", Toast.LENGTH_SHORT).show();
        }else{
            storeList(allLogs);
            Intent i = new Intent(this, AddLogActivity.class);
            startActivity(i);
        }
    }

    public void toActivityPrevTrips(View v){
        storeList(allLogs);
        Intent i = new Intent(this, PreviousTrips.class);
        startActivity(i);
    }



    /**
     * Should be used when displaying the records in the previous logs activity
     * @return the list to be displayed
     */
    public static LinkedList<Head> getStoredLogsList(){
        return allLogs;
    }

    /**
     * Should be used when adding a new specific log in the add new log activity
     * @return the current location as stored in the main activity
     */
    public static String getCurrentLocation(){
        return currentLocation.getText().toString();
    }



    public static boolean isEmpty(){
        return allLogs.isEmpty();
    }


    /**
     * Writes the String location to the file for storage.
     * @param fileName file to be stored in
     */
    private void writeStringToFile(String fileName){
        try{
            File path = context.getApplicationContext().getFilesDir();
            FileOutputStream writer = new FileOutputStream(new File(path, fileName));
            writer.write(currentLocation.getText().toString().getBytes());
            writer.close();
            Toast.makeText(getApplicationContext(), "Wrote to file " + fileName, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * Gets the current location from the appInformation file
     * @param fileName file to read data from
     * @return the current location as found in the file
     */
    private String readLocationFromFile(String fileName){
        File path = context.getApplicationContext().getFilesDir();
        File readFrom = new File(path, fileName);
        byte[] content = new byte[(int)readFrom.length()];
        try {
            FileInputStream inputStream = new FileInputStream(readFrom);
            inputStream.read(content);
            return new String(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No location selected.";
    }


    /**
     * Stores listStructure in SharedPreferences
     */
    private void storeList(LinkedList<Head> list){
        SharedPreferences sharedPreferences;
        Gson gson;
        SharedPreferences.Editor editor;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        gson = new Gson();

        String json = gson.toJson(list);
        editor.putString("Previous Logs", json);
        editor.apply();
    }

    /**
     * Loads the listStructure back from SharedPreferences
     * @return the listStructure from SharedPreferences
     */
    private LinkedList<Head> loadList(){
        SharedPreferences sharedPreferences;
        Gson gson;
        SharedPreferences.Editor editor;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        gson = new Gson();

        String json = sharedPreferences.getString("Previous Logs", null);
        Type type = new TypeToken<LinkedList<Head>>() {}.getType();
        LinkedList<Head> list = gson.fromJson(json, type);
        if(list == null){
            return new LinkedList<Head>();
        }
        return list;
    }

    /**
     * If traveling to a new place, add the places name. Ex Japan
     * @param name name of the place
     * @return success based on if the name is repeated
     */
    private static boolean addHeadLocation(String name){
        for(int i = 0; i < allLogs.size(); i++){
            if(allLogs.get(i).getHeadName().equals(name)){
                System.out.println("failed to add head location. allLogs already contains " + name);
                return false;
            }
        }
        allLogs.add(new Head(name, null));
        System.out.println("Added head Location");
        return true;
    }

    /**
     * Provides functionality to remove a head location from the dynamic list
     * @param name head location to be removed
     */
    public static void removeHeadLocation(String name){
        for(int i = 0; i < allLogs.size(); i++){
            //System.out.println(allLogs.get(i).getHeadName());
            if(allLogs.get(i).getHeadName().equals(name)) {
                System.out.println("Removing " + name);
                allLogs.remove(i);
                for(Head h : allLogs){
                    System.out.println(h.getHeadName());
                }
                return;
            }
        }
    }

    /**
     * Adds a new log of a specific location. Saves the listStruc to SharedPreferences each successful call.
     * @param myLog a SpecificLog type that is passed in from intent
     * @return success based on whether or not a head location has been selected.
     */
    private static boolean addSpecificLog(Head.SpecificLog myLog){
        //System.out.println("Trying to add specific log:\nCurrent location = " + currentLocation);
        for(int i = 0; i < allLogs.size(); i++) {
            if (allLogs.get(i).getHeadName().equals(currentLocation.getText().toString())) {
                allLogs.get(i).getSpecificLogs().add(myLog);
                //System.out.println("Adding specific log");
                return true;
            }
        }
        //System.out.println("Not able to add specific log");
        return false;
    }

    private static boolean removeSpecificLog(String name){
        for(int i = 0; i < allLogs.size(); i++) {
            if (allLogs.get(i).getHeadName().equals(currentLocation.getText().toString())) {
                for(int j = 0; j < allLogs.get(i).getSpecificLogs().size(); j++){
                    if(allLogs.get(i).getSpecificLogs().get(j).getSpecificLogName().equals(name)){
                        allLogs.get(i).getSpecificLogs().remove(j);
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Retrieves the specific log list from a certain location
     * @param name specifies which location to retrieve specific logs from
     * @return the list of specific logs
     */
    public static ArrayList<Head.SpecificLog> getSpecificList(String name){
        //System.out.println("Looking through allLogs for " + name);
        for(Head h  : allLogs) {
            if (h.getHeadName().equals(name)) {
                //System.out.println("returning " + name + "'s specific list");
                return h.getSpecificLogs();
            }
        }
        return null;
    }


    public static class Head{

        private String name;
        private ArrayList<SpecificLog> specificLogs;

        public Head(String name, ArrayList<SpecificLog> list){
            this.name = name;
            if(list == null)
                this.specificLogs = new ArrayList<>();
            else
                this.specificLogs = list;
        }

        public ArrayList<SpecificLog> getSpecificLogs() {
            return specificLogs;
        }

        public String getHeadName() {
            return name;
        }


        public static class SpecificLog{
            private String name;
            private String description;
            private double latitude;
            private double longitude;
            private String key;
            private String path;

            public SpecificLog(String name, String description, double latidude, double longitude, String key, String path){
                this.name = name;
                this.description = description;
                this.latitude = latidude;
                this.longitude = longitude;
                this.key = key;
                this.path = path;
            }

            public String getPath(){
                return this.path;
            }

            public String getKey(){
                return key;
            }

            public String getSpecificLogName(){
                return this.name;
            }

            public double getLatitude(){
                return latitude;
            }

            public double getLongitude(){
                return longitude;
            }

            public String getSpecificLogDescription(){
                return description;
            }


        }
    }

}