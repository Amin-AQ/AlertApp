package com.smd.alertapp.DataLayer.Comment;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smd.alertapp.Entities.Comment;

import java.util.ArrayList;

public class CommentFirebaseDAO implements ICommentDAO{
    FirebaseDatabase db;
    DatabaseReference commentRef;
    Context context;
    ValueEventListener postsValueEventListener;

    public CommentFirebaseDAO(Context ctx) {
        context = ctx;
        db = FirebaseDatabase.getInstance();
        commentRef = db.getReference("Comment");
    }

    @Override
    public void save(Comment comment) {
        String key = commentRef.push().getKey();
        Log.e("Comment key: ", key);
        comment.setCommentId(key);
        commentRef.child(key).setValue(comment)
                .addOnCompleteListener(task -> {
                    Toast.makeText( context,"Comment created successfully", Toast.LENGTH_LONG).show();
                }).addOnFailureListener(task -> {
                    Toast.makeText( context,"Error creating comment", Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void getComments(CommentCallback commentsCallback, String postId) {
        commentRef.orderByChild("postId").startAt(postId).endAt(postId + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Comment> comments = new ArrayList<>();
                        for (DataSnapshot noteSnapshot : snapshot.getChildren()) {
                            Comment comment = noteSnapshot.getValue(Comment.class);
                            if (comment != null) {
                                comment.setCommentId(noteSnapshot.getKey());
                                comments.add(comment);
                            }
                        }
                        commentsCallback.onCommentsReceived(comments);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e( "Error searching notes", String.valueOf(error.toException()));
                        commentsCallback.onCommentsReceived(new ArrayList<>());
                    }
                });
    }
}
