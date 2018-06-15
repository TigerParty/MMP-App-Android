package com.thetigerparty.argodflib.HelperClass;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.thetigerparty.argodflib.BuildConfig;

/**
 * Created by ttpttp on 2015/8/4.
 */
public class Config {
    public static boolean checkInternet(Context context) {
        boolean result = false;

        ConnectivityManager CManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = CManager.getActiveNetworkInfo();
        if(info == null || !info.isConnected()) {
            result = false;
        }
        else {
            if(info.isAvailable()){
                result = true;
            }
        }

        return result;
    }

    public static boolean checkGPS(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void hideKeyboard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) context).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
