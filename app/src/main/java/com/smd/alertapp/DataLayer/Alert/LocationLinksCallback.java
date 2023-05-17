package com.smd.alertapp.DataLayer.Alert;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface LocationLinksCallback {
    void onLocationLinksFetched(ArrayList<LatLng> locationLinks);
}
