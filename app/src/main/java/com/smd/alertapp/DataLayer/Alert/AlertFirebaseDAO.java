package com.smd.alertapp.DataLayer.Alert;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.smd.alertapp.Entities.Alert.Alert;
import com.smd.alertapp.Entities.Alert.AlertType;
import com.smd.alertapp.Entities.Alert.QuickAlert;
import com.smd.alertapp.Entities.Alert.CustomAlert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class AlertFirebaseDAO implements IAlertDAO{

    Context context;
    FirebaseDatabase db;
    DatabaseReference ref, quickRef, customRef;
    ArrayList<Hashtable<String,Object>>data;
    AlertFirebaseDAO(Context ctx){
        context=ctx;
        ref= db.getReference();
        quickRef=db.getReference("QuickAlert");
        customRef=db.getReference("CustomAlert");
        quickRef.addValueEventListener(createValueEventListener());
        customRef.addValueEventListener(createValueEventListener());
    }

    @Override
    public void save(Alert alert) {
        DatabaseReference alertRef;
        String key= alert.getAlertId();
        if(alert.getAlertType()== AlertType.QUICK_ALERT){
            QuickAlert quickAlert=(QuickAlert) alert;
            alertRef=quickRef.child(key);
        }
        else{
            CustomAlert customAlert=(CustomAlert) alert;
            alertRef=customRef.child(key);
        }
        alertRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    // User already exists, display toast
                    Toast.makeText(context, "Alert already sent", Toast.LENGTH_SHORT).show();
                else
                    // Alert does not exist, save to database
                    alertRef.setValue(alert);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.d("UserFirebaseOnCancelSave",error.toString());
            }
        });
    }

    private ValueEventListener createValueEventListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    data = new ArrayList<Hashtable<String,Object>>();
                    for(DataSnapshot d:snapshot.getChildren()){
                        GenericTypeIndicator<HashMap<String,Object>> typeIndicator=new GenericTypeIndicator<HashMap<String, Object>>() {};
                        HashMap<String,Object>map=d.getValue(typeIndicator);
                        if(map!=null) {
                            Hashtable<String, Object> obj = new Hashtable<String, Object>();
                            for (String key : map.keySet())
                                obj.put(key, map.get(key));
                            data.add(obj);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("UserFirebaseMsg", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("UserFirebaseOnCancel",error.toString());
            }
        };
    }
}
