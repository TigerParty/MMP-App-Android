package com.thetigerparty.argodflib.Subclass.Report.FieldSubview;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thetigerparty.argodflib.HelperClass.Config;
import com.thetigerparty.argodflib.HelperClass.Dialog;
import com.thetigerparty.argodflib.Object.FormFieldObject;
import com.thetigerparty.argodflib.R;
import com.thetigerparty.argodflib.ReportActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by ttpttp on 2015/8/12.
 */

interface MapKey {
    int latitude = 1;
    int longitude = 2;
    int unixtime = 3;
    int altitude =4;
}

interface HandlerKey {
    int location_should_be_locked = 1;
    int draw_polyline = 2;
}

public class GPSTrackerSubview extends LinearLayout implements DynamicForm, LocationListener {
    FormFieldObject obj_form_field;
    Context context;

    private RelativeLayout layout_mapview;
    private RelativeLayout layout_status;
    private MapView map_view;
    private Button gps_start;
    private Button gps_pause;
    private Button my_location;
    private TextView text_message;
    private TextView text_countdown;
    private TextView text_keep_open;
    private MyLocationNewOverlay location_overlay;
    private List<SparseArray> locations_with_map;
    private boolean location_should_be_locked = true;
    private Long countdown_total_time_with_millis;
    private CountDownTimer cd_timer;
    private Polyline line;
    private boolean gps_should_be_recording = false;
    private JSONArray array_location = new JSONArray();

