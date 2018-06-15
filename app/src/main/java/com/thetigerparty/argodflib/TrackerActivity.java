package com.thetigerparty.argodflib;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;

import com.thetigerparty.argodflib.HelperClass.Dialog;
import com.thetigerparty.argodflib.Model.Attachment;
import com.thetigerparty.argodflib.Model.Tracker;
import com.thetigerparty.argodflib.Object.AttachmentObject;
import com.thetigerparty.argodflib.Object.TrackerObject;
import com.thetigerparty.argodflib.Object.UserObject;
import com.thetigerparty.argodflib.SubView.TrackerActivity.Fragment.CameraFragment;
import com.thetigerparty.argodflib.SubView.TrackerActivity.Fragment.CommentFragment;
import com.thetigerparty.argodflib.SubView.TrackerActivity.Fragment.LocationFragment;
import com.thetigerparty.argodflib.SubView.TrackerActivity.Fragment.TrackerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by fredtsao on 2017/8/24.
 */

public class TrackerActivity extends Activity implements LocationListener {
    private final static String TAG = "TrackerActivity";
    final static int MAPKEY_LAT = 1;
    final static int MAPKEY_LNG = 2;
    final static int MAPKEY_TIME = 3;
    final static int MAPKEY_ALT = 4;

    public final static String ATTACHMENT_DESCTIPTION_HEADER = "header";
    public final static String ATTACHMENT_DESCTIPTION_CONTENT = "content";
    public final static String ATTACHMENT_DESCTIPTION_LAT = "lat";
    public final static String ATTACHMENT_DESCTIPTION_LNG = "lng";

    public Double nowLat;
    public Double nowLng;
    int currentAttachCount = 0;

    private List<SparseArray> list_tracker_points = new ArrayList();
    private JSONArray array_tracker_points = new JSONArray();

    public TrackerFragment trackerFragment;
    public LocationFragment locationFragment;
    public CommentFragment commentFragment;
    public CameraFragment cameraFragment;
    private FragmentManager fragmentManager;

    private long nowCountdownTotalMillis;
    private CountDownTimer countDownTimer;
    public LocationManager locationManager;

    public Button btnHeaderBack;

    public TrackerObject trackerObject = new TrackerObject();
    public AttachmentObject currentAttachmentObject = new AttachmentObject();
    public UserObject userObject = new UserObject();
    public ArrayList<AttachmentObject> arrayAttachmentObject = new ArrayList();
    public JSONObject attachmentDescription = new JSONObject();

