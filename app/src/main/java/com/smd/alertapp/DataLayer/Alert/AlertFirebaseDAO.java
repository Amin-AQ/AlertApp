package com.smd.alertapp.DataLayer.Alert;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smd.alertapp.Entities.Alert.Alert;
import com.smd.alertapp.Entities.Alert.AlertType;
import com.smd.alertapp.Entities.Alert.QuickAlert;
import com.smd.alertapp.Entities.Alert.CustomAlert;

import java.util.ArrayList;
import java.util.Hashtable;

public class AlertFirebaseDAO implements IAlertDAO{

    Context context;
    FirebaseDatabase db;
    DatabaseReference ref, quickRef, customRef;
    ArrayList<Hashtable<String,Object>>data;
    ValueEventListener quickAlertsListener,customAlertsListener;
    public AlertFirebaseDAO(Context ctx){
        context=ctx;
        db=FirebaseDatabase.getInstance();
        ref= db.getReference();
        quickRef=db.getReference("QuickAlert");
        customRef=db.getReference("CustomAlert");
    }

    @Override
    public void save(Alert alert) {
        DatabaseReference alertRef;
        String key= alert.getAlertId();

        if(alert.getAlertType()== AlertType.QUICK_ALERT)
            alertRef=quickRef.child(key);
        else
            alertRef=customRef.child(key);

        alertRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    // Alert already exists, display toast
                    Toast.makeText(context, "Alert already sent", Toast.LENGTH_SHORT).show();
                else {// Alert does not exist, save to database
                    alertRef.setValue(alert);
                    if(alert.getAlertType()==AlertType.CUSTOM_ALERT) {
                        alertRef.child("message").setValue(((CustomAlert) alert).getMessage());
                    }
                    Toast.makeText(context, "Alert sent to "+alert.getHelplineType(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.d("UserFirebaseOnCancelSave",error.toString());
            }
        });
    }

    @Override
    public void getQuickAlerts(QuickAlertsCallback callback){
        quickAlertsListener=quickRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Alert>quickAlerts=new ArrayList<Alert>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Alert alert=dataSnapshot.getValue(QuickAlert.class);
                    quickAlerts.add(alert);
                }
                callback.onQuickAlertsReceived(quickAlerts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AlertFirebaseDAO", "Failed to retrieve quick alerts: " + error.getMessage());
            }
        });

    }

    @Override
    public void getCustomAlerts(CustomAlertsCallback callback){
        customAlertsListener=customRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Alert>customAlerts=new ArrayList<Alert>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Alert alert=dataSnapshot.getValue(CustomAlert.class);
                    customAlerts.add(alert);
                }
                callback.onCustomAlertsReceived(customAlerts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
