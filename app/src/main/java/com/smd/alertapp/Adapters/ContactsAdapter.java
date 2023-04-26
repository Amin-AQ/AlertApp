package com.smd.alertapp.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.smd.alertapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<Map<String, String>> contacts;
    private ArrayList<Boolean> checkedContacts;

    public ContactsAdapter(List<Map<String, String>> contacts, Set<String> prevCheckedContacts) {
        this.contacts = contacts;
        this.checkedContacts = new ArrayList<>(contacts.size());

        // Initialize the checkedContacts list with false values for all contacts
        for (int i = 0; i < contacts.size(); i++) {
            checkedContacts.add(false);
        }

        // Update the checkedContacts list for previously checked contacts
        for (int i = 0; i < contacts.size(); i++) {
            Map<String, String> contact = contacts.get(i);
            if (prevCheckedContacts.contains(contact.get("number"))) {
                checkedContacts.set(i, true);
            }
        }

       /* // Sort the contacts so that the already checked contacts appear first
        Collections.sort(contacts, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> c1, Map<String, String> c2) {
                int c1Index = contacts.indexOf(c1);
                int c2Index = contacts.indexOf(c2);
                boolean c1Checked = checkedContacts.get(c1Index);
                boolean c2Checked = checkedContacts.get(c2Index);

                if (c1Checked && !c2Checked) {
                    return -1;
                } else if (!c1Checked && c2Checked) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });*/
    }


    public List<Map<String, String>> getContacts() {
        return contacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contacts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> contact = contacts.get(position);
        holder.contactNameTextView.setText(contact.get("name"));
        holder.contactNumberTextView.setText(contact.get("number"));
        holder.checkBox.setChecked(checkedContacts.get(position));
        holder.checkBox.setOnClickListener(v -> checkedContacts.set(position, holder.checkBox.isChecked()));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<Boolean> getCheckedContacts() {
        return checkedContacts;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView contactNameTextView, contactNumberTextView;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactNameTextView = itemView.findViewById(R.id.contact_name);
            contactNumberTextView = itemView.findViewById(R.id.contact_number);
            checkBox = itemView.findViewById(R.id.contact_checkbox);
        }
    }
}
