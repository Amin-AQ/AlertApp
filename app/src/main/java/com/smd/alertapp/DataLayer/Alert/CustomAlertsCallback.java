package com.smd.alertapp.DataLayer.Alert;

import com.smd.alertapp.Entities.Alert.Alert;

import java.util.ArrayList;

public interface CustomAlertsCallback {
    void onCustomAlertsReceived(ArrayList<Alert> alert);
}
