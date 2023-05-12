package com.smd.alertapp.Widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.smd.alertapp.Activities.MainActivity;
import com.smd.alertapp.Entities.User.HelplineType;
import com.smd.alertapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WidgetReceiver extends BroadcastReceiver {

    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("1","In Recciever");
        this.context=context;
        String action = intent.getAction();
        if(action!=null){
            switch (action){
                case "QUICK_ALERT":
                    break;
                case "CALL_HELPLINE":
                    callHelpline();
                    break;
                case "EDIT_CONTACTS":
                    break;
            }
        }
    }

    void callHelpline() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Click on the helpline to call");
        builder.setCancelable(true);
        final List<HelplineType> lables = new ArrayList<>(Arrays.asList(HelplineType.values()));
        List<String> lables2 = new ArrayList<>();
        for (HelplineType type : lables)
            lables2.add(type.name() + " : " + type.getValue());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, R.layout.dialog_item, lables2);
        builder.setAdapter(dataAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "You have selected " + lables.get(which), Toast.LENGTH_LONG).show();
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + lables.get(which).getValue()));
                context.startActivity(i);

            }
        });
    }
}
