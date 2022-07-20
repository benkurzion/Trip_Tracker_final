package com.example.triptracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ActivityAdapterSpecific extends RecyclerView.Adapter<ActivityAdapterSpecific.CustomVH> {

    private Activity activity;
    private ArrayList<MainActivity.Head.SpecificLog> list;
    private String name;

    public ActivityAdapterSpecific(Activity activity, String name) {
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
        private Button mapBtn;
        private Button deleteBtn;
        private double longitude;
        private double latitude;

        public CustomVH(@NonNull View itemView) {
            super(itemView);
            specificName = itemView.findViewById(R.id.nameTV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            locationTV = itemView.findViewById(R.id.locationTV);
            imageView = itemView.findViewById(R.id.imageView);
            mapBtn = itemView.findViewById(R.id.mapSpecific);
            deleteBtn = itemView.findViewById(R.id.deleteSpecific);

            mapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" +  latitude  + "," + longitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    view.getContext().startActivity(mapIntent);
                    /*
                    if (mapIntent.resolveActivity(view.getContext().getPackageManager()) != null) {
                        view.getContext().startActivity(mapIntent);
                    }else{
                        Toast.makeText(view.getContext(), "Please download Google Maps to use this functionality", Toast.LENGTH_SHORT).show();
                    }
                     */
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDeletionDialog(itemView.getContext(), specificName.getText().toString());
                }
            });
        }

        public void setImageView(Bitmap bm) {
            if (bm == null)
                System.out.println("Bitmap is null");
            else {
                imageView.setImageBitmap(bm);
                //System.out.println("Set the bitmap image");
            }
        }

        public void setLocationTV(double latitude, double longitude) {
            if (latitude == Integer.MAX_VALUE || longitude == Integer.MAX_VALUE) {
                locationTV.setText("Location:\nNo location found");
            } else {
                this.longitude = longitude;
                this.latitude = latitude;
                locationTV.setText("Location:\n" + latitude + ",\n" + longitude);
            }
        }

        public void setDescriptionTV(String s) {
            descriptionTV.setText("Description: " + s);
        }

        public void setSpecificName(String s) {
            specificName.setText("Name:\n" + s);
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
        if (list.get(position).getSpecificLogName().equals("two")) {
            System.out.println("Here");
        }
        holder.setSpecificName(list.get(position).getSpecificLogName());
        holder.setLocationTV(list.get(position).getLatitude(), list.get(position).getLongitude());
        holder.setDescriptionTV(list.get(position).getSpecificLogDescription());
        holder.setImageView(loadImageFromStorage(list.get(position).getPath(), list.get(position).getKey()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private static void openDeletionDialog(Context context, String name) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_deletion_dialog);

        Button continueBtn = dialog.findViewById(R.id.continue_btn);
        continueBtn.setText("CONTINUE");
        Button cancelBtn = dialog.findViewById(R.id.cancel_btn);
        cancelBtn.setText("CANCEL");
        TextView warning = dialog.findViewById(R.id.warning_tv);

        warning.setText("Deleting this location is irrevocable. Data will be lost.");

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(context, MainActivity.class);
                next.putExtra("delete list specific", name);
                context.startActivity(next);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Data was NOT deleted", Toast.LENGTH_SHORT).show();
                Intent next = new Intent(context, PreviousTrips.class);
                context.startActivity(next);
            }
        });

        dialog.show();
    }


    private Bitmap loadImageFromStorage(String path, String key) {
        try {
            File f = new File(path, key + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
