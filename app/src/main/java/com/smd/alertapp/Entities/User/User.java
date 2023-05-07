package com.smd.alertapp.Entities.User;

import android.content.Context;

public abstract class User {
    protected String username;
    protected String password;
    protected UserType userType;

    public User(String username,String password, UserType userType) {
        this.password = password;
        this.userType = userType;
        this.username=username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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