    public boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker_activity);
        this.nowCountdownTotalMillis = Long.valueOf(getResources().getInteger(R.integer.subview_gps_tracker_locations_limit) * getResources().getInteger(R.integer.subview_gps_tracker_min_milliseconds));

        initView();
        initFragment();

        initGPS();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getIntentValue();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        this.stopGPS();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (trackerFragment.isVisible()) {
            Dialog.exitTrackerActivityDialog(this);
        } else if (cameraFragment.isVisible()) {
            changeFragment(trackerFragment);
        } else if (locationFragment.isVisible()) {
            changeFragment(cameraFragment);
        } else if (commentFragment.isVisible()) {
            changeFragment(locationFragment);
        } else {
            finish();
        }
    }

    private void initFragment() {
        this.fragmentManager = getFragmentManager();

        this.trackerFragment = TrackerFragment.newInstance();
        this.commentFragment = CommentFragment.newInstance();
        this.locationFragment = LocationFragment.newInstance();
        this.cameraFragment = CameraFragment.newInstance();

        fragmentManager.beginTransaction()
                .add(R.id.tracker_activity_fragment_container, trackerFragment)
                .addToBackStack(null)
                .commit();

    }

    private void initView() {
        this.btnHeaderBack = (Button) findViewById(R.id.bt_header_back);
        btnHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trackerFragment.isVisible()) {
                    Dialog.exitTrackerActivityDialog(TrackerActivity.this);
                } else if (cameraFragment.isVisible()) {
                    changeFragment(trackerFragment);
                } else if (locationFragment.isVisible()) {
                    changeFragment(cameraFragment);
                } else if (commentFragment.isVisible()) {
                    changeFragment(locationFragment);
                } else {
                    finish();
                }
            }
        });
    }

    private void getIntentValue() {
        Log.d(TAG, "getIntentValue");
        if (getIntent().getExtras().getSerializable("obj_user") != null) {
            this.userObject = (UserObject) getIntent().getExtras().getSerializable("obj_user");
        } else {
            this.userObject = new UserObject();
        }
    }

    public void changeFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.tracker_activity_fragment_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void initGPS() {
        Log.d(TAG, "startGPS");

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                !EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                !EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                !EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.w(TAG, "initGPS: No permission to access location data.");
            return;
        }

        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Integer min_milliseconds = getResources().getInteger(R.integer.tracker_fragment_min_milliseconds);
        Integer min_meters = getResources().getInteger(R.integer.tracker_fragment_min_meters);
        try {
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                Log.d(TAG, "GPS_PROVIDER");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, min_milliseconds, min_meters, this);
            }
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                Log.d(TAG, "NETWORK_PROVIDER");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, min_milliseconds, min_meters, this);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    public void stopGPS() {
        Log.d(TAG, "stopGPS");
        try {
            locationManager.removeUpdates(this);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    public void startTracker() {
        Log.d(TAG, "startTracker");
        this.isRecording = true;
        startCountDownTimer();
    }

    public void pauseTracker() {
        Log.d(TAG, "pauseTracker");
        this.isRecording = false;
        countDownTimer.cancel();
    }

    public void startCountDownTimer() {
        long countDownintervalmillis = 1000;

        this.countDownTimer = new CountDownTimer(nowCountdownTotalMillis, countDownintervalmillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isRecording) {
                    nowCountdownTotalMillis = millisUntilFinished;
                    SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.subview_gps_tracker_countdown_time_format));
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String nowTimer = formatter.format(millisUntilFinished);
                    trackerFragment.tv_countDownTimer.setText(nowTimer);
                }
            }

            @Override
            public void onFinish() {
                stopGPS();
                countDownTimer.cancel();
            }
        };
        countDownTimer.start();
    }

    public void saveTracker() {
        String SIMPLE_DATE_FORMAT = getResources().getString(R.string.sdf_ymd_hms);
        SimpleDateFormat sdf = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        String currentDateTime = sdf.format(new Date());
        String title = "";

        try {
            Log.d(TAG,attachmentDescription.get(ATTACHMENT_DESCTIPTION_HEADER).toString());
            title = attachmentDescription.get(ATTACHMENT_DESCTIPTION_HEADER).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        Tracker storeTracker = new Tracker();
        storeTracker.title = title;
        storeTracker.path = trackerObject.getPath();
        storeTracker.created_by = userObject.getUserId();
        storeTracker.created_at = currentDateTime;
        storeTracker.save();
        int tracker_id = storeTracker.getId().intValue();

        for (AttachmentObject attachmentObject : arrayAttachmentObject) {
            Attachment storeAttachment = new Attachment();
            storeAttachment.name = attachmentObject.getName();
            storeAttachment.path = attachmentObject.getPath();
            storeAttachment.type = attachmentObject.getType();
            storeAttachment.description = attachmentObject.getDescription();
            storeAttachment.report_id = 0;
            storeAttachment.tracker_id = tracker_id;
            storeAttachment.created_at = currentDateTime;
            storeAttachment.save();
        }
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");

        nowLat = location.getLatitude();
        nowLng = location.getLongitude();
        Boolean moveNowLocation = true;

        DecimalFormat df = new DecimalFormat(getString(R.string.subview_gps_tracker_location_format));
        Double latitude = Double.valueOf(df.format(location.getLatitude()));
        Double longitude = Double.valueOf(df.format(location.getLongitude()));
        Double altitude = Double.valueOf(df.format(location.getAltitude()));
        Long unixtime = System.currentTimeMillis()/1000L;

        List<GeoPoint> tracker_points = new ArrayList<>();

        if (isRecording) {
            Log.d(TAG, "TrackerFragment::recording onLocationChanged: " + "Lat: " + latitude + " Lng: " + longitude + " Alt: " + altitude);

            if (list_tracker_points.size() > getResources().getInteger(R.integer.subview_gps_tracker_locations_limit)) {
                return;
            }

            SparseArray tracker_point = new SparseArray<>();
            tracker_point.put(MAPKEY_LAT, latitude);
            tracker_point.put(MAPKEY_LNG, longitude);
            tracker_point.put(MAPKEY_TIME, unixtime);
            tracker_point.put(MAPKEY_ALT, altitude);
            list_tracker_points.add(tracker_point);

            try {
                JSONArray jsonArray_location = new JSONArray();
                for (SparseArray list_tracker_point : list_tracker_points) {
                    tracker_points.add(new GeoPoint((Double)list_tracker_point.get(MAPKEY_LAT), (Double)list_tracker_point.get(MAPKEY_LNG)));

                    jsonArray_location.put(0, list_tracker_point.get(MAPKEY_LAT));
                    jsonArray_location.put(1, list_tracker_point.get(MAPKEY_LNG));
                    jsonArray_location.put(2, list_tracker_point.get(MAPKEY_TIME));
                    jsonArray_location.put(3, list_tracker_point.get(MAPKEY_ALT));

                    array_tracker_points.put(jsonArray_location);
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                e.printStackTrace();
            }

            String tracker_string = array_tracker_points.toString();
            trackerObject.setPath(tracker_string);
        }

        if (trackerFragment.isVisible()) {
            Log.d(TAG, "TrackerFragment::Drawing onLocationChanged: " + "Lat: " + latitude + " Lng: " + longitude + " Alt: " + altitude);
            if (moveNowLocation) {
                Message setNowCenterLocation = trackerFragment.locationHandler.obtainMessage(trackerFragment.LOCKED_LOCATION, new GeoPoint(latitude, longitude));
                trackerFragment.locationHandler.sendMessage(setNowCenterLocation);
                moveNowLocation = false;
            }

            Message message = trackerFragment.locationHandler.obtainMessage(trackerFragment.DRAW_TRACKER_LINE, tracker_points);
            trackerFragment.locationHandler.sendMessage(message);
        }

        if (locationFragment.isVisible()) {
            Log.d(TAG, "LocationFragment::onLocationChanged: " + "Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
            locationFragment.nowLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
            locationFragment.markerLocation(locationFragment.nowLocation);
            locationFragment.mapView.invalidate();
            locationFragment.markerChangeCount++;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled");
    }
}
