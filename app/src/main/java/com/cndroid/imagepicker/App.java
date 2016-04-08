package com.cndroid.imagepicker;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by jinbangzhu on 4/7/16.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);

    }
}
