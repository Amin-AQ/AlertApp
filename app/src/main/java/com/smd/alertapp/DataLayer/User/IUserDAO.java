package com.smd.alertapp.DataLayer.User;

import com.smd.alertapp.Entities.User.User;
import com.smd.alertapp.Entities.User.UserType;

import java.util.Hashtable;

public interface IUserDAO {
    public void save(User user);
    public Hashtable<String,String> getById(String id, UserType userType);
}
