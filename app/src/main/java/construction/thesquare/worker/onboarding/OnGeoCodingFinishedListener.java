/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

package construction.thesquare.worker.onboarding;

import android.location.Address;

public interface OnGeoCodingFinishedListener {
    void onReverseGeoCodingFinished(Address address);
}
