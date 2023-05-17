package com.smd.alertapp.Entities.Alert;

import android.content.Context;

import com.smd.alertapp.DataLayer.Alert.AlertSentCallback;
import com.smd.alertapp.DataLayer.Alert.IAlertDAO;
import com.smd.alertapp.Entities.User.HelplineType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public abstract class Alert{
    protected String alertId;
    protected AlertType alertType;
    protected String userId;
    protected HelplineType helplineType;
    protected String location;
    protected List<String> contactList;
    protected Date dateTimeStamp;

    public Alert(String alertId, AlertType alertType, String userId, HelplineType helplineType, String location, List<String> contactList) {
        this.alertId = alertId;
        this.alertType = alertType;
        this.userId = userId;
        this.helplineType = helplineType;
        this.location = location;
        this.contactList = contactList;
        LocalDateTime localDateTime = LocalDateTime.now();
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        this.dateTimeStamp = Date.from(instant);
    }

    public Alert() {

    }

    public Date getDateTimeStamp() {
        return dateTimeStamp;
    }

    public void setDateTimeStamp(Date dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public List<String> getContactList() {
        return contactList;
    }

    public void setContactList(List<String> contactList) {
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

   /* public Set<String> getContactList() {
        return contactList;
    }

    public void setContactList(Set<String> contactList) {
        this.contactList = contactList;
    }
*/
    public abstract void send(Context ctx, boolean alertContact, boolean alertHelpline, String username, IAlertDAO dao, AlertSentCallback callback);
    protected void setLocation(){}
}
