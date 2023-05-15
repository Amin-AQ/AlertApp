package com.smd.alertapp.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smd.alertapp.Entities.Post;
import com.smd.alertapp.Fragments.CommentsFragment;
import com.smd.alertapp.Fragments.ContactsFragment;
import com.smd.alertapp.R;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    ArrayList<Post> postList;
    FragmentManager fragmentManager;

    public PostsAdapter(ArrayList<Post> posts, @NonNull FragmentManager fragManager) {
        postList = posts;
        fragmentManager = fragManager;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_layout, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
        holder.commentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Sending post id:" + post.getPostId());
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                CommentsFragment commentsFragment = CommentsFragment.newInstance(post.getPostId());
                fragmentTransaction.replace(R.id.fragment_comment_container, commentsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postText, postUser, postDate;
        CardView commentCard;
        ImageView postImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postText = itemView.findViewById(R.id.postText);
            postUser = itemView.findViewById(R.id.postUser);
            postDate = itemView.findViewById(R.id.postDate);
            postImage = itemView.findViewById(R.id.post_image);
            commentCard = itemView.findViewById(R.id.viewCommentsCard);
        }
        public void bind(Post post) {
            postText.setText(post.getText());
            postUser.setText(post.getUserId());
            postDate.setText(post.getDate().toString().split(" GMT")[0]);
            String imageUrl = post.getMediaUrl();
            if (imageUrl != null) {
               FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                .getBytes(Long.MAX_VALUE)
                .addOnSuccessListener(bytes -> {
                    postImage.setVisibility(View.VISIBLE);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                   // postImage.setMaxHeight(250);
                    postImage.setImageBitmap(bmp);
                });

            }
        }
    }
}
