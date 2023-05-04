package com.smd.alertapp.Entities.Alert;

import com.smd.alertapp.Entities.User.HelplineType;

import java.util.ArrayList;

public abstract class Alert {
    protected String alertId;
    protected AlertType alertType;
    protected String userId;
    protected HelplineType helplineType;
    protected String location;
    protected ArrayList<String>contactList;

    public Alert(String alertId, AlertType alertType, String userId, HelplineType helplineType, String location, ArrayList<String> contactList) {
        this.alertId = alertId;
        this.alertType = alertType;
        this.userId = userId;
        this.helplineType = helplineType;
        this.location = location;
        this.contactList = contactList;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getUserId() {
        return userId;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public HelplineType getHelplineType() {
        return helplineType;
    }

    public void setHelplineType(HelplineType helplineType) {
        this.helplineType = helplineType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getContactList() {
        return contactList;
    }

    public void setContactList(ArrayList<String> contactList) {
        this.contactList = contactList;
    }

    public abstract void send();
    protected void setLocation(){}
}
