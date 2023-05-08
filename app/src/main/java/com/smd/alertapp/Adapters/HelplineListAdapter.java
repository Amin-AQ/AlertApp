package com.smd.alertapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smd.alertapp.Entities.User.HelplineType;
import com.smd.alertapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class HelplineListAdapter extends RecyclerView.Adapter<HelplineListAdapter.HelplineViewHolder> {
    Context context;
    int prevCheck;
    private ArrayList<Map<String,String>> helplineList;
    public HelplineListAdapter(Context ctx){
        context=ctx;
        prevCheck=-1;
        helplineList=new ArrayList<Map<String,String>>();
        for(HelplineType type:HelplineType.values()){
            Map<String,String>m=new HashMap<String, String>();
            m.put("name",type.name());
            m.put("number",type.getValue());
            helplineList.add(m);
        }
    }

    @NonNull
    @Override
    public HelplineListAdapter.HelplineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.helpline_type_layout, parent, false);
        return new HelplineListAdapter.HelplineViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull HelplineListAdapter.HelplineViewHolder holder, int position) {
        Map<String, String> contact = helplineList.get(position);
        holder.name.setText(contact.get("name"));
        holder.number.setText(contact.get("number"));
        holder.checkBox.setChecked(position == prevCheck);
    }

    @Override
    public int getItemCount() {
        return helplineList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class HelplineViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;
        TextView name, number;
        public HelplineViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox=itemView.findViewById(R.id.contact_checkbox);
            name=itemView.findViewById(R.id.helpline_name);
            number=itemView.findViewById(R.id.contact_number);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Update the selected position
                        int previousSelectedPosition = prevCheck;
                        prevCheck = position;

                        // Notify the adapter that the item at the previous selected position
                        // and the current selected position have changed
                        notifyItemChanged(previousSelectedPosition);
                        notifyItemChanged(prevCheck);
                    }
                }
            });

        }
    }

}
