package com.smd.alertapp.Utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsUtil {

    public static List<Map<String, String>> getContacts(Context context) {
        List<Map<String, String>> contacts = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME),
                            numIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    if (nameIndex >= 0 && numIndex >= 0) {
                        String contactName = cursor.getString(nameIndex);
                        String contactNumber = cursor.getString(numIndex);
                        contactNumber = contactNumber.replaceAll("[^\\d+]", ""); // Remove all non-digits and non-+ characters
                        if (contactNumber.startsWith("0")) {
                            contactNumber = "+92" + contactNumber.substring(1); // Replace leading 0 with +92 for Pakistan numbers
                        }
                        if (contactNumber.length() == 13) { // Check if the number is a Pakistan number
                            Map<String, String> contact = new HashMap<>();
                            contact.put("name", contactName);
                            contact.put("number", contactNumber);
                            contacts.add(contact);
                        }

                    }
                }
            } finally {
                cursor.close();
            }
        }

        return contacts;
    }


}
