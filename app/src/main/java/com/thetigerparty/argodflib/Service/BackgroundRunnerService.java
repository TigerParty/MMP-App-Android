package com.thetigerparty.argodflib.Service;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.thetigerparty.argodflib.BuildConfig;
import com.thetigerparty.argodflib.Object.UserObject;
import com.thetigerparty.argodflib.R;

/**
 * Created by fredtsao on 12/20/16.
 */

public class BackgroundRunnerService extends Service {
    BackgroundRunnerServiceBinder binder;
    ReporterLocationThread reporterLocation;
    AutoDataSyncThread autoDataSyncThread;

    UserObject user_obj = null;

    String ANDROID_ID;
    String SENT_LOCATION_API;

    // Service LifeCycle
    public void onCreate() {
        super.onCreate();
        this.setResourceValue();

        if (BuildConfig.LOCATION_SERVICE_ENABLE) {
            Logger.d("Enabled ReporterLocation");
            this.reporterLocation = new ReporterLocationThread(this);
            this.reporterLocation.enableGps();
            this.reporterLocation.start();
        }

        if (BuildConfig.AUTO_DATA_SYNC_ENABLE) {
            Logger.d("Enabled AutoDataSync");
            this.autoDataSyncThread = new AutoDataSyncThread(this);
            this.autoDataSyncThread.start();
        }

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.setIntentValue(intent);

        return START_REDELIVER_INTENT;
    }

    public void onDestroy() {
        super.onDestroy();

        if (BuildConfig.LOCATION_SERVICE_ENABLE) {
            this.reporterLocation.disableGps();
            this.reporterLocation = null;
        }

        if (BuildConfig.AUTO_DATA_SYNC_ENABLE) {
            this.autoDataSyncThread = null;
        }

        Runtime.getRuntime().gc();
    }

    // Service bind & unbind
    @Override
    public IBinder onBind(Intent intent) {
        binder = new BackgroundRunnerServiceBinder();
        return binder;
    }

    public boolean onUnbind(Intent intent) {
        this.destroyCurrentUser();
        return super.onUnbind(intent);
    }

    public class BackgroundRunnerServiceBinder extends Binder {
        public  BackgroundRunnerService getService() {
            return BackgroundRunnerService.this;
        }
    }

    // Service public method
    public void setCurrentUser(UserObject user_obj) {
        if (user_obj != null) {
            this.user_obj = user_obj;
        }
    }

    public void destroyCurrentUser() {
        this.user_obj = null;
    }

    public UserObject getCurrentUser() {
        return user_obj;
    }

    public String getDeviceId() {
        return ANDROID_ID;
    }

    public String getLocationApiUrl() {
        return SENT_LOCATION_API;
    }

    // Service private method
    private void setResourceValue() {
        ANDROID_ID = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        SENT_LOCATION_API = BuildConfig.REPORTER_LOCATION_API;
    }

    private void setIntentValue(Intent intent) {
        if (intent.getExtras() != null) {
            user_obj = (UserObject) intent.getExtras().getSerializable("obj_user");
        }
    }
}
