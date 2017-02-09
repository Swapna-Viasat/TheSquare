package com.hellobaytree.graftrs.worker.jobdetails;

import android.content.Context;

import com.hellobaytree.graftrs.worker.jobmatches.model.Application;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 FusionWorks. All rights reserved.
 */

interface JobDetailsContract {
    void onJobFetched();
    void onJobApply(Application application);
    void onBookingCanceled();
    Context getContext();
}
