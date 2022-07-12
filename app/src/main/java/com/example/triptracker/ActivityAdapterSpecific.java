package com.example.triptracker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActivityAdapterSpecific extends RecyclerView.Adapter<ActivityAdapterSpecific.CustomVH>{

    private Activity activity;
    private ArrayList<MainActivity.Head.SpecificLog> list;
    private String name;

    public ActivityAdapterSpecific (Activity activity, String name){
        this.activity = activity;
        this.name = name;
        //System.out.println("Searching for specific list under " + name);
        list = MainActivity.getSpecificList(name);
        System.out.println("Got list. constructed the activity adapter");
    }

    public static class CustomVH extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView locationTV;
        private TextView descriptionTV;
        private TextView specificName;

        public CustomVH(@NonNull View itemView) {
            super(itemView);
            specificName = itemView.findViewById(R.id.nameTV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            locationTV = itemView.findViewById(R.id.locationTV);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void setImageView(Bitmap bm){
            if(bm == null)
                System.out.println("Bitmap is null");
            else {
                imageView.setImageBitmap(bm);
                //System.out.println("Set the bitmap image");
            }
        }

        public void setLocationTV(Location s){
            if(s == null){
                locationTV.setText("Location:\nNo location found");
            }else{
                locationTV.setText("Location:\n" + s.getLatitude() + ", " + s.getLongitude());
            }
        }

        public void setDescriptionTV(String s){
            descriptionTV.setText(s);
        }

        public void setSpecificName(String s){
            specificName.setText(s);
        }
    }

    @NonNull
    @Override
    public ActivityAdapterSpecific.CustomVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_specific_log_view, parent, false);
        return new ActivityAdapterSpecific.CustomVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAdapterSpecific.CustomVH holder, int position) {
        if(list.get(position).getSpecificLogName().equals("two")){
            System.out.println("Here");
        }
        holder.setSpecificName(list.get(position).getSpecificLogName());
        System.out.println("Making the view for " + list.get(position).getSpecificLogName());
        byte[] byteArray = list.get(position).getPhoto();
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //holder.setImageView(bmp);
        //System.out.println("got photo which = " + list.get(position).getPhoto());
        holder.setLocationTV(list.get(position).getLocation());
        holder.setDescriptionTV(list.get(position).getSpecificLogDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
