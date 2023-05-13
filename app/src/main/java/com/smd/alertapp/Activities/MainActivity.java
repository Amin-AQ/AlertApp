package com.smd.alertapp.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smd.alertapp.DataLayer.Alert.AlertFirebaseDAO;
import com.smd.alertapp.DataLayer.Alert.IAlertDAO;
import com.smd.alertapp.Entities.Alert.CustomAlert;
import com.smd.alertapp.Entities.Alert.QuickAlert;
import com.smd.alertapp.Entities.User.HelplineType;
import com.smd.alertapp.Entities.User.HelplineUser;
import com.smd.alertapp.Fragments.ContactsFragment;
import com.smd.alertapp.R;
import com.smd.alertapp.Utilities.LocationUtil;
import com.smd.alertapp.Utilities.PermissionUtil;
import com.smd.alertapp.Utilities.SessionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_AUDIO_CAPTURE = 10;
    private static final int REQUEST_VIDEO_CAPTURE = 11;
    public static boolean taskFlag=false;
    EditText customAlertMsg;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;
    RadioButton alertAll,alertContacts,alertHelplines;
    CardView quickAlertCard, editContactsCard, callHelplineCard;
    BottomNavigationView bottomNav;
    ImageView micView, videoView, sendBtnView;
    FrameLayout contactFrag;
    IAlertDAO alertDAO;
    private File audioFile=null;
    private File videoFile=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(getApplicationContext());
        if(!sessionManager.checkLogin())
            finish();
        userDetails = sessionManager.getUserDetails();
        alertDAO=new AlertFirebaseDAO(MainActivity.this);
        customAlertMsg=findViewById(R.id.custom_alert_text);
        contactFrag=findViewById(R.id.fragment_container);
        alertAll=findViewById(R.id.radio_button_both);
        alertContacts=findViewById(R.id.radio_button_alert_contacts);
        alertHelplines=findViewById(R.id.radio_button_alert_helplines);
        quickAlertCard=findViewById(R.id.quickAlertCard);
        editContactsCard=findViewById(R.id.editContactsCard);
        callHelplineCard=findViewById(R.id.contact_helpline_card);
        bottomNav=findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.menu_home);
        micView=findViewById(R.id.microphone_icon);
        videoView=findViewById(R.id.video_icon);
        sendBtnView=findViewById(R.id.send_button);
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

    void quickAlert(){
        LocationUtil locationUtil=new LocationUtil(MainActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("ContactPrefs", Context.MODE_PRIVATE);
        Set<String> mSelectedContacts = sharedPreferences.getStringSet("selectedContacts", new HashSet<String>());
        locationUtil.getLocation(new LocationCallback() {
            @Override
            public void onLocationObtained(String location) {
                List<String> m = new ArrayList<String>(mSelectedContacts);
                QuickAlert alert = new QuickAlert(UUID.randomUUID().toString(), userDetails.get("id"), HelplineType.POLICE, location, m);
                alert.send(MainActivity.this,alertAll.isChecked()||alertContacts.isChecked(),alertAll.isChecked()||alertHelplines.isChecked(),userDetails.get("username"), alertDAO);
            }
        });
    }

    void customAlert(){
        LocationUtil locationUtil=new LocationUtil(MainActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("ContactPrefs", Context.MODE_PRIVATE);
        Set<String> mSelectedContacts = sharedPreferences.getStringSet("selectedContacts", new HashSet<String>());
        locationUtil.getLocation(new LocationCallback() {
            @Override
            public void onLocationObtained(String location) {
                List<String> m = new ArrayList<String>(mSelectedContacts);
                CustomAlert alert = new CustomAlert(UUID.randomUUID().toString(), userDetails.get("id"), HelplineType.POLICE, location, m, customAlertMsg.getText().toString());
                alert.send(MainActivity.this,alertAll.isChecked()||alertContacts.isChecked(),alertAll.isChecked()||alertHelplines.isChecked(),userDetails.get("username"), alertDAO);
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
        // Create a new audio file
        String audioFileName = "audio_" + System.currentTimeMillis() + ".3gp";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        audioFile = new File(storageDir, audioFileName);

        // Create an intent to record audio
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(audioFile));
        startActivityForResult(intent, REQUEST_AUDIO_CAPTURE);
    }

    private void recordVideo() {
        // Create a new video file
        String videoFileName = "video_" + System.currentTimeMillis() + ".mp4";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        videoFile = new File(storageDir, videoFileName);

        // Create an intent to record video
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
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
        else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            // Get the audio file URI from the intent
            Uri videoUri = data.getData();

            // Generate the content URI using FileProvider
            Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "com.smd.alertapp.fileprovider", new File(videoUri.getPath()));

            // Upload the video file to Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference videoRef = storageRef.child("video").child(contentUri.getLastPathSegment());

            UploadTask uploadTask = videoRef.putFile(contentUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Audio file uploaded successfully
                    // Get the download URL of the uploaded file
                    videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            // Handle the audio file URL
                            String videoUrl = downloadUri.toString();
                            // Pass the audioUrl to the CustomAlert object or save it directly to the Firebase Realtime Database
                            // ...
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors
                }
            });
        }
        else  if (requestCode == REQUEST_AUDIO_CAPTURE && resultCode == RESULT_OK) {
            // Get the audio file URI from the intent
            Uri audioUri = data.getData();

            // Generate the content URI using FileProvider
            Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "com.smd.alertapp.fileprovider", new File(audioUri.getPath()));

            // Upload the video file to Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference audioRef = storageRef.child("audio").child(contentUri.getLastPathSegment());

            UploadTask uploadTask = audioRef.putFile(contentUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Audio file uploaded successfully
                        // Get the download URL of the uploaded file
                        audioRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Handle the audio file URL
                                String audioUrl = downloadUri.toString();
                                // Pass the audioUrl to the CustomAlert object or save it directly to the Firebase Realtime Database
                                // ...
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                    }
                });
            }
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