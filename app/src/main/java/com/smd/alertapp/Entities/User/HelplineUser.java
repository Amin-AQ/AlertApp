package com.smd.alertapp.Entities.User;

import android.content.Context;

public class HelplineUser extends User{

    private String id;

    public HelplineUser(String id, String password) {
        super(password, UserType.HELPLINE);
        this.id=id;
    }

    public HelplineUser(){
        super("",UserType.HELPLINE);
    }

    public HelplineUser(String password, UserType userType, String id) {
        super(password, userType);
        this.id = id;
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
