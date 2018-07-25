package com.yu.mae.bundles.scanner.demo;

import android.app.Application;

/**
 * Created by liyu20 on 2017/11/23.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });
    }
}
