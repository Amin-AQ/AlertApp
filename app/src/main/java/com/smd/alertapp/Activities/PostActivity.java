package com.smd.alertapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.smd.alertapp.Adapters.ContactsAdapter;
import com.smd.alertapp.Adapters.PostsAdapter;
import com.smd.alertapp.DataLayer.Post.IPostDAO;
import com.smd.alertapp.DataLayer.Post.PostFirebaseDAO;
import com.smd.alertapp.DataLayer.Post.PostsCallback;
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
        dao = new PostFirebaseDAO(PostActivity.this);
        recyclerView = findViewById(R.id.added_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        createPost = findViewById(R.id.create_button);
        postText = findViewById(R.id.create_post_text);
        bottomNav=findViewById(R.id.bottom_navigation);
        sessionManager = new SessionManager(getApplicationContext());
        userDetails = sessionManager.getUserDetails();
        dao.getPosts(new PostsCallback() {
            @Override
            public void onPostsReceived(ArrayList<Post> postsList) {
                posts = postsList;
                adapter = new PostsAdapter(posts, getSupportFragmentManager());
                recyclerView.setAdapter(adapter);
            }
        });
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
                    dao.getPosts(new PostsCallback() {
                        @Override
                        public void onPostsReceived(ArrayList<Post> postsList) {
                            posts = postsList;
                            adapter = new PostsAdapter(posts, getSupportFragmentManager());
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.menu_settings) {
                    startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }
}