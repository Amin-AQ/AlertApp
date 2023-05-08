package com.smd.alertapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

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
import com.smd.alertapp.Entities.Alert.QuickAlert;
import com.smd.alertapp.Entities.User.HelplineType;
import com.smd.alertapp.Fragments.ContactsFragment;
import com.smd.alertapp.R;
import com.smd.alertapp.Utilities.LocationUtil;
import com.smd.alertapp.Utilities.PermissionUtil;
import com.smd.alertapp.Utilities.SessionManager;
import com.smd.alertapp.Utilities.ContactsUtil;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static boolean taskFlag=false;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;
    RadioButton alertAll,alertContacts,alertHelplines;
    Intent intent;
    CardView quickAlertCard, editContactsCard, callHelplineCard;
    BottomNavigationView bottomNav;
    FrameLayout contactFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactFrag=findViewById(R.id.fragment_container);
        alertAll=findViewById(R.id.radio_button_both);
        alertContacts=findViewById(R.id.radio_button_alert_contacts);
        alertHelplines=findViewById(R.id.radio_button_alert_helplines);
        quickAlertCard=findViewById(R.id.quickAlertCard);
        editContactsCard=findViewById(R.id.editContactsCard);
        callHelplineCard=findViewById(R.id.contact_helpline_card);
        bottomNav=findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.menu_home);
        Log.d("MainActivity","OnCreate called");
        sessionManager = new SessionManager(getApplicationContext());
        if(!sessionManager.checkLogin())
            finish();
        userDetails = sessionManager.getUserDetails();
        Log.d("Deb",userDetails.toString());
        PermissionUtil.requestReadContactsPermission(MainActivity.this);
        editContactsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtil.requestReadContactsPermission(MainActivity.this);
                if(checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                {
                    //Log.d("Contacts",contactList.toString());
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    ContactsFragment contactsFragment = new ContactsFragment();
                    fragmentTransaction.replace(R.id.fragment_container, contactsFragment);
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();
                }
            }
        });

        quickAlertCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[]permissions=new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                 Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                                 Manifest.permission.SEND_SMS};
                if(!hasPermissions(MainActivity.this,permissions)){
                     ActivityCompat.requestPermissions(MainActivity.this,permissions,2);
                }
                else
                    checkLocationSettings();
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_posts) {
                    startActivity(new Intent(getApplicationContext(), PostActivity.class));
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
        /*Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("whatsapp:+923236037484"),
                        new com.twilio.type.PhoneNumber("whatsapp:+13204335727"),
                        "Hello, Hope ur doing well. I am not, help me, haaalp. My location is blah blahah")
                .create();
        Message message2 = Message.creator(
                        new com.twilio.type.PhoneNumber("whatsapp:+923244270720"),
                        new com.twilio.type.PhoneNumber("whatsapp:+13204335727"),
                        "Hello, Hope ur doing well. I am not, help me, haaalp. My location is blah blahah")
                .create();
        Message message3 = Message.creator(
                        new com.twilio.type.PhoneNumber("whatsapp:+923217750712"),
                        new com.twilio.type.PhoneNumber("whatsapp:+13204335727"),
                        "Hello, Hope ur doing well. I am not, help me, haaalp. My location is blah blahah")
                .create();
        Message message4 = Message.creator(
                        new com.twilio.type.PhoneNumber("whatsapp:+923346119000"),
                        new com.twilio.type.PhoneNumber("whatsapp:+13204335727"),
                        "Hello, Hope ur doing well. I am not, help me, haaalp. My location is blah blahah")
                .create();
        Log.d("Whatsapp",message.getSid());
        Log.d("Whatsapp",message2.getSid());
        Log.d("Whatsapp",message3.getSid());
        Log.d("Whatsapp",message4.getSid());*/
    }

    void quickAlert(){
        LocationUtil locationUtil=new LocationUtil(MainActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("ContactPrefs", Context.MODE_PRIVATE);
        Set<String> mSelectedContacts = sharedPreferences.getStringSet("selectedContacts", new HashSet<String>());
        locationUtil.getLocation(new LocationCallback() {
            @Override
            public void onLocationObtained(String location) {
                QuickAlert alert = new QuickAlert(UUID.randomUUID().toString(), userDetails.get("phonenumber"), HelplineType.POLICE, location, mSelectedContacts);
                alert.send(alertAll.isChecked()||alertContacts.isChecked(),alertAll.isChecked()||alertHelplines.isChecked(),userDetails.get("username"));
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if the permission we requested was granted.
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // The permission was granted, do what we need to do.
            // For example, get the list of contacts.
        } else if(requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            boolean all=true;
            for (int res:grantResults){
                if(res!=PackageManager.PERMISSION_GRANTED)
                    all=false;
            }
            if(!all)
                Toast.makeText(MainActivity.this, "Permission denied, you won't be able to use this feature", Toast.LENGTH_LONG).show();
            else
                checkLocationSettings();
        }
        else {
            // The permission was denied, show a message to the user.
            Toast.makeText(MainActivity.this, "Permission denied, you won't be able to use this feature", Toast.LENGTH_LONG).show();
        }
    }

    void checkLocationSettings(){
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
                quickAlert();
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
                        resolvable.startResolutionForResult(MainActivity.this,
                                3);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
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