package com.smd.alertapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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
    ArrayList<QuickAlert> quickAlerts;
    ArrayList<CustomAlert>customAlerts;
    AlertAdapter quickAlertAdapter, customAlertAdaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpline_main);
        sessionManager=new SessionManager(getApplicationContext());
        userDetails=sessionManager.getUserDetails();
        quickAlertView=findViewById(R.id.quickalertsview);
        customAlertView=findViewById(R.id.customalertsview);
        quickAlertView.setLayoutManager(new LinearLayoutManager(this));
        quickAlertView.setHasFixedSize(true);
        customAlertView.setLayoutManager(new LinearLayoutManager(this));
        customAlertView.setHasFixedSize(true);
        alertDAO=new AlertFirebaseDAO(getApplicationContext());
        alertDAO.getQuickAlerts(new QuickAlertsCallback() {
            @Override
            public void onQuickAlertsReceived(ArrayList<Alert> alerts) {
                quickAlertAdapter=new AlertAdapter(alerts,getSupportFragmentManager());
                quickAlertView.setAdapter(quickAlertAdapter);
            }
        });
        alertDAO.getCustomAlerts(new CustomAlertsCallback() {
            @Override
            public void onCustomAlertsReceived(ArrayList<Alert> alerts) {
                customAlertAdaptor=new AlertAdapter(alerts,getSupportFragmentManager());
                customAlertView.setAdapter(customAlertAdaptor);
            }
        });

    }
}