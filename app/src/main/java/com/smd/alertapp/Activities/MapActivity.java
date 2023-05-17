package com.smd.alertapp.Activities;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smd.alertapp.DataLayer.Alert.AlertFirebaseDAO;
import com.smd.alertapp.DataLayer.Alert.IAlertDAO;
import com.smd.alertapp.DataLayer.Alert.LocationLinksCallback;
import com.smd.alertapp.R;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    IAlertDAO fDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        fDao=new AlertFirebaseDAO(MapActivity.this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        fDao.fetchLocationLinks(new LocationLinksCallback() {
            @Override
            public void onLocationLinksFetched(ArrayList<LatLng> locationLinks) {
                if(locationLinks!=null) {
                    for(LatLng coord : locationLinks){
                        googleMap.addMarker(new MarkerOptions()
                                .position(coord));
                    }
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    for (LatLng latLng : locationLinks) {
                        boundsBuilder.include(latLng);
                    }
                    LatLngBounds bounds = boundsBuilder.build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                    googleMap.animateCamera(cameraUpdate);
                }
            }
        });

    }
}
