package com.smd.alertapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smd.alertapp.Adapters.ContactsAdapter;
import com.smd.alertapp.Adapters.PostsAdapter;
import com.smd.alertapp.DataLayer.Post.IPostDAO;
import com.smd.alertapp.Entities.Post;
import com.smd.alertapp.R;
import com.smd.alertapp.Utilities.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    ImageButton createPost;
    EditText postText;
    ArrayList<Post> posts;
    IPostDAO dao;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;
    PostsAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        recyclerView = findViewById(R.id.added_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        createPost = findViewById(R.id.create_button);
        postText = findViewById(R.id.create_post_text);
        sessionManager = new SessionManager(getApplicationContext());
        userDetails = sessionManager.getUserDetails();
        posts = dao.load();
        adapter = new PostsAdapter(posts);
        recyclerView.setAdapter(adapter);
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postText.getText().toString().length() == 0) {
                    Toast.makeText(PostActivity.this, "Post text is empty", Toast.LENGTH_SHORT).show();
                } else {
                    String text = postText.getText().toString();
                    Post newPost = new Post(text, "124301");
                    dao.save(newPost);
                    postText.setText("");
                    posts = dao.load();
                    adapter = new PostsAdapter(posts);
                    recyclerView.setAdapter(adapter);
                }
            }
        });

        bottomNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedItemId = bottomNav.getSelectedItemId();
                if (selectedItemId == R.id.menu_posts) {
                    startActivity(new Intent(getApplicationContext(),PostActivity.class));
                    overridePendingTransition(0,0);
                }else if (selectedItemId==R.id.menu_settings){
                    startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                    overridePendingTransition(0,0);
                }
            }
        });
    }
}