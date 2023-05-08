package com.smd.alertapp.DataLayer.Post;

import android.content.Context;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smd.alertapp.Entities.Post;
import com.smd.alertapp.Entities.User.HelplineUser;
import com.smd.alertapp.Entities.User.RegularUser;
import com.smd.alertapp.Entities.User.UserType;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PostFirebaseDAO implements IPostDAO{
    FirebaseDatabase db;
    DatabaseReference postRef;
    Context context;
    ValueEventListener postsValueEventListener;

    public PostFirebaseDAO(Context ctx ){
        context = ctx;
        db = FirebaseDatabase.getInstance();
        postRef = db.getReference("Post");
//        postFileRef = db.getReference("Post_media");
    }

    @Override
    public void save(Post post, Uri fileUri) {
        String key = postRef.push().getKey();
        Log.e("Post key: ", key);
        post.setPostId(key);
        if(fileUri != null) {
            uploadFile(fileUri, new FileUploadCallback() {
                @Override
                public void onFileUpload(String fileUrl) {
                    // Update note with file URL
                    post.setMediaUrl(fileUrl);
                }

                @Override
                public void onError(String message) {
                    Log.e("Error uploading file: ", message);
                }
            });
        }
        postRef.child(key).setValue(post)
                .addOnCompleteListener(task -> {
                    Toast.makeText( context,"Post created successfully", Toast.LENGTH_LONG).show();
                }).addOnFailureListener(task -> {
                    Toast.makeText( context,"Error creating post", Toast.LENGTH_LONG).show();
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
    public void getPosts(PostsCallback callback) {
        postsValueEventListener = postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Post> postsList = new ArrayList<>();
                for (DataSnapshot noteSnapshot : snapshot.getChildren()) {
                    Post post = noteSnapshot.getValue(Post.class);
                    postsList.add(post);
                }
                callback.onPostsReceived(postsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostFirebaseDAO", "Failed to retrieve posts: " + error.getMessage());
            }
        });
    }

    public void uploadFile(Uri fileUri, final FileUploadCallback callback) {
        // Get a reference to the Firebase Storage
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a unique file name
        final String fileName = UUID.randomUUID().toString();

        // Create a reference to the file location
        final StorageReference fileRef = storageRef.child("files/" + fileName);

        // Upload the file to Firebase Storage
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL for the file
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    callback.onFileUpload(uri.toString());
                    }).addOnFailureListener(e -> {
                        callback.onError("Error getting file download URL: " + e.getMessage());
                    });
                })
                .addOnFailureListener(e -> {
                    callback.onError("Error uploading file: " + e.getMessage());
                });
    }
}
