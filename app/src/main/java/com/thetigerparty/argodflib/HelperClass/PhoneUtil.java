package com.thetigerparty.argodflib.HelperClass;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.thetigerparty.argodflib.BuildConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by louis on 11/10/2017.
 */

public class PhoneUtil {
    public static final String KEY_OS = "os";
    public static final String KEY_DEVICE_ID = "device_id";
    public static final String KEY_VERSION_MARK = "version_mark";
    public static final String KEY_COMMIT_NUMBER = "commit_number";

    public static Map<String, String> getDeviceInfo(final Context context) {
        return new HashMap<String, String>(){{
            put(KEY_OS, "Android " + Build.VERSION.RELEASE);
            put(KEY_DEVICE_ID, Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            put(KEY_VERSION_MARK, BuildConfig.VERSION_NAME);
            put(KEY_COMMIT_NUMBER, BuildConfig.GIT_COMMIT_NUMBER);
        }};
    }
}
