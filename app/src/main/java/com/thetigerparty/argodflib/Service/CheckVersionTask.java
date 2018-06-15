package com.thetigerparty.argodflib.Service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.thetigerparty.argodflib.BuildConfig;
import com.thetigerparty.argodflib.HelperClass.HttpProcess;
import com.thetigerparty.argodflib.HelperClass.PhoneUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by louis on 11/10/2017.
 */

public class CheckVersionTask extends AsyncTask<String, Void, String> {
    public final String TAG = this.getClass().getSimpleName();

    private CheckVersionTaskListener listener;
    private Context context;

    public interface CheckVersionTaskListener {
        void onSuccess(boolean available, String link);
        void onFailure();
    }

    public CheckVersionTask(Context c, CheckVersionTaskListener listener) {
        this.context = c;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... strings) {
        JSONObject obj = new JSONObject();
        try {
            for (Map.Entry entry : PhoneUtil.getDeviceInfo(context).entrySet()){
                obj.put(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String response = HttpProcess.post(BuildConfig.CURRENT_VERSION_API, obj);

        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        Log.d(TAG, "onPostExecute: " + response);

        if (listener != null) {
            try {
                JSONObject obj = new JSONObject(response);
                boolean available = obj.getBoolean("update_available");
                String link = obj.getString("link");

                listener.onSuccess(available, link);
            } catch (JSONException e) {
                Log.e(TAG, "onPostExecute: ", e);
                e.printStackTrace();

                listener.onFailure();
            }
        }
    }
}
