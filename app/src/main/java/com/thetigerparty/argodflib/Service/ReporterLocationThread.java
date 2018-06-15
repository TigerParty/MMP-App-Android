package com.thetigerparty.argodflib.Service;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.thetigerparty.argodflib.BuildConfig;
import com.thetigerparty.argodflib.HelperClass.Config;
import com.thetigerparty.argodflib.HelperClass.HttpProcess;
import com.thetigerparty.argodflib.Object.UserObject;

import org.json.JSONObject;

/**
 * Created by fredtsao on 12/21/16.
 */

public class ReporterLocationThread extends Thread implements LocationListener, Runnable {
    private LocationManager locationManager;
    private BackgroundRunnerService mainService;
    private UserObject userObject;
    private Thread postThread;

    private Double lat;
    private Double lng;

    private String ANDROID_ID = "";
    private String SENT_LOCATION_API = "";

    public ReporterLocationThread (BackgroundRunnerService service) {
        this.setDaemon(true);
        this.mainService = service;
        this.setResourceValue();
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void enableGps() {
        try {
            locationManager = (LocationManager) mainService.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
            }
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    public void disableGps() {
        try {
            locationManager.removeUpdates(this);
            Logger.d("GPS stop...");
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    public void run() {
        super.run();
        while (true) {
            try {
                if (Config.checkInternet(mainService)) {
                    postLocation();
                    Thread.sleep(10000);
                }
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
    }

    private void setResourceValue() {
        this.ANDROID_ID = mainService.getDeviceId();
        this.SENT_LOCATION_API = mainService.getLocationApiUrl();
        this.userObject = mainService.getCurrentUser();
    }

    private JSONObject getLocationPackage() {
        this.userObject = mainService.getCurrentUser();
        JSONObject locationPackage = new JSONObject();
        try {
            locationPackage.put("device_id", ANDROID_ID);
            locationPackage.put("lat", lat);
            locationPackage.put("lng", lng);
            if (userObject != null) {
                locationPackage.put("created_by", userObject.getUserId());
            } else {
                locationPackage.put("created_by", JSONObject.NULL);
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return locationPackage;
    }

    private void postLocation(){
        String postPackage;
        String result;
        if (getLocationPackage().has("lat") && getLocationPackage().has("lng")) {
            postPackage = getLocationPackage().toString();
            try {
                result = HttpProcess.httpPost(SENT_LOCATION_API, postPackage);
                Log.d(this.getClass().getSimpleName(), result);
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
