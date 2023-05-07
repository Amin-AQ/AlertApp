package com.smd.alertapp.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.smd.alertapp.Activities.LoginActivity;
import com.smd.alertapp.Entities.User.UserType;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Context _context;

    int Private_mode=0;
    private static final String PREF_NAME="AndroidHivePref";
    private static final String IS_LOGIN="IsLoggedIn";
    private static final String KEY_ID ="id";
    private static final String KEY_PASSWORD="password";
    private static final String KEY_USERTYPE="usertype";
    private static final String KEY_USERNAME="username";

    public SessionManager(Context context){
        this._context=context;
        pref=_context.getSharedPreferences(PREF_NAME,Private_mode);
        editor= pref.edit();
    }

    public void createLoginSession(String id, String username, String password, UserType userType){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_ID,id);
        editor.putString(KEY_PASSWORD,password);
        editor.putString(KEY_USERNAME,username);
        editor.putString(KEY_USERTYPE,userType.toString());
        editor.commit();
    }

    public Boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN,false);
    }

    public Boolean checkLogin(){
        Boolean loggedIn=this.isLoggedIn();
        if(!loggedIn){
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
        return loggedIn;
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        user.put(KEY_ID,pref.getString(KEY_ID,null));
        user.put(KEY_USERNAME,pref.getString(KEY_USERNAME,null));
        user.put(KEY_PASSWORD,pref.getString(KEY_PASSWORD,null));
        user.put(KEY_USERTYPE,pref.getString(KEY_USERTYPE,null));
        return user;
    }

    public void LogoutUser(){
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }


}
