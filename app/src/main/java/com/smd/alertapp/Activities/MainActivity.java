package com.smd.alertapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.smd.alertapp.DataLayer.Alert.AlertFirebaseDAO;
import com.smd.alertapp.DataLayer.Alert.AlertSentCallback;
import com.smd.alertapp.DataLayer.Alert.AudioUploadCallback;
import com.smd.alertapp.DataLayer.Alert.IAlertDAO;
import com.smd.alertapp.DataLayer.Alert.LocationLinksCallback;
import com.smd.alertapp.DataLayer.Alert.VideoUploadCallback;
import com.smd.alertapp.Entities.Alert.CustomAlert;
import com.smd.alertapp.Entities.Alert.QuickAlert;
import com.smd.alertapp.Entities.User.HelplineType;
import com.smd.alertapp.Entities.User.HelplineUser;
import com.smd.alertapp.Entities.User.UserType;
import com.smd.alertapp.Fragments.ContactsFragment;
import com.smd.alertapp.R;
import com.smd.alertapp.Utilities.LocationUtil;
import com.smd.alertapp.Utilities.PermissionUtil;
import com.smd.alertapp.Utilities.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_AUDIO_CAPTURE = 10;
    private static final int REQUEST_VIDEO_CAPTURE = 11;
    public static boolean taskFlag=false;
    private InterstitialAd mInterstitialAd;
    EditText customAlertMsg;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;
    RadioButton alertAll,alertContacts,alertHelplines;
    CardView quickAlertCard, editContactsCard, callHelplineCard, checkPlacesCard;
    BottomNavigationView bottomNav;
    ImageView micView, videoView, sendBtnView;
    ProgressBar locationProgressBar, videoProgressBar, audioProgressBar;
    TextView locationProgressBarText, videoProgressBarText, audioProgressBarText;
    FrameLayout contactFrag;
    IAlertDAO alertDAO;
    ActivityResultLauncher<Intent> recordMediaResultLauncher, recordAudioResultLauncher;
    private Uri audioFile=null;
    private Uri videoFile=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(getApplicationContext());
        if(!sessionManager.checkLogin())
            finish();
        userDetails = sessionManager.getUserDetails();
        if(userDetails.get("usertype").equals(UserType.HELPLINE.toString())){
            sessionManager.LogoutUser();
            finish();
        }
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
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case "CALL_HELPLINE": {
                        String[]perms=new String[]{Manifest.permission.CALL_PHONE};
                        if(!hasPermissions(MainActivity.this,perms)){
                            ActivityCompat.requestPermissions(MainActivity.this,perms,4);
                        }
                        else
                            HelplineUser.callHelpline(MainActivity.this);
                        break;
                    }
                    case "EDIT_CONTACTS": {
                        if(checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                            launchEditContactsFragment();
                        else
                            PermissionUtil.requestReadContactsPermission(MainActivity.this);
                        break;
                    }
                    case "QUICK_ALERT": {
                        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.SEND_SMS};
                        String[] perms = new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION};
                        if (!hasPermissions(MainActivity.this, permissions)) {
                            ActivityCompat.requestPermissions(MainActivity.this, permissions, 2);
                        } else if (!hasPermissions(MainActivity.this, perms)) {
                            ActivityCompat.requestPermissions(MainActivity.this, perms, 3);
                        } else
                            checkLocationSettings(true);
                        break;
                    }
                }
            }
        }
        alertDAO=new AlertFirebaseDAO(MainActivity.this);
        locationProgressBar = findViewById(R.id.locationprogressbar);
        videoProgressBar = findViewById(R.id.videoprogressbar);
        audioProgressBar = findViewById(R.id.audioprogressbar);
        locationProgressBarText = findViewById(R.id.locationprogressbartext);
        videoProgressBarText = findViewById(R.id.videoprogressbartext);
        audioProgressBarText = findViewById(R.id.audioprogressbartext);
        customAlertMsg=findViewById(R.id.custom_alert_text);
        contactFrag=findViewById(R.id.fragment_container);
        alertAll=findViewById(R.id.radio_button_both);
        alertContacts=findViewById(R.id.radio_button_alert_contacts);
        alertHelplines=findViewById(R.id.radio_button_alert_helplines);
        quickAlertCard=findViewById(R.id.quickAlertCard);
        editContactsCard=findViewById(R.id.editContactsCard);
        checkPlacesCard=findViewById(R.id.check_places_card);
        callHelplineCard=findViewById(R.id.contact_helpline_card);
        bottomNav=findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.menu_home);
        micView=findViewById(R.id.microphone_icon);
        videoView=findViewById(R.id.video_icon);
        sendBtnView=findViewById(R.id.send_button);


        recordMediaResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            // Process the recorded video data here
                            if (data != null) {
                                Uri videoUri = data.getData();
                                Log.d("Uri",videoUri.toString());
                                videoFile=videoUri;
                                // Upload the video file to Firebase or perform any other operations
                                //uploadVideoToFirebase(videoUri);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "No Video Created", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        recordAudioResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            // Process the recorded video data here
                            if (data != null) {
                                Uri audioUri = data.getData();
                                Log.d("Uri",audioUri.toString());
                                audioFile=audioUri;
                                // Upload the video file to Firebase or perform any other operations
                                //uploadVideoToFirebase(videoUri);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "No Audio Created", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        String alertVideo, alertAudio;
        Log.d("MainActivity","OnCreate called");
        Log.d("Deb",userDetails.toString());
        editContactsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                    launchEditContactsFragment();
                else
                    PermissionUtil.requestReadContactsPermission(MainActivity.this);
            }
        });
        checkPlacesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlaces();
            }
        });
        quickAlertCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[]permissions=new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                 Manifest.permission.SEND_SMS};
                String[]perms=new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION};
                if(!hasPermissions(MainActivity.this,permissions)){
                     ActivityCompat.requestPermissions(MainActivity.this,permissions,2);
                }
                else if(!hasPermissions(MainActivity.this,perms)){
                    ActivityCompat.requestPermissions(MainActivity.this,perms,3);
                }
                else
                    checkLocationSettings(true);
            }
        });

        callHelplineCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[]perms=new String[]{Manifest.permission.CALL_PHONE};
                if(!hasPermissions(MainActivity.this,perms)){
                    ActivityCompat.requestPermissions(MainActivity.this,perms,4);
                }
                else
                    HelplineUser.callHelpline(MainActivity.this);
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_posts) {
                    startActivity(new Intent(getApplicationContext(), PostActivity.class));
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

        micView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[]perms=new String[]{Manifest.permission.RECORD_AUDIO};
                if(!hasPermissions(MainActivity.this,perms)){
                    ActivityCompat.requestPermissions(MainActivity.this,perms,5);
                }
                else
                    recordAudio();
            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[]perms=new String[]{Manifest.permission.CAMERA};
                if(!hasPermissions(MainActivity.this,perms)){
                    ActivityCompat.requestPermissions(MainActivity.this,perms,6);
                }
                else
                    recordVideo();
            }
        });

        sendBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String[]permissions=new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.SEND_SMS};
                    String[]perms=new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION};
                    if(!hasPermissions(MainActivity.this,permissions)){
                        ActivityCompat.requestPermissions(MainActivity.this,permissions,7);
                    }
                    else if(!hasPermissions(MainActivity.this,perms)){
                        ActivityCompat.requestPermissions(MainActivity.this,perms,8);
                    }
                    else
                        checkLocationSettings(false);
                }
        });
    }
    void checkPlaces(){
        startActivity(new Intent(getApplicationContext(), MapActivity.class));
        overridePendingTransition(0, 0);
    }
    void quickAlert(){
        LocationUtil locationUtil=new LocationUtil(MainActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("ContactPrefs", Context.MODE_PRIVATE);
        Set<String> mSelectedContacts = sharedPreferences.getStringSet("selectedContacts", new HashSet<String>());
        locationProgressBar.setVisibility(View.VISIBLE);
        locationProgressBarText.setVisibility(View.VISIBLE);
        locationUtil.getLocation(new LocationCallback() {
            @Override
            public void onLocationObtained(String location) {
                locationProgressBar.setVisibility(View.GONE);
                locationProgressBarText.setVisibility(View.GONE);
                List<String> m = new ArrayList<String>(mSelectedContacts);
                QuickAlert alert = new QuickAlert(UUID.randomUUID().toString(), userDetails.get("id"), HelplineType.POLICE, location, m);
                alert.send(MainActivity.this, alertAll.isChecked() || alertContacts.isChecked(), alertAll.isChecked() || alertHelplines.isChecked(), userDetails.get("username"), alertDAO, new AlertSentCallback() {
                    @Override
                    public void onAlertSent() {
                        showAd();
                    }
                });
            }
        });

    }

    void customAlert(){
        LocationUtil locationUtil=new LocationUtil(MainActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("ContactPrefs", Context.MODE_PRIVATE);
        Set<String> mSelectedContacts = sharedPreferences.getStringSet("selectedContacts", new HashSet<String>());
        locationProgressBar.setVisibility(View.VISIBLE);
        locationProgressBarText.setVisibility(View.VISIBLE);
        locationUtil.getLocation(new LocationCallback() {
            @Override
            public void onLocationObtained(String location) {
                locationProgressBar.setVisibility(View.GONE);
                locationProgressBarText.setVisibility(View.GONE);
                List<String> m = new ArrayList<>(mSelectedContacts);
                CustomAlert alert = new CustomAlert(UUID.randomUUID().toString(), userDetails.get("id"), HelplineType.POLICE, location, m, customAlertMsg.getText().toString());

                final AtomicBoolean audioUploaded = new AtomicBoolean(false);
                final AtomicBoolean videoUploaded = new AtomicBoolean(false);

                // Check if audio file exists
                if (audioFile != null) {
                    audioProgressBar.setVisibility(View.VISIBLE);
                    audioProgressBarText.setVisibility(View.VISIBLE);
                    alertDAO.uploadAudioToFirebase(audioFile, new AudioUploadCallback() {
                        @Override
                        public void onAudioUpload(String audioUrl) {
                            audioProgressBar.setVisibility(View.GONE);
                            audioProgressBarText.setVisibility(View.GONE);
                            if (audioUrl != null) {
                                Toast.makeText(MainActivity.this, "Uploading Audio", Toast.LENGTH_SHORT).show();
                                alert.setAudioUrl(audioUrl);
                                Toast.makeText(MainActivity.this, "Audio Uploaded", Toast.LENGTH_SHORT).show();
                                audioUploaded.set(true);
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to upload audio", Toast.LENGTH_SHORT).show();
                            }

                            // Check if both audio and video uploads are completed
                            if (audioUploaded.get() && videoUploaded.get()) {
                                alert.send(MainActivity.this, alertAll.isChecked() || alertContacts.isChecked(), alertAll.isChecked() || alertHelplines.isChecked(), userDetails.get("username"), alertDAO, new AlertSentCallback() {
                                    @Override
                                    public void onAlertSent() {
                                        showAd();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    audioUploaded.set(true);
                }

                // Check if video file exists
                if (videoFile != null) {
                    videoProgressBar.setVisibility(View.VISIBLE);
                    videoProgressBarText.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Uploading Video", Toast.LENGTH_SHORT).show();
                    alertDAO.uploadVideoToFirebase(videoFile, new VideoUploadCallback() {
                        @Override
                        public void onVideoUpload(String videoUrl) {
                            videoProgressBar.setVisibility(View.GONE);
                            videoProgressBarText.setVisibility(View.GONE);
                            if (videoUrl != null) {
                                alert.setVideoUrl(videoUrl);
                                Toast.makeText(MainActivity.this, "Video Uploaded", Toast.LENGTH_SHORT).show();
                                videoUploaded.set(true);
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to upload video", Toast.LENGTH_SHORT).show();
                            }

                            // Check if both audio and video uploads are completed
                            if (audioUploaded.get() && videoUploaded.get()) {
                                alert.send(MainActivity.this, alertAll.isChecked() || alertContacts.isChecked(), alertAll.isChecked() || alertHelplines.isChecked(), userDetails.get("username"), alertDAO, new AlertSentCallback() {
                                    @Override
                                    public void onAlertSent() {
                                        showAd();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    videoUploaded.set(true);
                }

                // Check if both audio and video files are not provided
                if (audioFile == null && videoFile == null) {
                    alert.send(MainActivity.this, alertAll.isChecked() || alertContacts.isChecked(), alertAll.isChecked() || alertHelplines.isChecked(), userDetails.get("username"), alertDAO, new AlertSentCallback() {
                        @Override
                        public void onAlertSent() {
                            showAd();
                        }
                    });
                }
                audioFile=null;
                videoFile=null;
            }
        });
    }
    void showAd(){
        if (mInterstitialAd != null) {
            setFullScreenCallback();
            mInterstitialAd.show(MainActivity.this);
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

    void launchEditContactsFragment(){
        //Log.d("Contacts",contactList.toString());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ContactsFragment contactsFragment = new ContactsFragment();
        fragmentTransaction.replace(R.id.fragment_container, contactsFragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    private void recordAudio() {
          // Create an intent to record audio
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        recordAudioResultLauncher.launch(intent);
    }

    private void recordVideo() {
        // Create an intent to record a video
        Intent recordVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        // Start the activity for result
        recordMediaResultLauncher.launch(recordVideoIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if the permission we requested was granted.
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchEditContactsFragment();
        } else if(requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            boolean all=true;
            for (int res:grantResults){
                if(res!=PackageManager.PERMISSION_GRANTED)
                    all=false;
            }
            if(!all)
                Toast.makeText(MainActivity.this, "Permission denied, you won't be able to use this feature", Toast.LENGTH_LONG).show();
            else {
                String[] perms = new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION};
                ActivityCompat.requestPermissions(MainActivity.this,perms,3);
            }
        }
        else if(requestCode==3&&grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            checkLocationSettings(true);
        }
        else if(requestCode==4&&grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            HelplineUser.callHelpline(MainActivity.this);
        }
        else if(requestCode==5&&grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            recordAudio();
        }
        else if(requestCode==6&&grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            recordVideo();
        }
        else if(requestCode==7&&grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            boolean all=true;
            for (int res:grantResults){
                if(res!=PackageManager.PERMISSION_GRANTED)
                    all=false;
            }
            if(!all)
                Toast.makeText(MainActivity.this, "Permission denied, you won't be able to use this feature", Toast.LENGTH_LONG).show();
            else {
                String[] perms = new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION};
                ActivityCompat.requestPermissions(MainActivity.this,perms,8);
            }

        }
        else if(requestCode==8&&grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            checkLocationSettings(false);
        }
        else {
            // The permission was denied, show a message to the user.
            Toast.makeText(MainActivity.this, "Permission denied, you won't be able to use this feature", Toast.LENGTH_LONG).show();
        }

    }

    void checkLocationSettings(boolean isQuickAlert){
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if(isQuickAlert)
                    quickAlert();
                else
                    customAlert();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        if(isQuickAlert)
                            resolvable.startResolutionForResult(MainActivity.this, 6);
                        else
                            resolvable.startResolutionForResult(MainActivity.this, 7);


                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&requestCode==6)
            quickAlert();
        else if(resultCode==RESULT_OK&&requestCode==7)
            customAlert();
        else if(requestCode==6||requestCode==7)
            Toast.makeText(MainActivity.this,"Location not enabled, please enable location first",Toast.LENGTH_LONG).show();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



    public interface LocationCallback {
        void onLocationObtained(String location);
    }

}