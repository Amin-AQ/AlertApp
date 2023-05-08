package com.smd.alertapp.Fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smd.alertapp.Adapters.HelplineListAdapter;
import com.smd.alertapp.R;


public class AlertHelplineFragment extends Fragment {

    private RecyclerView recyclerView;
    private OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_alert_helpline, container, false);
        recyclerView = view.findViewById(R.id.helpline_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        if (drawable != null) {
            itemDecoration.setDrawable(drawable);
        }
        recyclerView.addItemDecoration(itemDecoration);
        HelplineListAdapter adapter=new HelplineListAdapter(getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }

    public interface OnFragmentInteractionListener {
        void onDataPassed(String data);
    }

}