package com.smd.alertapp.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.smd.alertapp.Activities.MainActivity;
import com.smd.alertapp.Entities.Alert.Alert;
import com.smd.alertapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ALERT = "alert";
    TextView alertType,userId;
    ImageButton backBtn, locationBtn;
    Alert alert;

    public AlertFragment() {
        // Required empty public constructor
    }

    public static AlertFragment newInstance(Alert a) {
        AlertFragment fragment = new AlertFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ALERT,a);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            alert= (Alert) getArguments().getSerializable(ARG_ALERT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_alert, container, false);
        alertType=v.findViewById(R.id.alerttype);
        userId=v.findViewById(R.id.userid);
        locationBtn=v.findViewById(R.id.locationbtn);
        backBtn=v.findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri=alert.getLocation();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getActivity().getApplicationContext().startActivity(intent);
            }
        });
        userId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[]perms=new String[]{Manifest.permission.CALL_PHONE};
                if(!hasPermissions(getActivity(),perms)){
                    ActivityCompat.requestPermissions(getActivity(),perms,1);
                }
                else
                    callUser();
            }
        });
        return v;
    }

    void callUser(){
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+alert.getUserId()));
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1&&grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            callUser();
        }
        else{
            Toast.makeText(getContext(),"Permission denied, cannot make call.",Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}