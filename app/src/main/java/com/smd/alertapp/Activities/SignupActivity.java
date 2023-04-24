package com.smd.alertapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smd.alertapp.DataLayer.User.IUserDAO;
import com.smd.alertapp.DataLayer.User.UserFirebaseDAO;
import com.smd.alertapp.Entities.User.RegularUser;
import com.smd.alertapp.Entities.User.User;
import com.smd.alertapp.R;

import java.util.Date;

public class SignupActivity extends AppCompatActivity {

    EditText phoneTxt, pwdTxt, cPwdTxt;  // phone num, password and confirm password
    TextView loginTxt;
    Button signupBtn;
    RadioGroup radioGroup;
    RadioButton maleRadioBtn, femaleRadioBtn;
    DatePicker dobPicker;
    IUserDAO dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        phoneTxt=findViewById(R.id.phone_number);
        pwdTxt=findViewById(R.id.password);
        cPwdTxt=findViewById(R.id.confirm_password);
        loginTxt=findViewById(R.id.login_textview);
        signupBtn=findViewById(R.id.signup_button);
        radioGroup=findViewById(R.id.gender_radio_group);
        femaleRadioBtn=findViewById(R.id.female_radio_button);
        maleRadioBtn=findViewById(R.id.male_radio_button);
        dobPicker=findViewById(R.id.date_of_birth_picker);
        dao=new UserFirebaseDAO(SignupActivity.this);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Check if all fields are filled in
                    if (phoneTxt.getText().toString().isEmpty() ||
                            pwdTxt.getText().toString().isEmpty() ||
                            cPwdTxt.getText().toString().isEmpty()) {
                        Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Check if passwords match
                    if (!pwdTxt.getText().toString().equals(cPwdTxt.getText().toString())) {
                        Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String pno = phoneTxt.getText().toString();
                    if (pno.startsWith("0")) {
                        pno = "+92" + pno.substring(1);
                        phoneTxt.setText(pno);
                    }
                    // Check if phone number is valid
                    if (!phoneTxt.getText().toString().matches("^\\+92\\d{10}$")) {
                        Toast.makeText(SignupActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new SignupActivity.SignupUser().execute("");

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    public class SignupUser extends AsyncTask<String, String, String> {

        Boolean success=false;

        @Override
        protected void onPreExecute(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SignupActivity.this,"Processing . . .",Toast.LENGTH_LONG).show();
                }
            });
        }
        @Override
        protected void onPostExecute(String s){
            success=true;
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                // Get gender
                String gender = maleRadioBtn.isChecked() ? "M" : "F";
                // Get date of birth
                int year = dobPicker.getYear();
                int month = dobPicker.getMonth() + 1;
                int day = dobPicker.getDayOfMonth();
                java.util.Date date = new java.text.SimpleDateFormat("dd.MM.yyyy").parse(day + "." + month + "." + year);
                // Create user
                RegularUser user = new RegularUser(phoneTxt.getText().toString(), pwdTxt.getText().toString(),date,gender);
                phoneTxt.setText("");
                pwdTxt.setText("");
                cPwdTxt.setText("");
                // Create user in database
                // TODO: Implement this method
                dao.save(user);
                // Redirect to login screen
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }catch (Exception e) {
                success=false;
                Log.e("Registration Error", e.getMessage());
                Log.e("Cause", String.valueOf(e.getCause()));
            }
            return null;
        }

    }
}
