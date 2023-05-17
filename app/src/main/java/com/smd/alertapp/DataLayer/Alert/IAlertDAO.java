package com.smd.alertapp.DataLayer.Alert;

import android.net.Uri;

import com.smd.alertapp.Entities.Alert.Alert;
import com.smd.alertapp.Entities.User.HelplineType;

public interface IAlertDAO {
    void save(Alert alert, AlertSentCallback callback);
    void getQuickAlerts(HelplineType helplineType,QuickAlertsCallback callback);
    void getCustomAlerts(HelplineType helplineType,CustomAlertsCallback callback);
    void uploadVideoToFirebase(Uri videoUri, VideoUploadCallback callback);
    void uploadAudioToFirebase(Uri audioUri, AudioUploadCallback callback);
    void fetchLocationLinks(final LocationLinksCallback callback);
}
