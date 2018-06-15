package com.thetigerparty.argodflib.SubView.TrackerActivity.Fragment;

import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thetigerparty.argodflib.BuildConfig;
import com.thetigerparty.argodflib.R;
import com.thetigerparty.argodflib.TrackerActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

/**
 * Created by fredtsao on 2017/8/24.
 */

public class LocationFragment extends Fragment {
    private final static String TAG = "LocationFragment";

    private View view;
    private TrackerActivity activity;
    private Button btnConfirm;

    public MapView mapView;
    public IMapController mapController;

    public GeoPoint nowLocation;
    public GeoPoint touchLocation;
    public GeoPoint saveLocation;
    public ItemizedIconOverlay<OverlayItem> gpsIconOverlay;
    public int markerChangeCount = 0;

    public LocationFragment() {}

    public static LocationFragment newInstance(){
        LocationFragment fragment = new LocationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.tracker_activity_location_fragment, container, false);

        this.btnConfirm = (Button)view.findViewById(R.id.location_fragment_bt_location_confirm);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = (TrackerActivity) getActivity();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (touchLocation == null) {
                    saveLocation = nowLocation;
                } else {
                    saveLocation = touchLocation;
                }

                activity.attachmentDescription = new JSONObject();
                try {
                    activity.attachmentDescription.put(activity.ATTACHMENT_DESCTIPTION_LAT, saveLocation.getLatitude());
                    activity.attachmentDescription.put(activity.ATTACHMENT_DESCTIPTION_LNG, saveLocation.getLongitude());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }

                activity.changeFragment(activity.commentFragment);
            }
        });

        initMapView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initMapView() {
        Double lat = activity.nowLat;
        Double lng = activity.nowLng;
        if (lat == null && lng == null) {
            lat = BuildConfig.DEFAULT_MAP_CENTER_LATITUDE;
            lng = BuildConfig.DEFAULT_MAP_CENTER_LONGITUDE;
        }

        GeoPoint centerPoint = new GeoPoint(lat, lng);
        this.mapView = (MapView) activity.findViewById(R.id.location_fragment_map_view);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setTilesScaledToDpi(true);

        // init touch event marker method
        Overlay overlay = new Overlay() {
            @Override
            public void draw(Canvas c, MapView osmv, boolean shadow) {

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent event, MapView view) {
                Projection projection = view.getProjection();
                GeoPoint location = (GeoPoint)projection.fromPixels((int)event.getX(), (int)event.getY());
                touchLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

                ArrayList<OverlayItem> overlayItemList = new ArrayList<>();
                OverlayItem item = new OverlayItem("GPSLocation", "GPSLocation", location);
                Drawable gpsMarker = activity.getResources().getDrawable(R.drawable.touch_marker);
                item.setMarker(gpsMarker);
                overlayItemList.add(item);

                if (gpsIconOverlay != null) {
                    mapView.getOverlays().remove(gpsIconOverlay);
                }

                gpsIconOverlay = new ItemizedIconOverlay<>(activity, overlayItemList, null);
                mapView.getOverlays().add(gpsIconOverlay);
                mapView.invalidate();

                return super.onSingleTapConfirmed(event, view);
            }
        };
        mapView.getOverlays().add(overlay);

        mapController = mapView.getController();
        mapController.setZoom(15);
        mapController.setCenter(centerPoint);
        mapController.animateTo(centerPoint);
        mapView.invalidate();

        nowLocation = centerPoint;
        markerLocation(centerPoint);
    }

    public void markerLocation(GeoPoint point) {
        if (gpsIconOverlay != null) {
            mapView.getOverlays().remove(gpsIconOverlay);
        }
        mapController.setCenter(point);

        ArrayList<OverlayItem> overlayItemList = new ArrayList<>();
        OverlayItem item = new OverlayItem("NowLocation", "NowLocation", point);
        Drawable marker = activity.getResources().getDrawable(R.drawable.touch_marker);
        item.setMarker(marker);
        overlayItemList.add(item);

        gpsIconOverlay = new ItemizedIconOverlay<>(activity, overlayItemList, null);
        mapView.getOverlays().add(gpsIconOverlay);
    }
}
