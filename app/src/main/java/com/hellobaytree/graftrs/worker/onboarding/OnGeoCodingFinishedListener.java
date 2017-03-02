/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package com.hellobaytree.graftrs.worker.onboarding;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

public interface OnGeoCodingFinishedListener {
    void onReverseGeoCodingFinished(Address address);
    void onGeoCodingFinished(LatLng latLng);
    void onGeoCodingFailed();
}
