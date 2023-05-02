package com.smd.alertapp.Entities.User;

import android.content.Context;
import android.widget.Toast;

public class HelplineUser extends User{

    private String id;

    private HelplineType helplineType;

    public HelplineUser(String id, String password, HelplineType type) {
        super(password, UserType.HELPLINE);
        this.id=id;
        this.helplineType=type;
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

    public HelplineType getHelplineType() {
        return helplineType;
    }

    public void setHelplineType(HelplineType helplineType) {
        this.helplineType = helplineType;
    }

    @Override
    public void login(Context ctx) {
        if (id.length() == 0 || password.length() == 0) {
            Toast.makeText(ctx, "Error, fields cannot be empty", Toast.LENGTH_SHORT).show();
        }

    }
}
