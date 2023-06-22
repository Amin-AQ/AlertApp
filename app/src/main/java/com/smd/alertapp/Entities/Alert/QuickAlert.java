package com.smd.alertapp.Entities.Alert;

import android.content.Context;
import android.content.DialogInterface;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.smd.alertapp.DataLayer.Alert.AlertSentCallback;
import com.smd.alertapp.DataLayer.Alert.IAlertDAO;
import com.smd.alertapp.Entities.User.HelplineType;
import com.smd.alertapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuickAlert extends Alert {
    public QuickAlert(String alertId, String userId, HelplineType helplineType, String location, List<String> contactList) {
        super(alertId, AlertType.QUICK_ALERT, userId, helplineType, location, contactList);
    }

    public QuickAlert() {
    }

    @Override
    public void send(Context ctx, boolean alertContacts, boolean alertHelplines, String username, IAlertDAO dao, AlertSentCallback callback) {
        String message = "This message is from EmergencyAlert app.\n"+username+" needs your assistance!\nLocation:\n"+location;
        if(alertContacts) {
            SmsManager smsManager = SmsManager.getDefault();
            if(contactList.size()==0)
                Toast.makeText(ctx,"No contact selected! Please go to 'Edit Contacts' and select contacts to alert.",Toast.LENGTH_LONG).show();
            for (String contact : contactList) {
                    smsManager.sendTextMessage(contact, null, message, null, null);
                    Toast.makeText(ctx,"Alert SMS sent to "+ contact,Toast.LENGTH_SHORT).show();
            }
            if(!alertHelplines) {
                helplineType = null;
                dao.save(QuickAlert.this,callback);
            }
        }
        if(alertHelplines){
            showDialog(ctx,dao,callback);
            if(!alertContacts)
                contactList=null;
        }

    }

    private void alertHelpline(IAlertDAO dao, AlertSentCallback callback){
        Log.d("Alert","alert helpline function called");
        dao.save(QuickAlert.this,callback);
    }
    private void showDialog(Context ctx, IAlertDAO dao, AlertSentCallback callback)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Click on the helpline to alert");
        final List<HelplineType> lables = new ArrayList<>(Arrays.asList(HelplineType.values()));
        List<String> lables2 = new ArrayList<>();
        for (HelplineType type : lables)
            lables2.add(type.name()+" : "+type.getValue());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx, R.layout.dialog_item, lables2);
        builder.setAdapter(dataAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ctx,"You have selected " +lables.get(which),Toast.LENGTH_LONG).show();
                helplineType=lables.get(which);
                alertHelpline(dao,callback);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}