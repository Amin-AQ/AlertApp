package com.smd.alertapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.smd.alertapp.Entities.Alert.Alert;
import com.smd.alertapp.Fragments.AlertFragment;
import com.smd.alertapp.Fragments.CommentsFragment;
import com.smd.alertapp.R;

import java.util.ArrayList;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder>{

    ArrayList<Alert>alerts;
    FragmentManager fragmentManager;

    public AlertAdapter(ArrayList<Alert> alerts, FragmentManager fragmentManager) {
        this.alerts = alerts;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alert_layout, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        Alert alert=alerts.get(position);
        holder.alertTypeTxt.setText(alert.getAlertType().toString());
        holder.alertIdTxt.setText(alert.getAlertId());
        holder.alertUserIdTxt.setText(alert.getUserId());
        holder.alertCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AlertFragment fragment=AlertFragment.newInstance(alert.getAlertId(),alert.getUserId(),alert.getLocation(),alert.getAlertType().toString());
                fragmentTransaction.replace(R.id.fragment_alert_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class AlertViewHolder extends RecyclerView.ViewHolder{

        CardView alertCard;
        TextView alertIdTxt, alertUserIdTxt, alertTypeTxt;
        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            alertCard=itemView.findViewById(R.id.alertcard);
            alertIdTxt=itemView.findViewById(R.id.alertid);
            alertUserIdTxt=itemView.findViewById(R.id.alertuser);
            alertTypeTxt=itemView.findViewById(R.id.alerttype);
        }
    }

}
