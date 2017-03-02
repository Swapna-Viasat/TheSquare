package com.hellobaytree.graftrs.worker.onboarding;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

public class GeocodeRunnable implements Runnable {

    private Context context;
    private OnGeoCodingFinishedListener listener;
    private String targetAddress;
    private Dialog dialog;

    public GeocodeRunnable(Context context, String targetAddress, OnGeoCodingFinishedListener listener, Dialog dialog) {
        this.context = context;
        this.listener = listener;
        this.targetAddress = targetAddress;
        this.dialog = dialog;
    }

    @Override
    public void run() {
        if (context == null) return;

        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;
        Address address;

        try {
            addresses = geocoder.getFromLocationName(targetAddress, 1);
            if (listener != null) listener.onGeoCodingFailed();
        } catch (IOException e) {
            if (listener != null) listener.onGeoCodingFailed();
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            address = addresses.get(0);
            if (listener != null)
                listener.onGeoCodingFinished(new LatLng(address.getLatitude(), address.getLongitude()));
        }
    }
}
