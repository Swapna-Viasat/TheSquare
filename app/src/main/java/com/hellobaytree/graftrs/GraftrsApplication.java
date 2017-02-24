package com.hellobaytree.graftrs;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;

import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;

public class GraftrsApplication extends MultiDexApplication {

    private static Context context;

    private static Tracker mTracker;
    private static GoogleAnalytics googleAnalytics;



    public void onCreate() {
        super.onCreate();
        googleAnalytics = GoogleAnalytics.getInstance(this);
        mTracker = getDefaultTracker();
        mTracker.enableAutoActivityTracking(true);
        context = getApplicationContext();

        SharedPreferencesManager.saveDeviceId(this);

        Branch.getAutoInstance(this);

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            mTracker = googleAnalytics.newTracker(R.xml.global_tracker_settings);
        }
        return mTracker;
    }

    public static Context getAppContext() {
        return context;
    }
}
