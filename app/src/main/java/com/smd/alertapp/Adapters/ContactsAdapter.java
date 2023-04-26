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
import java.util.List;
import java.util.Map;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<Map<String, String>> contacts;
    private ArrayList<Boolean> checkedContacts;

    public ContactsAdapter(List<Map<String, String>> contacts) {
        this.contacts = contacts;
        this.checkedContacts = new ArrayList<Boolean>(contacts.size());
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
