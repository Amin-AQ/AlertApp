package com.smd.alertapp.Entities.User;

import android.content.Context;
import android.widget.Toast;

public class RegularUser extends User{

    private String phoneNumber;

    public RegularUser(String password, UserType userType, String phoneNumber) {
        super(password, userType);
        this.phoneNumber = phoneNumber;
    }

    public RegularUser(User u, String pno){
        super(u.password,u.userType);
        phoneNumber=pno;
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
