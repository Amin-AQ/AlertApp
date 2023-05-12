package com.smd.alertapp.Entities.Alert;

import android.content.Context;
import android.content.DialogInterface;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.smd.alertapp.DataLayer.Alert.IAlertDAO;
import com.smd.alertapp.Entities.User.HelplineType;
import com.smd.alertapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CustomAlert extends Alert{

    String message;
    public CustomAlert(String alertId, String userId, HelplineType helplineType, String location, List<String> contactList, String message) {
        super(alertId, AlertType.CUSTOM_ALERT, userId, helplineType, location, contactList);
        this.message=message;
    }

    public CustomAlert() {
    }

    @Override
    public void send(Context ctx, boolean alertContacts, boolean alertHelplines, String username, IAlertDAO dao) {
        message="Custom Alert from "+username+":\n"+message;
        ArrayList<String>multipartMessage=new ArrayList<String>();
        if(alertContacts) {
            SmsManager smsManager = SmsManager.getDefault();
            if(contactList.size()==0)
                Toast.makeText(ctx,"No contact selected! Please go to 'Edit Contacts' and select contacts to alert.",Toast.LENGTH_LONG).show();
            for (String contact : contactList) {
                multipartMessage=smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(contact,null,multipartMessage,null,null);
                smsManager.sendTextMessage(contact, null, "Location:\n"+location, null, null);
                Toast.makeText(ctx,"Alert SMS sent to "+ contact,Toast.LENGTH_SHORT).show();
            }
            if(!alertHelplines)
                helplineType=null;
        }
        if(alertHelplines){
            showDialog(ctx,dao);
            if(!alertContacts)
                contactList=null;
        }
    }

    private void alertHelpline(IAlertDAO dao){
        Log.d("Alert","alert helpline function called");
        dao.save(CustomAlert.this);
    }
    private void showDialog(Context ctx, IAlertDAO dao)
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
                alertHelpline(dao);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
