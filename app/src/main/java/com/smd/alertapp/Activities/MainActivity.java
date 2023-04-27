package com.smd.alertapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.smd.alertapp.Fragments.ContactsFragment;
import com.smd.alertapp.R;
import com.smd.alertapp.Utilities.PermissionUtil;
import com.smd.alertapp.Utilities.SessionManager;
import com.smd.alertapp.Utilities.ContactsUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    SessionManager sessionManager;
    HashMap<String, String> userDetails;
    Intent intent;


    FrameLayout contactFrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //contactFrag=findViewById(R.id.fragment_container);
        Log.d("MainActivity","OnCreate called");
        sessionManager = new SessionManager(getApplicationContext());
        if(!sessionManager.checkLogin())
            finish();
        userDetails = sessionManager.getUserDetails();
        Log.d("Deb",userDetails.toString());
        PermissionUtil.requestReadContactsPermission(MainActivity.this);
        if(checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
        {
            List<Map<String,String>> contactList= ContactsUtil.getContacts(MainActivity.this);
            //Log.d("Contacts",contactList.toString());
/*            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            ContactsFragment contactsFragment = new ContactsFragment();
            fragmentTransaction.replace(R.id.fragment_container, contactsFragment);
            fragmentTransaction.addToBackStack(null);

            fragmentTransaction.commit();*/

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if the permission we requested was granted.
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // The permission was granted, do what we need to do.
            // For example, get the list of contacts.
        } else {
            // The permission was denied, show a message to the user.
            Toast.makeText(MainActivity.this, "Permission denied, you won't be able to use this feature", Toast.LENGTH_LONG).show();
        }
    }

}