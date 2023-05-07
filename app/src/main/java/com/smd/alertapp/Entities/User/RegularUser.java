package com.smd.alertapp.Entities.User;

import android.content.Context;
import android.widget.Toast;

import java.util.Date;

public class RegularUser extends User{

    private String phoneNumber;
    private Date dob;
    private String gender;

    public RegularUser(String username,String password, UserType userType, String phoneNumber, Date dob, String gender) {
        super(username,password, userType);
        this.phoneNumber = phoneNumber;
        this.dob = dob;
        this.gender = gender;
    }

    public RegularUser(String username,String phoneNumber, String password, Date dob, String gender) {
        super(username,password, UserType.REGULAR);
        this.phoneNumber = phoneNumber;
        this.dob = dob;
        this.gender = gender;
    }

    public RegularUser() {
        super("","", UserType.REGULAR);
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void login(Context ctx) {
        if (phoneNumber.length() == 0 || password.length() == 0) {
            Toast.makeText(ctx, "Error, fields cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            if (phoneNumber.startsWith("0"))
                phoneNumber = "+92" + phoneNumber.substring(1);
            // Check if phone number starts with +92 and has 13 digits
            if (!phoneNumber.matches("^\\+92\\d{10}$"))
                Toast.makeText(ctx, "Invalid phone number", Toast.LENGTH_SHORT).show();
        }
    }
}
