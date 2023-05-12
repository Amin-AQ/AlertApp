package com.smd.alertapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.smd.alertapp.Entities.User.UserType;
import com.smd.alertapp.Fragments.ContactsFragment;
import com.smd.alertapp.Fragments.UserManualFragment;
import com.smd.alertapp.R;
import com.smd.alertapp.Utilities.SessionManager;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    SessionManager sessionManager;
    HashMap<String,String>userDetails;
    TextView logoutTxt,manualTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sessionManager=new SessionManager(getApplicationContext());
        userDetails=sessionManager.getUserDetails();
        bottomNav=findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.menu_settings);
        manualTxt=findViewById(R.id.usermanual);
        logoutTxt=findViewById(R.id.logout);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_posts) {
                    startActivity(new Intent(getApplicationContext(), PostActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.menu_home) {
                    if(userDetails.get("usertype").equals(UserType.REGULAR.toString()))
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    else
                        startActivity(new Intent(getApplicationContext(), HelplineMainActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            }
        });
        manualTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                UserManualFragment userManualFragment = new UserManualFragment();
                fragmentTransaction.replace(R.id.fragment_container, userManualFragment);
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit();
            }
        });
        logoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.LogoutUser();
                finish();
            }
        });
    }
}