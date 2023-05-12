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
import android.widget.TextView;
import android.widget.Toast;

import com.smd.alertapp.DataLayer.User.IUserDAO;
import com.smd.alertapp.DataLayer.User.UserFirebaseDAO;
import com.smd.alertapp.Entities.User.HelplineUser;
import com.smd.alertapp.Entities.User.RegularUser;
import com.smd.alertapp.Entities.User.User;
import com.smd.alertapp.Entities.User.UserType;
import com.smd.alertapp.Utilities.SessionManager;
import com.smd.alertapp.R;

public class LoginActivity extends AppCompatActivity {

    SessionManager sessionManager;
    EditText phoneTxt, idTxt, pwdText;
    Button loginBtn;
    SwitchCompat userTypeSwitch;
    TextView signupTxt;
    IUserDAO dao;
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
        dao=new UserFirebaseDAO(LoginActivity.this);
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
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean noErrors=true;
                if ((phoneTxt.getText().toString().length() == 0 && !userTypeSwitch.isChecked()) ||(idTxt.getText().toString().length()==0 && userTypeSwitch.isChecked()) || pwdText.getText().length() == 0) {
                    Toast.makeText(LoginActivity.this, "Error, fields cannot be empty", Toast.LENGTH_SHORT).show();
                    noErrors=false;
                } else if(!userTypeSwitch.isChecked()) {
                    String pno = phoneTxt.getText().toString();
                    if (pno.startsWith("0")) {
                        pno = "+92" + pno.substring(1);
                        phoneTxt.setText(pno);
                    }
                    // Check if phone number starts with +92 and has 13 digits
                    if (!userTypeSwitch.isChecked()&&!pno.matches("^\\+92\\d{10}$")) {
                        Toast.makeText(LoginActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                        noErrors=false;
                    }
                }
                if(noErrors)
                    new LoginActivity.LoginUser().execute("");
            }
        });
    }


    public class LoginUser extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                //TODO: get user by phone-number/id from db to check if it already exists
                UserType userType=userTypeSwitch.isChecked()? UserType.HELPLINE : UserType.REGULAR;
                String id = getUserId();
                User u = dao.getById(id,userType);
                if(u!=null) {
                    if(u.getPassword().equals(pwdText.getText().toString())) {
                        if (u.getUserType() == UserType.REGULAR)
                            sessionManager.createLoginSession(((RegularUser) u).getPhoneNumber(),u.getUsername(), u.getPassword(), u.getUserType(), null);
                        else if (u.getUserType() == UserType.HELPLINE)
                            sessionManager.createLoginSession(((HelplineUser) u).getId(),u.getUsername(), u.getPassword(), u.getUserType(), ((HelplineUser)u).getHelplineType());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                        // start main activity for the user
                        Intent intent;
                        if(u.getUserType() == UserType.REGULAR)
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                        else
                            intent = new Intent(LoginActivity.this, HelplineMainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                        u=null;
                }
                if(u==null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Invalid Credentials. Login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            catch (Exception e){
                Log.e("ExceptionMsg",e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        String getUserId(){
            return userTypeSwitch.isChecked()?idTxt.getText().toString() : phoneTxt.getText().toString();
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