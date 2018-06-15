package com.thetigerparty.argodflib.SubView.TrackerActivity.Fragment;



import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.thetigerparty.argodflib.BuildConfig;
import com.thetigerparty.argodflib.HelperClass.FileHelper;
import com.thetigerparty.argodflib.Object.AttachmentObject;
import com.thetigerparty.argodflib.R;
import com.thetigerparty.argodflib.TrackerActivity;
import com.thetigerparty.argodflib.databinding.TrackerActivityTrackerFragmentBinding;

import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static android.app.Activity.RESULT_OK;

/**
 * Created by fredtsao on 2017/8/24.
 */

public class TrackerFragment extends Fragment {
    private final static String TAG = "TrackerFragment";
    public final static int LOCKED_LOCATION = 1;
    public final static int DRAW_TRACKER_LINE = 2;
    private final static int REQUEST_CAMERA = 1;
    TrackerActivityTrackerFragmentBinding binding;

    private View view;
    private TrackerActivity activity;
    public Button bt_save, bt_start, bt_pause, bt_take_photo;
    public TextView tv_countDownTimer;

    public MapView tracker_map_view;
    public Polyline tracker_line;
    public MyLocationNewOverlay tracker_overlay;
    public ArrayList<OverlayItem> overlayItemList = new ArrayList<>();
    public ItemizedIconOverlay<OverlayItem> gpsIconOverlay;

    private String currentPhotoPath;

    public TrackerFragment(){}

    public static TrackerFragment newInstance(){
        TrackerFragment fragment = new TrackerFragment();
        return fragment;
    }

    public Handler locationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOCKED_LOCATION:
                    GeoPoint now_location = (GeoPoint)msg.obj;

                    tracker_map_view.getController().setCenter(now_location);
                    break;

                case DRAW_TRACKER_LINE:
                    List<GeoPoint> array_locations = (List<GeoPoint>)msg.obj;

                    tracker_map_view.getOverlayManager().remove(tracker_line);
                    tracker_line.setPoints(array_locations);
                    tracker_map_view.getOverlayManager().add(tracker_line);
                    tracker_map_view.invalidate();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.tracker_activity_tracker_fragment, container, false);
        this.tv_countDownTimer = (TextView)view.findViewById(R.id.tracker_fragment_count_down_timer);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        this.activity = (TrackerActivity) getActivity();

        initView(view);
        initMapView(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            File photoFile = new File(currentPhotoPath);
            try {
                if (photoFile.exists()) {
                    String fileName = currentPhotoPath.substring(currentPhotoPath.lastIndexOf("/") + 1, currentPhotoPath.length());
                    String fileType = FileHelper.getMimeTypeByPath(currentPhotoPath);

                    activity.currentAttachmentObject = new AttachmentObject();
                    activity.currentAttachmentObject.setName(fileName);
                    activity.currentAttachmentObject.setPath(currentPhotoPath);
                    activity.currentAttachmentObject.setType(fileType);

                    Log.d(TAG, String.format("New file inserted into gallery: %s", currentPhotoPath));

                    currentPhotoPath = "";
                    activity.changeFragment(activity.locationFragment);
                } else {
                    throw new Exception("File not exists. Path: " + currentPhotoPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        markerLocation();
    }

    private void initView(View view) {
        this.bt_take_photo = (Button)view.findViewById(R.id.tracker_fragment_take_photo_button);
        this.bt_start = (Button)view.findViewById(R.id.tracker_fragment_start_button);
        this.bt_save = (Button)view.findViewById(R.id.tracker_fragment_save_button);
        this.bt_pause = (Button)view.findViewById(R.id.tracker_fragment_pause_button);

        if (activity.isRecording) {
            this.bt_start.setVisibility(View.GONE);
            this.bt_pause.setVisibility(View.VISIBLE);
        } else {
            this.bt_start.setVisibility(View.VISIBLE);
            this.bt_pause.setVisibility(View.GONE);
        }

        this.bt_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOriginCamera();
            }
        });
        this.bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startTracker();
                bt_start.setVisibility(View.GONE);
                bt_pause.setVisibility(View.VISIBLE);
            }
        });
        this.bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.saveTracker();
                activity.finish();
            }
        });
        this.bt_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.pauseTracker();
                bt_start.setVisibility(View.VISIBLE);
                bt_pause.setVisibility(View.GONE);
            }
        });
    }

    private void initMapView(View view) {
        Double lat = activity.nowLat;
        Double lng = activity.nowLng;
        if (lat == null && lng == null) {
            lat = BuildConfig.DEFAULT_MAP_CENTER_LATITUDE;
            lng = BuildConfig.DEFAULT_MAP_CENTER_LONGITUDE;
        }

        GeoPoint centerPoint = new GeoPoint(lat, lng);
        this.tracker_map_view = (MapView)view.findViewById(R.id.tracker_fragment_tracker_map_view);
        tracker_map_view.getTileProvider().clearTileCache();
        tracker_map_view.setUseDataConnection(true);
        tracker_map_view.setBuiltInZoomControls(true);
        tracker_map_view.setMultiTouchControls(true);
        tracker_map_view.setMinZoomLevel(activity.getResources().getInteger(R.integer.tracker_fragment_min_zoom_level));
        tracker_map_view.setMaxZoomLevel(activity.getResources().getInteger(R.integer.tracker_fragment_max_zoom_level));
        tracker_map_view.getController().setZoom(activity.getResources().getInteger(R.integer.tracker_fragment_init_zoom_level));
        tracker_map_view.getController().setCenter(centerPoint);

        this.tracker_line = new Polyline();
        tracker_line.setGeodesic(true);
        tracker_line.setWidth(8.0f);
        tracker_line.setColor(Color.RED);

        this.tracker_overlay = new MyLocationNewOverlay(new GpsMyLocationProvider(activity), tracker_map_view);
        tracker_map_view.getOverlays().add(tracker_overlay);
        tracker_overlay.enableMyLocation();
        tracker_map_view.invalidate();
    }

    public void markerLocation() {
        String json_string;
        JSONObject json_desctiption;
        String header, content;
        Double lat = activity.nowLat, lng = activity.nowLng;
        int image_size = activity.getResources().getInteger(R.integer.tracker_fragment_marker_image_size);

        if (gpsIconOverlay != null) {
            tracker_map_view.getOverlays().remove(gpsIconOverlay);
        }

        try {
            for (AttachmentObject attachmentObject : activity.arrayAttachmentObject) {
                json_string = attachmentObject.getDescription().toString();
                json_desctiption = new JSONObject(json_string);
                header = json_desctiption.get(activity.ATTACHMENT_DESCTIPTION_HEADER).toString();
                content = json_desctiption.get(activity.ATTACHMENT_DESCTIPTION_CONTENT).toString();
                lat = json_desctiption.getDouble(activity.ATTACHMENT_DESCTIPTION_LAT);
                lng = json_desctiption.getDouble(activity.ATTACHMENT_DESCTIPTION_LNG);
                OverlayItem item = new OverlayItem(header, content, new GeoPoint(lat, lng));

                Drawable drawable = Drawable.createFromPath(attachmentObject.getPath());
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, image_size, image_size, true));
                bitmap.recycle();
                item.setMarker(drawable);
                overlayItemList.add(item);
                gpsIconOverlay = new ItemizedIconOverlay<>(activity, overlayItemList, null);
                tracker_map_view.getOverlays().add(gpsIconOverlay);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    private void openOriginCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = FileHelper.createPhotoFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            currentPhotoPath = photoFile.getAbsolutePath();
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (Exception e) {
            Logger.e(e.getMessage());
            e.printStackTrace();
        }
    }
}
