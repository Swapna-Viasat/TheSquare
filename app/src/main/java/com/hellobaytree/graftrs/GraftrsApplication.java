package com.hellobaytree.graftrs;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.hellobaytree.graftrs.shared.data.persistence.SharedPreferencesManager;

import io.fabric.sdk.android.Fabric;

public class GraftrsApplication extends MultiDexApplication {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        SharedPreferencesManager.saveDeviceId(this);

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
    }

    public static Context getAppContext() {
        return context;
    }
}
