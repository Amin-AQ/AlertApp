package com.smd.alertapp;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;

public class AlertApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
/*        FirebaseOptions options = FirebaseOptions.fromResource(this);
        FirebaseApp.initializeApp(this, options, "AlertApp");

*//*        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:1044443573499:android:94f00f9dccf2f1c843d45e")
                .setApiKey("AIzaSyAGXaLQgorH--LHbZl1P4F9SmF8Oz62z6I")
                .setDatabaseUrl("https://emergencyalert-app-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .build();*//*

        //FirebaseApp.initializeApp(this, options,"AlertApp");*/
        FirebaseDatabase.getInstance("https://emergencyalert-app-default-rtdb.asia-southeast1.firebasedatabase.app/").setPersistenceEnabled(true);
    }
}
