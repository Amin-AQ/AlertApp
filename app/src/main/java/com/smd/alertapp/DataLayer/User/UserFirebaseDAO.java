package com.smd.alertapp.DataLayer.User;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.smd.alertapp.Entities.User.HelplineUser;
import com.smd.alertapp.Entities.User.RegularUser;
import com.smd.alertapp.Entities.User.User;
import com.smd.alertapp.Entities.User.UserType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

public class UserFirebaseDAO implements IUserDAO{

    FirebaseDatabase db;
    DatabaseReference ref, regularRef, helplineRef;
    Context context;
    ArrayList<Hashtable<String,Object>>data;

    public UserFirebaseDAO(Context ctx ){
        context=ctx;
        db=FirebaseDatabase.getInstance();
        ref= db.getReference() ;
        regularRef=ref.child("RegularUser");
        helplineRef=ref.child("HelplineUser");
        regularRef.addValueEventListener(createValueEventListener());
        helplineRef.addValueEventListener(createValueEventListener());
    }

    @Override
    public void save(User user) {
            DatabaseReference userRef;
            String key;
            if(user.getUserType() == UserType.HELPLINE) {
                HelplineUser helplineUser = (HelplineUser) user;
                key = helplineUser.getId();
                userRef = helplineRef.child(key);
            } else {
                RegularUser regularUser = (RegularUser) user;
                key = regularUser.getPhoneNumber();
                userRef = regularRef.child(key);
            }
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                        // User already exists, display toast
                        Toast.makeText(context, "User already exists", Toast.LENGTH_SHORT).show();
                    else
                        // User does not exist, save to database
                        userRef.setValue(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                    Log.d("UserFirebaseOnCancelSave",error.toString());
                }
            });
        }


    @Override
    public User getById(String id, UserType userType) {
        DatabaseReference userRef;
        if (userType == UserType.HELPLINE) {
            userRef = helplineRef.child(id);
            Task<DataSnapshot> dataSnapshotTask = userRef.get();
            try {
                Tasks.await(dataSnapshotTask);
                DataSnapshot dataSnapshot = dataSnapshotTask.getResult();
                if (dataSnapshot.exists()) {
                    HelplineUser helplineUser = dataSnapshot.getValue(HelplineUser.class);
                    helplineUser.setId(id);
                    return helplineUser;
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            userRef = regularRef.child(id);
            Task<DataSnapshot> dataSnapshotTask = userRef.get();
            try {
                Tasks.await(dataSnapshotTask);
                DataSnapshot dataSnapshot = dataSnapshotTask.getResult();
                if (dataSnapshot.exists()) {
                    RegularUser regularUser = dataSnapshot.getValue(RegularUser.class);
                    regularUser.setPhoneNumber(id);
                    return regularUser;
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



    private ValueEventListener createValueEventListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    data = new ArrayList<Hashtable<String,Object>>();
                    for(DataSnapshot d:snapshot.getChildren()){
                        GenericTypeIndicator<HashMap<String,Object>>typeIndicator=new GenericTypeIndicator<HashMap<String, Object>>() {};
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
