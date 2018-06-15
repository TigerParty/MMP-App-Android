package com.thetigerparty.argodflib.Service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by fredtsao on 12/21/16.
 */

public class BackgroundRunnerProvider extends Observable implements Serializable {

    public void add_observer(Observer observer) {
        this.addObserver(observer);
        Logger.d("Add observer: " + observer.getClass().getSimpleName());
    }

    public BackgroundRunnerServiceConnection getBackgroundRunnerServiceConnection() {
        return new BackgroundRunnerServiceConnection();
    }

    public class BackgroundRunnerServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundRunnerService mainService = ((BackgroundRunnerService.BackgroundRunnerServiceBinder)service).getService();
            Logger.d("onServiceConnected");
            setChanged();
            notifyObservers(mainService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d("onServiceDisconnected");
        }
    }
}
