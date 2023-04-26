package com.smd.alertapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        contacts = ContactsUtil.getContacts(getContext());
        adapter = new ContactsAdapter(contacts);

        recyclerView.setAdapter(adapter);

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

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
        // Get the SharedPreferences instance
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Get the saved selected contacts (if any)
        Set<String> mSelectedContacts = sharedPreferences.getStringSet("selectedContacts", new HashSet<String>());

        // Get the selected contacts from the adapter
        ArrayList<Boolean> contactsCheck = adapter.getCheckedContacts();
        List<Map<String, String>> contacts = adapter.getContacts();
        for (int i = 0; i < contacts.size(); i++) {
            Map<String,String> contact = contacts.get(i);
            Boolean isChecked=contactsCheck.get(i);
            if (isChecked) {
                // Check if the contact is not already saved
                if (!mSelectedContacts.contains(contact.get("number"))) {
                    // Add the contact to the saved set
                    mSelectedContacts.add(contact.get("number"));
                }
            } else {
                // Remove the contact from the saved set (if present)
                mSelectedContacts.remove(contact.get("number"));
            }
        }

        // Save the selected contacts to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("selectedContacts", mSelectedContacts);
        editor.apply();
    }
}
