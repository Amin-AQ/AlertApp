package com.smd.alertapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.smd.alertapp.Entities.User.HelplineUser;
import com.smd.alertapp.Entities.User.RegularUser;
import com.smd.alertapp.Entities.User.User;
import com.smd.alertapp.Entities.User.UserType;
import com.smd.alertapp.Utilities.SessionManager;
import com.smd.alertapp.R;


import java.util.Arrays;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    SessionManager sessionManager;
    EditText phoneTxt, idTxt, pwdText;
    Button loginBtn;
    SwitchCompat userTypeSwitch;
    TextView signupTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("MainActivity","OnCreate called");
        sessionManager=new SessionManager(getApplicationContext());
        phoneTxt=(EditText) findViewById(R.id.phone_number);
        idTxt=(EditText) findViewById(R.id.id_text);
        pwdText=(EditText) findViewById(R.id.password);
        signupTxt=findViewById(R.id.signup_textview);
        loginBtn=findViewById(R.id.login_button);
        userTypeSwitch=findViewById(R.id.switch_user_type);
        signupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
        userTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    phoneTxt.setVisibility(View.INVISIBLE);
                    idTxt.setVisibility(View.VISIBLE);
                }
                else{
                    phoneTxt.setVisibility(View.VISIBLE);
                    idTxt.setVisibility(View.INVISIBLE);
                }
            }
        });
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