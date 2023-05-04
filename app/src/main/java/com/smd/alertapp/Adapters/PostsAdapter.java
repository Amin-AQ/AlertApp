package com.smd.alertapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.smd.alertapp.Entities.Post;
import com.smd.alertapp.R;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    ArrayList<Post> postList;

    public PostsAdapter(ArrayList<Post> posts) {
        postList = posts;
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
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postText, postUser;
        CardView commentCard;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postText = itemView.findViewById(R.id.postText);
            postUser = itemView.findViewById(R.id.postUser);
            commentCard = itemView.findViewById(R.id.viewCommentsCard);
        }
        public void bind(Post post) {
            postText.setText(post.getText());
            postUser.setText(post.getUserId());
        }
    }
}
