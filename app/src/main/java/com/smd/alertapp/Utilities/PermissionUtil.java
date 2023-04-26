package com.smd.alertapp.Utilities;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class PermissionUtil {

    private static final String TAG = "PermissionUtil";

    public static void requestReadContactsPermission(Activity activity) {
        // Check if we already have the permission
        if (activity.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            // We have the permission, do what we need to do.
            // For example, get the list of contacts.
        } else {
            // We don't have the permission, so ask the user for it.
            activity.requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, 1);
        }
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {

        // Check if the permission we requested was granted.
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // The permission was granted, do what we need to do.
            // For example, get the list of contacts.
        } else {
            // The permission was denied, show a message to the user.
            Toast.makeText(activity, "Permission denied, you won't be able to use this feature", Toast.LENGTH_LONG).show();
        }
    }
}
