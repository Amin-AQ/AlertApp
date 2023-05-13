package com.smd.alertapp.Entities.User;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.smd.alertapp.Activities.MainActivity;
import com.smd.alertapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelplineUser extends User{

    private String id;

    private HelplineType helplineType;

    public HelplineUser(String id, String username, String password, HelplineType type) {
        super(username, password, UserType.HELPLINE);
        this.id=id;
        this.helplineType=type;
    }

    public HelplineUser(){
        super("","",UserType.HELPLINE);
    }


    public HelplineUser(String username,String password, UserType userType, String id) {
        super(username,password, userType);
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

    public static void callHelpline(Activity a){
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle("Click on the helpline to call");
        builder.setCancelable(true);
        final List<HelplineType> lables = new ArrayList<>(Arrays.asList(HelplineType.values()));
        List<String> lables2 = new ArrayList<>();
        for (HelplineType type : lables)
            lables2.add(type.name()+" : "+type.getValue());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(a, R.layout.dialog_item, lables2);
        builder.setAdapter(dataAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(a,"You have selected " +lables.get(which),Toast.LENGTH_LONG).show();
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+lables.get(which).getValue()));//   tel:1122
                a.startActivity(i);

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }
}
