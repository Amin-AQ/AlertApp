package com.smd.alertapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smd.alertapp.Adapters.ContactsAdapter;
import com.smd.alertapp.R;
import com.smd.alertapp.Utilities.ContactsUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A fragment representing a list of Items.
 */
public class ContactsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Map<String, String>> contacts;
    private ContactsAdapter adapter;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts_list, container, false);

        recyclerView = view.findViewById(R.id.contacts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        sharedPreferences = getActivity().getSharedPreferences("ContactPrefs", Context.MODE_PRIVATE);

        // Get the saved selected contacts (if any)
        Set<String> mSelectedContacts = sharedPreferences.getStringSet("selectedContacts", new HashSet<String>());

        Log.d("Here0",mSelectedContacts.toString());
        contacts = ContactsUtil.getContacts(getContext());
        adapter = new ContactsAdapter(contacts, mSelectedContacts);
        recyclerView.setAdapter(adapter);


        Button doneButton = view.findViewById(R.id.contacts_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save selected contacts as shared preferences
                saveSelectedContacts();

                // Close the fragment
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void saveSelectedContacts() {
        // Get the saved selected contacts (if any)
        Set<String> mSelectedContacts = new HashSet<String>();
        Log.d("Here1",mSelectedContacts.toString());
        // Get the selected contacts from the adapter
        ArrayList<Boolean> contactsCheck = adapter.getCheckedContacts();
        List<Map<String, String>> contacts = adapter.getContacts();
        for (int i = 0; i < contacts.size(); i++) {
            Map<String,String> contact = contacts.get(i);
            Boolean isChecked=contactsCheck.get(i);
            if (isChecked) {
                // Check if the contact is not already saved
                    // Add the contact to the saved set
                    mSelectedContacts.add(contact.get("number"));
            }
        }
        Log.d("Here1",mSelectedContacts.toString());
        // Save the selected contacts to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        editor.putStringSet("selectedContacts", mSelectedContacts);
        Log.d("Hare", String.valueOf(editor.commit()));
    }
}
