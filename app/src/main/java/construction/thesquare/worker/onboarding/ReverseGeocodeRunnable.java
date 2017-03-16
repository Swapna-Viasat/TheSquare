/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package construction.thesquare.worker.onboarding;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class ReverseGeocodeRunnable implements Runnable {

    private Context context;
    private OnGeoCodingFinishedListener listener;
    private LatLng target;

    public ReverseGeocodeRunnable(Context context, LatLng target, OnGeoCodingFinishedListener listener) {
        this.context = context;
        this.listener = listener;
        this.target = target;
    }

    @Override
    public void run() {
        if (context == null || target == null) return;

        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;
        Address address = null;

        try {
            addresses = geocoder.getFromLocation(target.latitude, target.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            address = addresses.get(0);
        }

        if (listener != null)
            listener.onReverseGeoCodingFinished(address);
    }
}
