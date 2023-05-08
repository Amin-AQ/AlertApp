package com.smd.alertapp.Utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContactsUtil {

    public static Set<Map<String, String>> getContacts(Context context) {
        Set<Map<String, String>> contacts = new HashSet<Map<String, String>>();
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
                        if (contactNumber.length() == 13 && contactNumber.startsWith("+92")) { // Check if the number is a Pakistan number
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

        // Convert the set to a list
        List<Map<String, String>> contactsList = new ArrayList<>(contacts);

        // Sort the list based on the key of the Map
        Collections.sort(contactsList, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> map1, Map<String, String> map2) {
                return map1.get("name").compareTo(map2.get("name"));
            }
        });

        // Convert the list back to a set
        Set<Map<String, String>> sortedContacts = new LinkedHashSet<>(contactsList);

        return sortedContacts;
    }


}
