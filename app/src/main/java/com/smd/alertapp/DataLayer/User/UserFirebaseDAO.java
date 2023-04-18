package com.smd.alertapp.DataLayer.User;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.smd.alertapp.Entities.User.UserType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class UserFirebaseDAO implements IUserDAO{

    FirebaseDatabase db;
    DatabaseReference ref;

    ArrayList<Hashtable<String,String>>data;

    public UserFirebaseDAO() {
        db=FirebaseDatabase.getInstance();
        db.setPersistenceEnabled(true);
        ref= db.getReference("data");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    data = new ArrayList<Hashtable<String,String>>();
                    for(DataSnapshot d:snapshot.getChildren()){
                        GenericTypeIndicator<HashMap<String,Object>>typeIndicator=new GenericTypeIndicator<HashMap<String, Object>>() {};
                        HashMap<String,Object>map=d.getValue(typeIndicator);
                        Hashtable<String,String>obj = new Hashtable<String, String>();
                        for(String key: map.keySet())
                            obj.put(key,map.get(key).toString());
                        data.add(obj);
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
        });
    }

    @Override
    public void save(Hashtable<String, String> user) {
        ref.child(user.get("id"));
    }

    @Override
    public Hashtable<String, String> getById(String id, UserType userType) {
        return null;
    }
}
