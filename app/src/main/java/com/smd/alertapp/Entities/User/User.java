package com.smd.alertapp.Entities.User;

import android.content.Context;

public abstract class User {
    protected String password;
    protected UserType userType;

    public User(String password, UserType userType) {
        this.password = password;
        this.userType = userType;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public abstract void login(Context ctx);
}


