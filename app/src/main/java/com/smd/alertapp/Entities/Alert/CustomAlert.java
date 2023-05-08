package com.smd.alertapp.Entities.Alert;

import com.smd.alertapp.Entities.User.HelplineType;

import java.util.Set;

public class CustomAlert extends Alert{

    public CustomAlert(String alertId, AlertType alertType, String userId, HelplineType helplineType, String location, Set<String> contactList) {
        super(alertId, alertType, userId, helplineType, location, contactList);
    }

    @Override
    public void send(boolean alertContacts, boolean alertHelplines,String username) {

    }
}
