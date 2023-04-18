package com.smd.alertapp.Entities.User;

import android.content.Context;

public class HelplineUser extends User{

    private String id;

    public HelplineUser(String password, UserType userType) {
        super(password, userType);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void login(Context ctx) {

    }
}
