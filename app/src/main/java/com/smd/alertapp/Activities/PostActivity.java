package com.smd.alertapp.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.smd.alertapp.Adapters.ContactsAdapter;
import com.smd.alertapp.Adapters.PostsAdapter;
import com.smd.alertapp.DataLayer.Post.IPostDAO;
import com.smd.alertapp.DataLayer.Post.PostFirebaseDAO;
import com.smd.alertapp.DataLayer.Post.PostsCallback;
import com.smd.alertapp.Entities.Post;
import com.smd.alertapp.Entities.User.UserType;
import com.smd.alertapp.R;
import com.smd.alertapp.Utilities.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_FILE = 1;
    BottomNavigationView bottomNav;
    ImageButton createPost, uploadFile;
    EditText postText;
    ArrayList<Post> posts;
    IPostDAO dao;
    ImageView postImage;
    private InterstitialAd mInterstitialAd;
    SessionManager sessionManager;
//    ActivityResultLauncher mGetContentLauncher;
    HashMap<String, String> userDetails;
    PostsAdapter adapter;
    RecyclerView recyclerView;
    Uri fileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        dao = new PostFirebaseDAO(PostActivity.this);
        recyclerView = findViewById(R.id.added_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        createPost = findViewById(R.id.create_button);
        uploadFile = findViewById(R.id.upload_post_button);
        postText = findViewById(R.id.create_post_text);
        bottomNav=findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.menu_posts);
        postImage = findViewById(R.id.post_image);
        sessionManager = new SessionManager(getApplicationContext());
        userDetails = sessionManager.getUserDetails();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-5649663507193610/6846984843", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("Debug", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("Debug", loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
        dao.getPosts(new PostsCallback() {
            @Override
            public void onPostsReceived(ArrayList<Post> postsList) {
                posts = postsList;
                Collections.sort(posts, new Comparator<Post>() {
                    @Override
                    public int compare(Post o1, Post o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });
                adapter = new PostsAdapter(posts, getSupportFragmentManager());
                recyclerView.setAdapter(adapter);
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                            fileUri = data.getData();
                            Log.e("File uploaded", fileUri.toString());
                            postImage.setImageURI(fileUri);
                    } else {
                        Toast.makeText(PostActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postText.getText().toString().length() == 0) {
                    Toast.makeText(PostActivity.this, "Post text is empty", Toast.LENGTH_SHORT).show();
                } else {
                    String text = postText.getText().toString();
                    Post newPost = new Post(text, userDetails.get("username"));
                    //Log.e("Saving post uri: ", fileUri.toString());
                    dao.save(newPost, fileUri);
                    postText.setText("");
                    postImage.setImageURI(null);
                    dao.getPosts(new PostsCallback() {
                        @Override
                        public void onPostsReceived(ArrayList<Post> postsList) {
                            posts = postsList;
                            Collections.sort(posts, new Comparator<Post>() {
                                @Override
                                public int compare(Post o1, Post o2) {
                                    return o2.getDate().compareTo(o1.getDate());
                                }
                            });
                            adapter = new PostsAdapter(posts, getSupportFragmentManager());
                            recyclerView.setAdapter(adapter);
                            showAd();
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
                    if(userDetails.get("usertype").equals(UserType.REGULAR.toString()))
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    else
                        startActivity(new Intent(getApplicationContext(), HelplineMainActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.menu_settings) {
                    startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            }
        });

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
    }

    void showAd(){
        if (mInterstitialAd != null) {
            setFullScreenCallback();
            mInterstitialAd.show(PostActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-5649663507193610/6846984843", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("Debug", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("Debug", loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }
    void setFullScreenCallback(){
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d("Deubg", "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d("Deubg", "Ad dismissed fullscreen content.");
                mInterstitialAd = null;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                // Called when ad fails to show.
                Log.e("Deubg", "Ad failed to show fullscreen content.");
                mInterstitialAd = null;
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d("Deubg", "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d("Deubg", "Ad showed fullscreen content.");
            }

        });
    }

}