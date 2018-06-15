package com.thetigerparty.argodflib.ViewModel.TrackerActivity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.thetigerparty.argodflib.BR;

/**
 * Created by fredtsao on 2017/9/2.
 */

public class CameraFragmentViewModel extends BaseObservable {
    private boolean isUsingCamera = true;

    public CameraFragmentViewModel(){}

    public void setUsingCamera(boolean usingCamera) {
        isUsingCamera = usingCamera;
        notifyPropertyChanged(BR.usingCamera);
    }

    @Bindable
    public boolean isUsingCamera() {
        return isUsingCamera;
    }
}
