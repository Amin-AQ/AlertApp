package com.smd.alertapp.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.smd.alertapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class RegularUserWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.d("1","In widget");
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.regular_user_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        // creating intents for all 3 button
        Intent quickAlertIntent = new Intent(context,WidgetReceiver.class);
        Intent callHelplineIntent = new Intent(context,WidgetReceiver.class);
        Intent editContactsIntent = new Intent(context,WidgetReceiver.class);
        // setting actions to call in broadcast receiver
        quickAlertIntent.setAction("QUICK_ALERT");
        editContactsIntent.setAction("EDIT_CONTACTS");
        callHelplineIntent.setAction("CALL_HELPLINE");

        PendingIntent quickAlertPendingIntent=PendingIntent.getBroadcast(context,0,quickAlertIntent,0),
                      callHelplinePendingIntent=PendingIntent.getBroadcast(context,1,callHelplineIntent,0),
                      editContactsPendingIntent=PendingIntent.getBroadcast(context,2,editContactsIntent,0);
        views.setOnClickPendingIntent(R.id.quickalertbtn,quickAlertPendingIntent);
        views.setOnClickPendingIntent(R.id.callbtn,callHelplinePendingIntent);
        views.setOnClickPendingIntent(R.id.editcontactsbtn,editContactsPendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}