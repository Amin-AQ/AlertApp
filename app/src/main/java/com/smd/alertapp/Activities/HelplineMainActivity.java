package com.smd.alertapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.smd.alertapp.Adapters.AlertAdapter;
import com.smd.alertapp.DataLayer.Alert.AlertFirebaseDAO;
import com.smd.alertapp.DataLayer.Alert.CustomAlertsCallback;
import com.smd.alertapp.DataLayer.Alert.IAlertDAO;
import com.smd.alertapp.DataLayer.Alert.QuickAlertsCallback;
import com.smd.alertapp.Entities.Alert.Alert;
import com.smd.alertapp.Entities.Alert.CustomAlert;
import com.smd.alertapp.Entities.Alert.QuickAlert;
import com.smd.alertapp.R;
import com.smd.alertapp.Utilities.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class HelplineMainActivity extends AppCompatActivity {

    SessionManager sessionManager;
    RecyclerView quickAlertView, customAlertView;
    HashMap<String, String> userDetails;
    IAlertDAO alertDAO;
    ArrayList<Alert> quickAlerts;
    ArrayList<Alert> customAlerts;
    BottomNavigationView bottomNav;
    AlertAdapter quickAlertAdapter, customAlertAdaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpline_main);
        sessionManager=new SessionManager(getApplicationContext());
        userDetails=sessionManager.getUserDetails();
        bottomNav=findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.menu_home);
        quickAlertView=findViewById(R.id.quickalertsview);
        customAlertView=findViewById(R.id.customalertsview);
        quickAlertView.setLayoutManager(new LinearLayoutManager(this));
        quickAlertView.setHasFixedSize(true);
        customAlertView.setLayoutManager(new LinearLayoutManager(this));
        customAlertView.setHasFixedSize(true);
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
        alertDAO=new AlertFirebaseDAO(getApplicationContext());
        alertDAO.getQuickAlerts(new QuickAlertsCallback() {
            @Override
            public void onQuickAlertsReceived(ArrayList<Alert> alerts) {
                quickAlerts=alerts;
                Log.d("Deb",quickAlerts.toString());
                quickAlertAdapter=new AlertAdapter(quickAlerts,getSupportFragmentManager());
                quickAlertView.setAdapter(quickAlertAdapter);
            }
        });
        alertDAO.getCustomAlerts(new CustomAlertsCallback() {
            @Override
            public void onCustomAlertsReceived(ArrayList<Alert> alerts) {
                customAlerts=alerts;
                customAlertAdaptor=new AlertAdapter(customAlerts,getSupportFragmentManager());
                customAlertView.setAdapter(customAlertAdaptor);
            }
        });

    }
}