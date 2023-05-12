package com.smd.alertapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.smd.alertapp.Entities.User.UserType;
import com.smd.alertapp.R;
import com.smd.alertapp.Utilities.SessionManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SessionManager sessionManager=new SessionManager(getApplicationContext());
        // Wait for a few seconds and then start the MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sessionManager.isLoggedIn()) {
                    if (sessionManager.getUserType().equals(UserType.REGULAR.toString())) {
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashScreenActivity.this, HelplineMainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else
                    sessionManager.checkLogin();
            }
        }, 3000);
    }
}