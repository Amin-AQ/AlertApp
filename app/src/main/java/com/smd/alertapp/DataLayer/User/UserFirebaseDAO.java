package com.smd.alertapp.DataLayer.User;

import com.smd.alertapp.Entities.User.UserType;

import java.util.Hashtable;

public class UserFirebaseDAO implements IUserDAO{

    @Override
    public void save(Hashtable<String, String> user) {
        
    }

    @Override
    public Hashtable<String, String> getById(String id, UserType userType) {
        return null;
    }
}
