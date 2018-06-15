package com.thetigerparty.argodflib;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by louis on 06/11/2017.
 */

public class ArgoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ActiveAndroid.initialize(this);
    }
}
