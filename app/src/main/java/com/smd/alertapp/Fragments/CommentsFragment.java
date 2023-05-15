package com.smd.alertapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smd.alertapp.Activities.PostActivity;
import com.smd.alertapp.Adapters.CommentsAdapter;
import com.smd.alertapp.Adapters.PostsAdapter;
import com.smd.alertapp.DataLayer.Comment.CommentCallback;
import com.smd.alertapp.DataLayer.Comment.CommentFirebaseDAO;
import com.smd.alertapp.DataLayer.Post.PostsCallback;
import com.smd.alertapp.Entities.Comment;
import com.smd.alertapp.Entities.Post;
import com.smd.alertapp.R;
import com.smd.alertapp.Utilities.SessionManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.RecursiveAction;

public class CommentsFragment extends Fragment {
    private static final String ARG_POST_ID = "postId";

    private ArrayList<Comment> comments;
    private ImageButton commentButton, backButton;
    private TextView newCommentText;
    private RecyclerView commentsList;
    private CommentsAdapter adapter;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    public static CommentsFragment newInstance(String postId) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        newCommentText = view.findViewById(R.id.new_comment_text);
        commentButton = view.findViewById(R.id.comment_button);
        backButton = view.findViewById(R.id.back_button);
        commentsList = view.findViewById(R.id.comment_box);
        commentsList.setLayoutManager(new LinearLayoutManager(getContext()));
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        userDetails = sessionManager.getUserDetails();
        String postId = getArguments().getString(ARG_POST_ID);
        System.out.println("Post id received: " + postId);
        CommentFirebaseDAO dao = new CommentFirebaseDAO(this.getContext());
        dao.getComments(new CommentCallback() {
            @Override
            public void onCommentsReceived(ArrayList<Comment> coms) {
                comments = coms;
                adapter = new CommentsAdapter(comments);
                commentsList.setAdapter(adapter);
            }
        }, postId);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newCommentText.getText().toString().length() == 0) {
                    Toast.makeText(getContext(), "Comment text is empty", Toast.LENGTH_SHORT).show();
                } else {
                    String text = newCommentText.getText().toString();
                    Comment newComment = new Comment(postId, userDetails.get("username"), text);
                    dao.save(newComment);
                    Log.e("Comment for post: ", postId);
                    newCommentText.setText("");
                    dao.getComments(new CommentCallback() {
                        @Override
                        public void onCommentsReceived(ArrayList<Comment> coms) {
                            comments = coms;
                            adapter = new CommentsAdapter(comments);
                            commentsList.setAdapter(adapter);
                        }
                    }, postId);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        return view;
    }

}