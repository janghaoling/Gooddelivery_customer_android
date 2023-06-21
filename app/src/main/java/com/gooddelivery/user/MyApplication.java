package com.gooddelivery.user;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import androidx.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.google.android.libraries.places.api.Places;
import com.gooddelivery.user.utils.LocaleUtils;

/**
 * Created by santhosh@appoets.com on 28-08-2017.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
      //  Fabric.with(this, new Crashlytics());
        Stetho.initializeWithDefaults(this);

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/Nunito-Regular.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        );

        MultiDex.install(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAj-4ntJ-31ANsBQ93qSIb42Veuu_txy6c");
        }

    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void attachBaseContext(Context base) {
        //super.attachBaseContext(base);
        super.attachBaseContext(LocaleUtils.onAttach(base, "es"));
        MultiDex.install(base);
    }
}
