package com.smd.alertapp.DataLayer.Post;

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
import com.smd.alertapp.Entities.Post;
import com.smd.alertapp.Entities.User.HelplineUser;
import com.smd.alertapp.Entities.User.RegularUser;
import com.smd.alertapp.Entities.User.UserType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PostFirebaseDAO implements IPostDAO{
    FirebaseDatabase db;
    DatabaseReference ref, postRef;
    Context context;
    ArrayList<Hashtable<String,Object>> data;

    public PostFirebaseDAO(Context ctx ){
        context=ctx;
        db=FirebaseDatabase.getInstance();
        ref= db.getReference() ;
        postRef = ref.child("Post");
        postRef.addValueEventListener(createValueEventListener());
    }

    @Override
    public void save(Post post) {
        DatabaseReference lpostRef;
        String key = post.getPostId();
        lpostRef = postRef.child(key);
        lpostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    Toast.makeText(context, "Post Id already exists", Toast.LENGTH_SHORT).show();
                else
                    lpostRef.setValue(post)
                            .addOnCompleteListener(task -> {
                                Toast.makeText( context,"Post created successfully", Toast.LENGTH_LONG).show();
                            }).addOnFailureListener(task -> {
                                Toast.makeText( context,"Error creating post", Toast.LENGTH_LONG).show();
                            });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.d("UserFirebaseOnCancelSave",error.toString());
            }
        });
    }

    @Override
    public Post getById(String id) {
        DatabaseReference lpostRef = postRef.child(id);
        Task<DataSnapshot> dataSnapshotTask = lpostRef.get();
        try {
            Tasks.await(dataSnapshotTask);
            DataSnapshot dataSnapshot = dataSnapshotTask.getResult();
            if (dataSnapshot.exists()) {
                Post post = dataSnapshot.getValue(Post.class);
                post.setPostId(id);
                return post;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Post> load() {
        ArrayList<Post> posts = new ArrayList<Post>();
        Task<DataSnapshot> dataSnapshotTask = postRef.get();
        try{
            Tasks.await(dataSnapshotTask);
            DataSnapshot dataSnapshot = dataSnapshotTask.getResult();
            for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                Post post = snapshot.getValue(Post.class);
                posts.add(post);
            }
        }catch (Exception e){
            Log.e("Error",e.getMessage());
            e.printStackTrace();
        }
        return posts;
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
                    Log.e("PostFirebaseMsg", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("PostFirebaseOnCancel",error.toString());
            }
        };
    }

}
