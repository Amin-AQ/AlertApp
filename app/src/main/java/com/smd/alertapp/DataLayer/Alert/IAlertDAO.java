package com.smd.alertapp.DataLayer.Alert;

import android.net.Uri;

import com.smd.alertapp.Entities.Alert.Alert;

public interface IAlertDAO {
    void save(Alert alert);
    void getQuickAlerts(QuickAlertsCallback callback);
    void getCustomAlerts(CustomAlertsCallback callback);
    void uploadVideoToFirebase(Uri videoUri, VideoUploadCallback callback);
    void uploadAudioToFirebase(Uri audioUri, AudioUploadCallback callback);
}
