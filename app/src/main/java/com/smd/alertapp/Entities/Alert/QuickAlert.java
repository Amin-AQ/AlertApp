package com.smd.alertapp.Entities.Alert;

import android.telephony.SmsManager;

import com.smd.alertapp.Entities.User.HelplineType;

import java.util.ArrayList;
import java.util.Set;

public class QuickAlert extends Alert {
    public QuickAlert(String alertId, String userId, HelplineType helplineType, String location, Set<String> contactList) {
        super(alertId, AlertType.QUICK_ALERT, userId, helplineType, location, contactList);
    }

    @Override
    public void send(boolean alertContacts,boolean alertHelplines,String username) {
        String message = "This message is from EmergencyAlert app.\n"+username+" is in danger and needs your help!\nLocation:\n"+location;
        if(alertContacts) {
            SmsManager smsManager = SmsManager.getDefault();
            for (String contact : contactList) {
                    smsManager.sendTextMessage(contact, null, message, null, null);
            }
        }

    }
}