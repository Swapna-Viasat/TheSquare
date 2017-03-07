/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package com.hellobaytree.graftrs.worker.onboarding;

import android.location.Address;

public interface OnGeoCodingFinishedListener {
    void onReverseGeoCodingFinished(Address address);
}
