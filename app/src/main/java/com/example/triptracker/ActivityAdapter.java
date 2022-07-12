package com.example.triptracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.CustomVH>{

    private LinkedList<MainActivity.Head> list;
    private Activity activity;

    public ActivityAdapter(Activity activity){
        this.activity = activity;
        list = MainActivity.getStoredLogsList();
    }

    public static class CustomVH extends RecyclerView.ViewHolder{
        private TextView name;
        private Button deleteBtn;
        private Button expandBtn;
        public CustomVH(@NonNull View itemView) {
            super(itemView);
            deleteBtn = itemView.findViewById(R.id.deleteButton);
            expandBtn = itemView.findViewById(R.id.expandButton);
            name = itemView.findViewById(R.id.headNameView);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDeletionDialog(itemView.getContext(), name.getText().toString());
                }
            });

            expandBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(itemView.getContext(), ExpandedSpecificLogs.class);
                    i.putExtra("name", name.getText().toString());
                    itemView.getContext().startActivity(i);
                }
            });
        }

        public void setName(String n){
            name.setText(n);
        }
    }

    @NonNull
    @Override
    public ActivityAdapter.CustomVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_head_view, parent, false);
        return new ActivityAdapter.CustomVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.CustomVH holder, int position) {
        holder.setName(list.get(position).getHeadName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private static void openDeletionDialog(Context context, String name){
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
                next.putExtra("store list", name);
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
}

