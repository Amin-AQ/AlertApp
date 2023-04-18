package com.smd.alertapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.smd.alertapp.Entities.User.HelplineUser;
import com.smd.alertapp.Entities.User.RegularUser;
import com.smd.alertapp.Entities.User.User;
import com.smd.alertapp.Entities.User.UserType;
import com.smd.alertapp.R;
import com.smd.alertapp.Utilities.SessionManager;

import java.util.Arrays;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager=new SessionManager(getApplicationContext());
    }


    public class LoginUser extends AsyncTask<User,String,String>{

        @Override
        protected String doInBackground(User... users) {
            try {
                //TODO: get user by phone-number/id from db to check if it already exists

                User u = Arrays.stream(users).findFirst().get();
                if (u.getUserType() == UserType.REGULAR)
                    sessionManager.createLoginSession(((RegularUser) u).getPhoneNumber(), u.getPassword(), u.getUserType());
                else if (u.getUserType() == UserType.HELPLINE)
                    sessionManager.createLoginSession(((HelplineUser) u).getId(), u.getPassword(), u.getUserType());

                // start main activity for the user
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
            catch (Exception e){
                Log.e("ExceptionMsg",e.getMessage());
                Log.e("ExceptionCause", Objects.requireNonNull(e.getCause()).toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}