    Handler messageHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {

            switch (msg.what) {
                case HandlerKey.location_should_be_locked:
                    GeoPoint point = (GeoPoint)msg.obj;

                    my_location.setEnabled(false);
                    map_view.getController().setZoom(getResources().getInteger(R.integer.subview_gps_tracker_auto_locate_zoom_level));
                    map_view.getController().setCenter(point);
                    break;

                case HandlerKey.draw_polyline:
                    List<GeoPoint> points = (List<GeoPoint>)msg.obj;

                    map_view.getOverlayManager().remove(line);
                    line.setPoints(points);
                    map_view.getOverlayManager().add(line);
                    map_view.invalidate();
                    break;
            }
        }
    };

    public GPSTrackerSubview(Context context) {
        super(context);
    }

    public GPSTrackerSubview(Context context, FormFieldObject obj_form_field) {
        super(context);

        this.context = context;
        this.obj_form_field = obj_form_field;
        countdown_total_time_with_millis = Long.valueOf(getResources().getInteger(R.integer.subview_gps_tracker_locations_limit) * getResources().getInteger(R.integer.subview_gps_tracker_min_milliseconds));

        setupView();

        if (obj_form_field.getValue().isEmpty()) {
            layout_mapview.setVisibility(View.VISIBLE);
            text_message.setVisibility(View.GONE);
        }
        else {
            layout_mapview.setVisibility(View.GONE);
            text_message.setVisibility(View.VISIBLE);
        }
    }

    private void setupView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.report_activity_tracker_subview, this, true);
        locations_with_map = new ArrayList<>();

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
            Integer min_milliseconds = getResources().getInteger(R.integer.subview_gps_tracker_min_milliseconds);
            Integer min_meters = getResources().getInteger(R.integer.subview_gps_tracker_min_meters);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, min_milliseconds, min_meters, this);
        }

        //Init map.
        layout_mapview = (RelativeLayout) findViewById(R.id.layout_mapview);
        layout_status = (RelativeLayout) findViewById(R.id.layout_status);
        map_view = (MapView) findViewById(R.id.mapview);
        gps_start = (Button) findViewById(R.id.gps_start);
        gps_pause = (Button) findViewById(R.id.gps_pause);
        my_location = (Button) findViewById(R.id.my_location);
        text_message = (TextView) findViewById(R.id.text_message);
        text_countdown = (TextView) findViewById(R.id.text_countdown);
        text_keep_open = (TextView) findViewById(R.id.text_keep_open);

        map_view.getTileProvider().clearTileCache();
        map_view.setUseDataConnection(true);
        map_view.setBuiltInZoomControls(false);
        map_view.setMultiTouchControls(true);
        map_view.setMinZoomLevel(getResources().getInteger(R.integer.subview_gps_tracker_min_zoom_level));
        map_view.setMaxZoomLevel(getResources().getInteger(R.integer.subview_gps_tracker_max_zoom_level));
        map_view.getController().setZoom(getResources().getInteger(R.integer.subview_gps_tracker_init_zoom_level));
        map_view.getController().setCenter(new GeoPoint(Double.valueOf(context.getString(R.string.subview_gps_tracker_init_latitude)), Double.valueOf(context.getString(R.string.subview_gps_tracker_init_longitude))));
        line = new Polyline(context);
        line.setGeodesic(true);
        line.setWidth(5.0f);
        line.setColor(Color.BLACK);

        location_overlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map_view);
        map_view.getOverlays().add(location_overlay);
        location_overlay.enableMyLocation();

        map_view.invalidate();

        gps_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGPS();
            }
        });

        gps_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseGPS();
            }
        });

        my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location_should_be_locked = true;

                try {
                    SparseArray location = locations_with_map.get(locations_with_map.size() - 1);
                    map_view.getController().setCenter(new GeoPoint((Double) location.get(MapKey.latitude), (Double) location.get(MapKey.longitude)));
                    my_location.setEnabled(false);
                }
                catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        map_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);

                if ((event.getAction() == MotionEvent.ACTION_MOVE) && (!locations_with_map.isEmpty())) {
                    location_should_be_locked = false;
                    my_location.setEnabled(true);
                }
                return false;
            }
        });
    }

    public FormFieldObject getFormFieldObject() {
        return obj_form_field;
    }

    public void clearValue() {
        obj_form_field.setValue("");
    }

    public void startGPS() {
        gps_should_be_recording = true;
        location_should_be_locked = true;

        ((ReportActivity)context).spinner_form.setEnabled(false);

        layout_status.setVisibility(View.VISIBLE);
        cd_timer = new CountDownTimer(countdown_total_time_with_millis, 1000) {
            public void onTick(long millisUntilFinished) {
                countdown_total_time_with_millis = millisUntilFinished;

                //Convert to correct format.
                SimpleDateFormat formatter = new SimpleDateFormat(context.getString(R.string.subview_gps_tracker_countdown_time_format));
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String st_time = formatter.format(millisUntilFinished);
                text_countdown.setText(st_time);
            }

            public void onFinish() {
                pauseGPS();
            }
        };

        cd_timer.start();
        gps_start.setEnabled(false);
        gps_pause.setEnabled(true);
        my_location.setEnabled(false);
        text_keep_open.setVisibility(View.VISIBLE);
    }

    private void pauseGPS() {
        gps_should_be_recording = false;
        cd_timer.cancel();

        gps_start.setEnabled(true);
        gps_pause.setEnabled(false);
        my_location.setEnabled(false);
        layout_status.setVisibility(View.GONE);
        text_keep_open.setVisibility(View.GONE);
    }

    @Override
    public void onLocationChanged(Location location) {
        DecimalFormat df = new DecimalFormat(context.getString(R.string.subview_gps_tracker_location_format));
        Double latitude = Double.valueOf(df.format(location.getLatitude()));
        Double longitude = Double.valueOf(df.format(location.getLongitude()));
        Double altitude = Double.valueOf(df.format(location.getAltitude()));
        Long unixtime = System.currentTimeMillis()/1000L;

        Log.d("GPS Location"," Lat: "+latitude+", Long: "+longitude + ", Alt: " + altitude);

        if (location_should_be_locked) {
            Message message = messageHandler.obtainMessage(HandlerKey.location_should_be_locked, new GeoPoint(latitude, longitude));
            messageHandler.sendMessage(message);
        }

        //Limitation in locations.
        if (locations_with_map.size() > getResources().getInteger(R.integer.subview_gps_tracker_locations_limit)) {
            return;
        }

        //Make single location then put into locations.
        if (gps_should_be_recording) {
            SparseArray location_with_key = new SparseArray<>();
            location_with_key.put(MapKey.latitude, latitude);
            location_with_key.put(MapKey.longitude, longitude);
            location_with_key.put(MapKey.unixtime, unixtime);
            location_with_key.put(MapKey.altitude, altitude);
            locations_with_map.add(location_with_key);
        }

        //Process output locations for line and final value.
        List<GeoPoint> points = new ArrayList<>();
        if (gps_should_be_recording) {
            try{
                JSONArray jsonArray_location = new JSONArray();
                for(SparseArray location_with_map : locations_with_map){
                    points.add(new GeoPoint((Double) location_with_map.get(MapKey.latitude), (Double) location_with_map.get(MapKey.longitude)));

                    jsonArray_location.put(0,location_with_map.get(MapKey.latitude));
                    jsonArray_location.put(1,location_with_map.get(MapKey.longitude));
                    jsonArray_location.put(2,location_with_map.get(MapKey.unixtime));
                    jsonArray_location.put(3,location_with_map.get(MapKey.altitude));

                    array_location.put(jsonArray_location);
                }
            } catch (JSONException je){
                je.printStackTrace();
            }

            String value = array_location.toString();
            obj_form_field.setValue(value);
        }

        Message message = messageHandler.obtainMessage(HandlerKey.draw_polyline, points);
        messageHandler.sendMessage(message);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        gps_start.setEnabled(true);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context, "Your GPS seems to be disabled", Toast.LENGTH_LONG).show();
        Dialog.GPSDialog(this.context);
        gps_start.setEnabled(false);
    }
}
