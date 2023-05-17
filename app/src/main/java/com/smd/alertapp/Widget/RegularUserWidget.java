package com.smd.alertapp.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.smd.alertapp.Activities.MainActivity;
import com.smd.alertapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class RegularUserWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.d("1", "In widget");
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.regular_user_widget);
        views.setTextViewText(R.id.previewText, widgetText);

        // Create an explicit Intent to launch MainActivity
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Create unique request codes for each button
        int quickAlertRequestCode = 0;
        int callHelplineRequestCode = 1;
        int editContactsRequestCode = 2;

        // Add extra data with unique keys to the intent to differentiate actions
        mainActivityIntent.setAction("QUICK_ALERT");
        PendingIntent quickAlertPendingIntent = PendingIntent.getActivity(context, quickAlertRequestCode, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mainActivityIntent.setAction("CALL_HELPLINE");
        PendingIntent callHelplinePendingIntent = PendingIntent.getActivity(context, callHelplineRequestCode, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mainActivityIntent.setAction("EDIT_CONTACTS");
        PendingIntent editContactsPendingIntent = PendingIntent.getActivity(context, editContactsRequestCode, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.quickalertbtn, quickAlertPendingIntent);
        views.setOnClickPendingIntent(R.id.callbtn, callHelplinePendingIntent);
        views.setOnClickPendingIntent(R.id.editcontactsbtn, editContactsPendingIntent);

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
