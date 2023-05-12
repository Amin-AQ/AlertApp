package com.smd.alertapp.DataLayer.Alert;

import com.smd.alertapp.Entities.Alert.Alert;

import java.util.ArrayList;

// Interface to load alerts from firebase to helpline's main activity
public interface QuickAlertsCallback {
    void onQuickAlertsReceived(ArrayList<Alert>alert);
}
