package com.smd.alertapp.Entities.Alert;

import androidx.core.app.ActivityCompat;

import com.smd.alertapp.Entities.User.HelplineType;

import java.util.ArrayList;

public class QuickAlert extends Alert {
    public QuickAlert(String alertId, String userId, HelplineType helplineType, String location, ArrayList<String> contactList) {
        super(alertId, AlertType.QUICK_ALERT, userId, helplineType, location, contactList);
    }

    @Override
    public void send() {
    }
}
