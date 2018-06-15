package com.thetigerparty.argodflib;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.thetigerparty.argodflib.Adapter.AttachRecyclerViewAdapter;
import com.thetigerparty.argodflib.Component.RegionComponent;
import com.thetigerparty.argodflib.Component.RegionSpinnerComponent;
import com.thetigerparty.argodflib.HelperClass.Config;
import com.thetigerparty.argodflib.HelperClass.Dialog;
import com.thetigerparty.argodflib.HelperClass.ImageProcess;
import com.thetigerparty.argodflib.HelperClass.Validator;
import com.thetigerparty.argodflib.Model.ArgoConfig;
import com.thetigerparty.argodflib.Model.Attachment;
import com.thetigerparty.argodflib.Model.Field;
import com.thetigerparty.argodflib.Model.Form;
import com.thetigerparty.argodflib.Model.FormField;
import com.thetigerparty.argodflib.Model.LocalProject;
import com.thetigerparty.argodflib.Model.Project;
import com.thetigerparty.argodflib.Model.Region;
import com.thetigerparty.argodflib.Model.Report;
import com.thetigerparty.argodflib.Model.ReportValue;
import com.thetigerparty.argodflib.Object.AttachmentObject;
import com.thetigerparty.argodflib.Object.ContainerObject;
import com.thetigerparty.argodflib.Object.FieldObject;
import com.thetigerparty.argodflib.Object.FormFieldObject;
import com.thetigerparty.argodflib.Object.FormObject;
import com.thetigerparty.argodflib.Object.ProjectObject;
import com.thetigerparty.argodflib.Object.RegionObject;
import com.thetigerparty.argodflib.Object.ReportObject;
import com.thetigerparty.argodflib.Object.ShowIfObject;
import com.thetigerparty.argodflib.Object.UserObject;
import com.thetigerparty.argodflib.Service.BackgroundRunnerProvider;
import com.thetigerparty.argodflib.Service.BackgroundRunnerService;
import com.thetigerparty.argodflib.Subclass.Report.FieldSubview.CheckBoxGroupSubview;
import com.thetigerparty.argodflib.Subclass.Report.FieldSubview.CheckBoxSubview;
import com.thetigerparty.argodflib.Subclass.Report.FieldSubview.DatePickerSubview;
import com.thetigerparty.argodflib.Subclass.Report.FieldSubview.DropDownListSubview;
import com.thetigerparty.argodflib.Subclass.Report.FieldSubview.DynamicForm;
import com.thetigerparty.argodflib.Subclass.Report.FieldSubview.RadioButtonSubview;
import com.thetigerparty.argodflib.Subclass.Report.FieldSubview.TextAreaSubview;
import com.thetigerparty.argodflib.Subclass.Report.FieldSubview.TextBoxSubview;
import com.thetigerparty.argodflib.Subclass.Report.FieldSubview.TextNumericalSubview;
import com.thetigerparty.argodflib.Subclass.Report.FieldSubview.GPSTrackerSubview;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by ttpttp on 2015/8/5.
 */
public class ReportActivity extends Activity implements LocationListener, Observer {

    private static final String TAG = ReportActivity.class.getSimpleName();
    private RecyclerView rvAttachment;
    private AttachRecyclerViewAdapter attachRecyclerViewAdapter;

    public LinearLayout layout_attachment;
    public Spinner spinner_form;
    public ReportObject obj_report = new ReportObject();

    LinearLayout layout_report;
    LinearLayout layout_form_field;
    LinearLayout layout_mapview;
    LinearLayout layout_reporter_info;

    TextView tv_project_name;
    TextView tv_map_loading;
    TextView tv_header_title;
    TextView tv_project_name_title;

    EditText et_project_name;
    EditText et_title;
    EditText et_description;
    EditText et_latitude;
    EditText et_longitude;
    EditText et_reporter_name;
    EditText et_reporter_email;

    Spinner spinner_region;

    Button bt_camera;
    Button bt_album;
    Button bt_save;
    Button bt_cancel;
    Button bt_record;
    Button bt_camera_caption;
    Button bt_reporter_next;
    Button bt_header_back;

    MapView mapView;
    IMapController mapController;
    MyLocationNewOverlay myLocationOverlay;
    ArrayList<OverlayItem> overlayItems = new ArrayList<>();

    LocationManager locationManager;
    Location location;

    ArrayList<RegionObject> list_all_region = new ArrayList<>();
    List<Region> list_region_by_project = new ArrayList<>();

    ArrayList<FormObject> list_obj_form = new ArrayList<>();
    ArrayList<String> list_form_name = new ArrayList<>();

    ArrayList<LinearLayout> list_layout_spinner = new ArrayList<>();
    ArrayList<Spinner> list_spinner = new ArrayList<>();

    RegionComponent regionComponent = new RegionComponent();
    RegionSpinnerComponent regionSpinnerComponent;

    Set selected_region_collection = new HashSet();

    UserObject obj_user = null;


    ProjectObject parent_project_obj;
    ContainerObject container_obj = null;

    BackgroundRunnerService backgroundRunnerService;
    BackgroundRunnerProvider backgroundRunnerProvider;
    BackgroundRunnerProvider.BackgroundRunnerServiceConnection backgroundRunnerServiceConnection;

    boolean is_new_project = false;

    public Handler handler = new MsgHandler(this);

    String current_image_path;

    String SIMPLE_DATE_FORMAT;
    int REQUEST_CAMERA = 1;
    int SELECT_FILE = 2;
    int REQUEST_RECORD_VIDEO = 3;
    int REQUEST_CAMERA_CAPTION = 4;
    int REQUEST_CAPTION_ACTIVITY = 5;

    final static int SAVE_REPORT_SUCCEED = 1;
    final static int SAVE_REPORT_FAILED = 2;
    public final static int OPTION_CHANGED_IN_DYNAMIC_FORM = 3;
    final static int PRESS_BACK_BUTTON = 4;
    int MAX_UPLOAD_ATTACHMENT_SIZE;

    static String TOAST_SAVE_REPORT_FAIL;

    static class MsgHandler extends Handler {
        WeakReference<ReportActivity> reportActivity;

        MsgHandler(ReportActivity activity) {
            reportActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            ReportActivity activity = reportActivity.get();
            switch (msg.what) {
                case SAVE_REPORT_SUCCEED:
                    Dialog.saveReportSuccessDialog(activity);
                    break;

                case SAVE_REPORT_FAILED:
                    Toast.makeText(activity, TOAST_SAVE_REPORT_FAIL, Toast.LENGTH_LONG).show();
                    break;

                case OPTION_CHANGED_IN_DYNAMIC_FORM:
                    FormFieldObject obj_form_field_changed = (FormFieldObject) msg.obj;

                    for (int i = 0; i < activity.layout_form_field.getChildCount(); i++) {
                        //Apply "DynamicForm" interface to implement action in each form.
                        DynamicForm dynamic_form = (DynamicForm) activity.layout_form_field.getChildAt(i);
                        FormFieldObject obj_form_field = dynamic_form.getFormFieldObject();

                        if (obj_form_field.getFormula() == null) {
                            if (!obj_form_field.getShowIf().equals("")) {
                                try {
                                    JSONObject jsObj = new JSONObject(obj_form_field.getShowIf());
                                    int form_field_id = Integer.parseInt(jsObj.keys().next());
                                    String form_field_value = jsObj.getJSONArray(jsObj.keys().next()).get(0).toString();

                                    //Check with "show if".
                                    if ((obj_form_field_changed.getId() == form_field_id) && (obj_form_field_changed.getValue().equals(form_field_value))) {
                                        dynamic_form.setVisibility(View.VISIBLE);

                                        //Only for DatePickerSubview.
                                        if (dynamic_form.getClass().getSimpleName().equals("DatePickerSubview")) {
                                            ((DatePickerSubview) dynamic_form).setValueWithDateNow();
                                        }
                                    } else if ((obj_form_field_changed.getId() == form_field_id) && (!obj_form_field_changed.getValue().equals(form_field_value))) {
                                        dynamic_form.setVisibility(View.GONE);
                                        dynamic_form.clearValue();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    break;
                case PRESS_BACK_BUTTON:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);

        getIntentValue();
        getResourceValue();
        setReportObject();

        findView();

        setupLocation();
        setupRegion();
        setupForm();

        if (obj_report.getPushed() == 1) {
            clearAttachments();
        } else {
            showAttachment();
        }

        backgroundRunnerProvider.add_observer(this);
        this.backgroundRunnerServiceConnection = backgroundRunnerProvider.getBackgroundRunnerServiceConnection();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!Config.checkGPS(this)) {
            Dialog.GPSDialog(this);
        }

        bindService(new Intent(this, BackgroundRunnerService.class), backgroundRunnerServiceConnection, Service.BIND_ADJUST_WITH_ACTIVITY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (layout_reporter_info.getVisibility() == View.GONE && layout_report.getVisibility() == View.VISIBLE) {
                layout_report.setVisibility(View.GONE);
                layout_reporter_info.setVisibility(View.VISIBLE);
            }
            Dialog.exitReportDialog(this);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.checkGPS(this)) {
            // delay one second for start gps
            tv_map_loading.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startGPS();
                }
            }, 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Config.checkGPS(this)) {
            stopGPS();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(backgroundRunnerServiceConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundRunnerProvider.deleteObserver(this);

        if (Config.checkGPS(this)) {
            stopGPS();
        }
        location = null;
        locationManager = null;
        obj_report = null;
        obj_user = null;

        mapView = null;
        mapController = null;
        myLocationOverlay = null;
        overlayItems = null;

        handler = null;

        list_form_name = null;
        list_obj_form = null;

        layout_report = null;
        layout_form_field = null;
        layout_attachment = null;

        tv_project_name = null;
        tv_map_loading = null;

        et_project_name = null;
        et_title = null;
        et_description = null;
        et_latitude = null;
        et_longitude = null;

        spinner_form = null;

        bt_camera = null;
        bt_album = null;
        bt_save = null;
        bt_cancel = null;
        bt_record = null;

        backgroundRunnerProvider = null;
        backgroundRunnerServiceConnection = null;
        backgroundRunnerService = null;

        Runtime.getRuntime().gc();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            try {
                File file = new File(current_image_path);
                if (file.exists()) {
                    String fileName = current_image_path.substring(current_image_path.lastIndexOf("/") + 1, current_image_path.length());
                    String fileType = ImageProcess.getMimeTypeByPath(current_image_path);

                    AttachmentObject obj_attachment = new AttachmentObject();
                    obj_attachment.setName(fileName);
                    obj_attachment.setPath(current_image_path);
                    obj_attachment.setType(fileType);

                    addAttachmentToList(obj_attachment);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
            Uri selectedImageUri = intent.getData();
            current_image_path = ImageProcess.getPath(selectedImageUri, this);
            Intent caption_intent = new Intent(this, CaptionActivity.class);
            AttachmentObject attachmentObject = new AttachmentObject();
            attachmentObject.setPath(current_image_path);
            caption_intent.putExtra(CaptionActivity.KEY_ATTACHMENT, attachmentObject);
            startActivityForResult(caption_intent, REQUEST_CAPTION_ACTIVITY);
        } else if (requestCode == REQUEST_RECORD_VIDEO && resultCode == RESULT_OK) {
            try {
                String video_path = getRealPathFromURI(intent.getData());
                File video_file = new File(video_path);

                if (video_file.exists()) {
                    String file_name = video_file.getName();
                    String fileType = ImageProcess.getMimeTypeByPath(video_path);

                    AttachmentObject obj_attachment = new AttachmentObject();
                    obj_attachment.setName(file_name);
                    obj_attachment.setPath(video_path);
                    obj_attachment.setType(fileType);

                    addAttachmentToList(obj_attachment);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CAMERA_CAPTION && resultCode == RESULT_OK) {
            Intent caption_intent = new Intent(this, CaptionActivity.class);
            AttachmentObject attachmentObject = new AttachmentObject();
            attachmentObject.setPath(current_image_path);
            caption_intent.putExtra(CaptionActivity.KEY_ATTACHMENT, attachmentObject);
            startActivityForResult(caption_intent, REQUEST_CAPTION_ACTIVITY);
        } else if (requestCode == REQUEST_CAPTION_ACTIVITY && resultCode == RESULT_OK) {
            if (intent.getExtras() == null) {
                Log.e(TAG, String.format("onActivityResult: RequestCode(%d): null intent", requestCode));
                return;
            }

            AttachmentObject attachmentObject = (AttachmentObject) intent.getSerializableExtra(CaptionActivity.KEY_ATTACHMENT);
            int position = intent.getIntExtra(CaptionActivity.KEY_POSITION, 0);
            Gson gson = new GsonBuilder().serializeNulls().create();
            Log.d(TAG, "onActivityResult: " + gson.toJson(attachmentObject));

            //-- new attach
            if (attachmentObject.getReportId() == 0) {
                addAttachmentToList(attachmentObject);
            }
            //-- edit attach
            else {
                attachRecyclerViewAdapter.replaceItem(position, attachmentObject);
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        return ImageProcess.getPath(contentUri, this);
    }

    void getIntentValue() {
        if (getIntent().getExtras() != null) {
            backgroundRunnerProvider = (BackgroundRunnerProvider) getIntent().getExtras().getSerializable("background_runner");
            obj_user = (UserObject) getIntent().getExtras().getSerializable("obj_user");
            parent_project_obj = (ProjectObject) getIntent().getExtras().getSerializable("parent_project_obj");
            container_obj = (ContainerObject) getIntent().getExtras().getSerializable("container_obj");
            is_new_project = (boolean) getIntent().getExtras().get("is_new_project");
            obj_report = (ReportObject) getIntent().getExtras().getSerializable("obj_report");
        }
    }

    void getResourceValue() {
        SIMPLE_DATE_FORMAT = getString(R.string.sdf_ymd_hms);
        TOAST_SAVE_REPORT_FAIL = getString(R.string.save_report_fail);
        MAX_UPLOAD_ATTACHMENT_SIZE = getResources().getInteger(R.integer.max_attachments);
    }

    private void setReportObject() {
        if (obj_report == null) {
            this.obj_report = new ReportObject();

            FormObject form_obj = new FormObject();
            Form container_form = Form.selectSingle(container_obj.getForm_id());

            if (container_form == null) {
                container_form = Form.getDefaultForm();
            }

            form_obj.setName(container_form.name);
            form_obj.setId(container_form.form_id);

            obj_report.setForm(form_obj);

            if (!is_new_project) {
                obj_report.setProject(parent_project_obj);
                obj_report.setProjectId(parent_project_obj.getId());
                obj_report.setProjectType(parent_project_obj.getType());
            }
        }
    }

    void findView() {
        layout_mapview = (LinearLayout) this.findViewById(R.id.layout_mapview);
        layout_mapview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
                    Config.hideKeyboard(ReportActivity.this);
                }
                return false;
            }
        });

        layout_report = (LinearLayout) this.findViewById(R.id.layout_report);
        layout_report.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
                    Config.hideKeyboard(ReportActivity.this);
                }
                return false;
            }
        });

        tv_project_name = (TextView) this.findViewById(R.id.tv_project_name);

        et_project_name = (EditText) this.findViewById(R.id.et_project_name);

        tv_project_name_title = (TextView) this.findViewById(R.id.tv_project_name_title);
        if (container_obj != null) {
            tv_project_name_title.setText(container_obj.getContainerName());
        }

        if (is_new_project) {
            et_project_name.setVisibility(View.VISIBLE);
            tv_project_name.setVisibility(View.GONE);
        } else {
            tv_project_name.setText(parent_project_obj.getTitle());
            tv_project_name.setVisibility(View.VISIBLE);
            et_project_name.setVisibility(View.GONE);
        }

        et_description = (EditText) this.findViewById(R.id.et_description);
        et_description.setText(obj_report.getDescription());
        et_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                obj_report.setDescription(et_description.getText().toString());
            }
        });

        spinner_form = (Spinner) this.findViewById(R.id.spinner_form);
        spinner_form.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshForm(position);
                obj_report.setForm(list_obj_form.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        LinearLayout layout_form = (LinearLayout) this.findViewById(R.id.layout_form);
        layout_form.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
                    Config.hideKeyboard(ReportActivity.this);
                }
                return false;
            }
        });

        layout_form_field = (LinearLayout) this.findViewById(R.id.layout_form_field);
        layout_form_field.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
                    Config.hideKeyboard(ReportActivity.this);
                }
                return false;
            }
        });

        et_latitude = (EditText) this.findViewById(R.id.et_latitude);
        et_latitude.setText(String.valueOf(obj_report.getLatitude()));
        et_latitude.clearFocus();

        et_longitude = (EditText) this.findViewById(R.id.et_longitude);
        et_longitude.setText(String.valueOf(obj_report.getLongitude()));
        et_longitude.clearFocus();

        tv_map_loading = (TextView) this.findViewById(R.id.tv_map_loading);

        attachRecyclerViewAdapter = new AttachRecyclerViewAdapter();
        attachRecyclerViewAdapter.addOnAttachClickListener(new AttachRecyclerViewAdapter.OnAttachClickListener() {
            @Override
            public void onEditClick(int position, AttachmentObject object) {
                Log.d(TAG, "onEditClick: " + position);

                Gson gson = new GsonBuilder().serializeNulls().create();
                Log.d(TAG, "onEditClick: " + gson.toJson(object));

                Intent intent = new Intent(ReportActivity.this, CaptionActivity.class);
                intent.putExtra(CaptionActivity.KEY_POSITION, position);
                intent.putExtra(CaptionActivity.KEY_ATTACHMENT, object);
                startActivityForResult(intent, REQUEST_CAPTION_ACTIVITY);
            }

            @Override
            public void onDeleteClick(int position, AttachmentObject object) {
                Log.d(TAG, "onDeleteClick: " + position);

                attachRecyclerViewAdapter.removeItem(position);
            }
        });
        rvAttachment = (RecyclerView) findViewById(R.id.rv_attachment);
        rvAttachment.setAdapter(attachRecyclerViewAdapter);
        rvAttachment.setLayoutManager(new LinearLayoutManager(this));
        rvAttachment.setHasFixedSize(false);

        bt_camera = (Button) this.findViewById(R.id.bt_camera);
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraOnClick();
            }
        });

        bt_album = (Button) this.findViewById(R.id.bt_album);
        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumOnClick();
            }
        });

        bt_save = (Button) this.findViewById(R.id.bt_save);
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidator() && checkIsRequiredField() && checkIsPhotoRequired()) {
                    clickedSaveReport();
                }
            }
        });

        bt_cancel = (Button) this.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.exitReportDialog(ReportActivity.this);
            }
        });

        bt_record = (Button) this.findViewById(R.id.bt_record);
        bt_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record_on_click();
            }
        });

        bt_camera_caption = (Button) this.findViewById(R.id.bt_camera_caption);
        bt_camera_caption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera_on_click();
            }
        });

        if (BuildConfig.VIDEO_RECORD_ENABLE) {
            bt_record.setVisibility(View.VISIBLE);
        } else {
            bt_record.setVisibility(View.GONE);
        }

        if (BuildConfig.CAPTION_ACTIVITY_ENABLE) {
            bt_camera.setVisibility(View.GONE);
            bt_camera_caption.setVisibility(View.VISIBLE);
        } else {
            bt_camera.setVisibility(View.VISIBLE);
            bt_camera_caption.setVisibility(View.GONE);
        }

        layout_reporter_info = (LinearLayout) findViewById(R.id.layout_reporter_info);
        layout_reporter_info.setVisibility(View.VISIBLE);
        layout_report.setVisibility(View.GONE);

        et_reporter_name = (EditText) findViewById(R.id.et_reporter_name);
        et_reporter_email = (EditText) findViewById(R.id.et_reporter_email);

        if (obj_user != null) {
            et_reporter_name.setText(obj_user.getName());
            et_reporter_email.setText(obj_user.getEmail());
        } else {
            et_reporter_name.setText(ArgoConfig.getReporterName());
            et_reporter_email.setText(ArgoConfig.getReporterEmail());
        }

        bt_reporter_next = (Button) findViewById(R.id.bt_reporter_next);
        bt_reporter_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedReporterInfoNext();
            }
        });

        tv_header_title = (TextView) this.findViewById(R.id.tv_header_title);
        tv_header_title.setText("Report");

        bt_header_back = (Button) this.findViewById(R.id.bt_header_back);
        bt_header_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_reporter_info.getVisibility() == View.GONE && layout_report.getVisibility() == View.VISIBLE) {
                    layout_report.setVisibility(View.GONE);
                    layout_reporter_info.setVisibility(View.VISIBLE);
                }
                Dialog.exitReportDialog(ReportActivity.this);
            }
        });

        LinearLayout layout_static = (LinearLayout) findViewById(R.id.layout_static);
        layout_static.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
                    Config.hideKeyboard(ReportActivity.this);
                }
                return false;
            }
        });
    }

    void setupRegion() {
        List<Region> list_region = Region.getRegionsByParentId(0);

        ArrayList<RegionObject> list_obj_region = new ArrayList<>();
        ArrayList<String> list_region_name = new ArrayList<>();

        if (!list_region.isEmpty()) {
            list_obj_region.add(new RegionObject());
            list_region_name.add("");
            for (Region region : list_region) {
                if (region != null) {
                    list_obj_region.add(
                            new RegionObject(
                                    region.region_id,
                                    region.name,
                                    region.parnet_id,
                                    region.label_name,
                                    region.order
                            )
                    );
                    list_region_name.add(region.name);
                }
            }

            if (obj_report.getProject().getType().equals("server")) {
                list_region_by_project = Project.getRegions(obj_report.getProjectId());
            } else if (obj_report.getProject().getType().equals("new") && obj_report.getProjectId() != 0) {
                list_region_by_project = LocalProject.getRegions(obj_report.getProjectId());
            } else {
                // Nothing to do.....
                list_region_by_project = new ArrayList<>();
            }

            LinearLayout layout_region_spinner = (LinearLayout) findViewById(R.id.layout_region_spinner);
            layout_region_spinner.setOrientation(LinearLayout.VERTICAL);
            regionSpinnerComponent = new RegionSpinnerComponent(
                    list_obj_region.get(1).getLabel_name(),
                    list_obj_region,
                    layout_region_spinner,
                    list_region_by_project,
                    selected_region_collection,
                    this
            );

            layout_region_spinner.addView(regionSpinnerComponent);
        }
    }

    void setupForm() {
        //-- get form field value
        List<ReportValue> list_value = Report.reportValues(obj_report.getId());

        //-- set form
        List<Form> list_form = Form.forms();
        if (!list_form.isEmpty()) {
            for (Form form : list_form) {
                if (form != null) {
                    //-- set form field
                    ArrayList<FormFieldObject> list_obj_form_field = new ArrayList<>();
                    List<FormField> list_form_field = Form.formFields(form.form_id);
                    if (!list_form_field.isEmpty()) {
                        for (int i = 0; i < list_form_field.size(); i++) {
                            //-- set field
                            Field field = Field.select(list_form_field.get(i).field_id);
                            FieldObject obj_field = new FieldObject();
                            if (field != null) {
                                obj_field.setId(field.field_id);
                                obj_field.setName(field.name);
                            }

                            FormFieldObject obj_form_field = new FormFieldObject(
                                    list_form_field.get(i).form_field_id,
                                    obj_field,
                                    list_form_field.get(i).name,
                                    list_form_field.get(i).default_value,
                                    list_form_field.get(i).options,
                                    "",
                                    list_form_field.get(i).order,
                                    list_form_field.get(i).show_if,
                                    list_form_field.get(i).edit_level_priority,
                                    list_form_field.get(i).is_required,
                                    new ArrayList<ShowIfObject>(),
                                    null,
                                    list_form_field.get(i).formula
                            );
                            list_obj_form_field.add(obj_form_field);

                            //-- set form field edit value
                            if (obj_report.getForm().getId() == form.form_id && !list_value.isEmpty()) {
                                obj_form_field.setValue(list_value.get(i).value);
                            }
                        }
                    }

                    list_obj_form.add(
                            new FormObject(
                                    form.form_id,
                                    form.name,
                                    list_obj_form_field,
                                    form.is_photo_required
                            ));
                    list_form_name.add(form.name);

                    //-- set show if object for form field object
                    for (FormFieldObject formFieldObject : list_obj_form_field) {
                        String showIf = formFieldObject.getShowIf();
                        if (!showIf.isEmpty()) {
                            try {
                                JSONObject jsonObject = new JSONObject(showIf);
                                Iterator<String> iterator = jsonObject.keys();
                                while (iterator.hasNext()) {
                                    String formFieldId = iterator.next();
                                    for (FormFieldObject f : list_obj_form_field) {
                                        if (f.getId() == Integer.parseInt(formFieldId)) {
                                            ShowIfObject showIfObject = new ShowIfObject();
                                            showIfObject.setFormFieldObject(formFieldObject);
                                            showIfObject.setValue(jsonObject.getString(formFieldId));

                                            ArrayList<ShowIfObject> list = new ArrayList<>();
                                            list.add(showIfObject);
                                            f.setShowIfObjects(list);

                                            break;
                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Logger.e(e.toString());
                            }
                        }
                    }
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list_form_name);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_form.setAdapter(adapter);

        //-- set form default select item
        spinner_form.setSelection(list_form_name.indexOf(obj_report.getForm().getName()));
    }

    void refreshForm(int position) {
        layout_form_field.removeAllViews();

        layout_mapview.setVisibility(View.VISIBLE);
        spinner_form.setEnabled(true);
        for (FormFieldObject obj_form_field : list_obj_form.get(position).getListFormField()) {
            createFormFieldView(obj_form_field);
        }
    }

    void createFormFieldView(FormFieldObject obj_form_field) {
        View view = null;

        switch (obj_form_field.getField().getName()) {
            case "text_box":
                view = new TextBoxSubview(this, obj_form_field);
                break;

            case "text_area":
                view = new TextAreaSubview(this, obj_form_field);
                break;

            case "drop_down_list":
                view = new DropDownListSubview(this, obj_form_field);
                break;

            case "check_box":
                view = new CheckBoxSubview(this, obj_form_field);
                break;

            case "radio_button":
                view = new RadioButtonSubview(this, obj_form_field);
                break;

            case "numerical":
                view = new TextNumericalSubview(this, obj_form_field);
                break;

            case "date":
                view = new DatePickerSubview(this, obj_form_field);
                break;

            case "gps_tracker":
                view = new GPSTrackerSubview(this, obj_form_field);
                layout_mapview.setVisibility(View.GONE);
                break;

            case "check_box_group":
                view = new CheckBoxGroupSubview(this, obj_form_field);
                break;

            default:
                break;
        }

        if (view != null) {
            obj_form_field.setView(view);
            layout_form_field.addView(view);

            //-- hide for form_field had formula
            if (obj_form_field.getFormula() != null) {
                view.setVisibility(View.GONE);
                obj_form_field.setIsValidate(true);
                obj_form_field.setIsRequired(false);
            }
            if (!obj_form_field.getShowIf().equals("")) {
                view.setVisibility(View.GONE);

                //-- disable validate and required when layout is invisible
                obj_form_field.setIsValidate(true);
                obj_form_field.setIsRequired(false);
            }
            //-- hide layout if user's edit level lower than form field's edit level
            if (obj_user != null && obj_form_field.getEditLevelPriority() < obj_user.getPermissionPriority()) {
                view.setVisibility(View.GONE);

                //-- disable validate and required when layout is invisible
                obj_form_field.setIsValidate(true);
                obj_form_field.setIsRequired(false);
            }
        }
    }

    void setupLocation() {
        mapView = (MapView) this.findViewById(R.id.mapview);

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                !EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
            Log.w(TAG, "startGPS: No permission to access location data");

            mapView.setVisibility(View.GONE);
            return;
        }

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(13);
        IGeoPoint point = new IGeoPoint() {
            @Override
            public int getLatitudeE6() {
                return 0;
            }

            @Override
            public int getLongitudeE6() {
                return 0;
            }

            @Override
            public double getLatitude() {
                return obj_report.getLatitude();
            }

            @Override
            public double getLongitude() {
                return obj_report.getLongitude();
            }
        };
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);

        overlayItems.add(new OverlayItem("MyLocation", "MyLocation", new GeoPoint(obj_report.getLatitude(), obj_report.getLongitude())));
        ItemizedIconOverlay<OverlayItem> itemItemizedIconOverlay = new ItemizedIconOverlay<>(this, overlayItems, null);
        mapView.getOverlays().add(itemItemizedIconOverlay);
        mapController.setCenter(point);
        mapController.animateTo(point);
        mapView.invalidate();
    }

    void startGPS() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                !EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
            Log.w(TAG, "startGPS: No permission to access location data");

            return;
        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, this);

        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, this);
    }

    void stopGPS(){
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        Log.d(TAG, "onLocationChanged: " + location);

        this.location = location;
        IGeoPoint point = new IGeoPoint() {
            @Override
            public int getLatitudeE6() {
                return 0;
            }

            @Override
            public int getLongitudeE6() {
                return 0;
            }

            @Override
            public double getLatitude() {
                return location.getLatitude();
            }

            @Override
            public double getLongitude() {
                return location.getLongitude();
            }
        };
        mapView.getOverlays().clear();

        mapController.setCenter(point);
        mapView.invalidate();

        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);
        mapView.invalidate();

        obj_report.setLatitude(location.getLatitude());
        et_latitude.setText(String.valueOf(location.getLatitude()));
        et_latitude.clearFocus();

        obj_report.setLongitude(location.getLongitude());
        et_longitude.setText(String.valueOf(location.getLongitude()));
        et_longitude.clearFocus();

        if(tv_map_loading.getVisibility() == View.VISIBLE){
            tv_map_loading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    void cameraOnClick(){
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            Log.w(TAG, "onCreateView: No permission to access camera");

            return;
        }

        if(checkListAttachmentSize()){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(this.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = ImageProcess.createImageFile();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if(photoFile != null) {
                    current_image_path = photoFile.getAbsolutePath();
                    ImageProcess.galleryAddPic(current_image_path, this);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            }
        }
        else{
            Dialog.overMaxAttachmentsSizeDialog(this);
        }
    }

    void albumOnClick(){
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Log.w(TAG, "onCreateView: No permission to access storage");

            return;
        }

        if(checkListAttachmentSize()) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FILE);
        }
        else{
            Dialog.overMaxAttachmentsSizeDialog(this);
        }
    }

    void record_on_click(){
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            Log.w(TAG, "onCreateView: No permission to access camera");

            return;
        }

        if(checkListAttachmentSize()){
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (takeVideoIntent.resolveActivity(this.getPackageManager()) != null) {
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, getResources().getInteger(R.integer.record_limit_second));
                startActivityForResult(takeVideoIntent, REQUEST_RECORD_VIDEO);
            }
        } else {
            Dialog.overMaxAttachmentsSizeDialog(this);
        }
    }

    void camera_on_click(){
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            Log.w(TAG, "onCreateView: No permission to access camera");

            return;
        }

        if(checkListAttachmentSize()){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(this.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = ImageProcess.createImageFile();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if(photoFile != null) {
                    current_image_path = photoFile.getAbsolutePath();
                    ImageProcess.galleryAddPic(current_image_path, this);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(intent, REQUEST_CAMERA_CAPTION);
                }
            }
        }
        else{
            Dialog.overMaxAttachmentsSizeDialog(this);
        }
    }

    boolean checkListAttachmentSize(){
        return attachRecyclerViewAdapter.getAttachmentObjectList().size() < MAX_UPLOAD_ATTACHMENT_SIZE;
    }

    void showAttachment(){
        for (AttachmentObject obj_attachment : obj_report.getListObjAttachment()){
            addAttachmentToList(obj_attachment);
        }
    }

    void clearAttachments() {
        obj_report.setListObjAttachment(new ArrayList<AttachmentObject>());
    }

    void addAttachmentToList(final AttachmentObject obj_attachment){
        attachRecyclerViewAdapter.addItem(obj_attachment);
    }

    boolean checkValidator(){
        boolean check = true;

        for(FormFieldObject obj_form_field : list_obj_form.get(spinner_form.getSelectedItemPosition()).getListFormField()){
            if(obj_form_field.getView().getVisibility() == View.VISIBLE && !obj_form_field.getIsValidate()){
                Toast.makeText(getApplicationContext(), TOAST_SAVE_REPORT_FAIL, Toast.LENGTH_LONG).show();
                check = false;
                break;
            }
        }

        return check;
    }

    boolean checkIsRequiredField(){
        boolean check = true;

        for (FormFieldObject obj_form_field : list_obj_form.get(spinner_form.getSelectedItemPosition()).getListFormField()){
            if(obj_form_field.getView().getVisibility() == View.VISIBLE && obj_form_field.getIsRequired() && (obj_form_field.getValue().isEmpty() || obj_form_field.getValue().equals("[]"))){
                if (!obj_form_field.getField().getName().equals("check_box")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.save_report_fail_with_required_field) , Toast.LENGTH_LONG).show();
                    check = false;
                    break;
                }
            }
        }

        return check;
    }

    boolean checkIsPhotoRequired(){
        boolean check = true;

        if (list_obj_form.get(spinner_form.getSelectedItemPosition()).getIsPhotoRequired() && attachRecyclerViewAdapter.getAttachmentObjectList().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.save_report_fail_with_required_attachment) , Toast.LENGTH_LONG).show();
            check = false;
        }

        return check;
    }

    void clickedReporterInfoNext() {
        if (et_reporter_name.getText().toString().isEmpty() && et_reporter_email.getText().toString().isEmpty()) {
            Toast.makeText(ReportActivity.this, getString(R.string.reporter_info_empty), Toast.LENGTH_LONG).show();
        } else if (Validator.isIncorrentEmail(et_reporter_email.getText().toString())) {
            Toast.makeText(ReportActivity.this, getString(R.string.reporter_email_incorrect), Toast.LENGTH_LONG).show();
        } else {
            layout_reporter_info.setVisibility(View.GONE);
            layout_report.setVisibility(View.VISIBLE);
            obj_report.setReporterName(et_reporter_name.getText().toString());
            obj_report.setReporterEmail(et_reporter_email.getText().toString());
        }
    }

    void clickedSaveReport(){
        SimpleDateFormat sdf = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        String currentDateTime = sdf.format(new Date());

        Project project;
        LocalProject localProject;
        Report report;

        try{
            ActiveAndroid.beginTransaction();

            boolean is_belongs_created_new_project = (obj_report.getProject().getId() == 0);
            boolean is_belongs_exist_project = (Project.hasProject(obj_report.getProjectId()) && obj_report.getProjectType().equals("server"));
            boolean is_belongs_exist_localproject = (LocalProject.hasProject(obj_report.getProjectId()) && obj_report.getProjectType().equals("new"));

            //-- new project without project name
            if (et_project_name.getText().toString().isEmpty() && (et_project_name.getVisibility() == View.VISIBLE)) {
                throw new Exception("Create New Issue without Issue Name");
            }

            if (is_belongs_created_new_project) {
                localProject = createNewLocalProject();
                localProject.project_id = ((int)(long)localProject.getId());
                localProject.parent_id = (parent_project_obj == null) ? 0 : parent_project_obj.getId();
                localProject.container_id = container_obj.getContainerId();
                localProject.save();

                obj_report.setProjectId((int)(long)localProject.getId());
                obj_report.setProjectType("new");

                saveRegionAndDistrict(localProject);
            } else if (is_belongs_exist_localproject) {
                localProject = LocalProject.getProject(obj_report.getProjectId(), "new");
                saveRegionAndDistrict(localProject);
                obj_report.getProject().setTitle(tv_project_name.getText().toString());
            } else if (is_belongs_exist_project) {
                project = Project.selectSingle(obj_report.getProjectId(), "server");
                saveRegionAndDistrict(project);
                obj_report.getProject().setTitle(tv_project_name.getText().toString());
            } else {
                finish();
                throw new Exception("Create New Issue Failed");
            }

            //-- create new report for creating and editing pushed report
            if((obj_report.getPushed() == 0 && obj_report.getId() == 0) || obj_report.getPushed() == 1){
                report = new Report();
            }
            //-- load report
            else{
                report = Report.load(Report.class, obj_report.getId());

                ReportValue.delete(obj_report.getId());
                Attachment.delete(obj_report.getId());
            }

            saveReport(report, currentDateTime);
            saveFormValue(report.getId().intValue());
            saveAttachment(report.getId().intValue(), currentDateTime);
            ArgoConfig.updateReporterEmailAndName(obj_report.getReporterEmail() ,obj_report.getReporterName());

            ActiveAndroid.setTransactionSuccessful();
            handler.sendEmptyMessage(SAVE_REPORT_SUCCEED);
        }
        catch (Exception e){
            e.printStackTrace();
            handler.sendEmptyMessage(SAVE_REPORT_FAILED);
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    LocalProject createNewLocalProject() {
        LocalProject localProject;
        localProject = new LocalProject();
        localProject.title = et_project_name.getText().toString();
        localProject.default_form_id = obj_report.getForm().getId();
        localProject.district_id = obj_report.getDistrict().getId();
        localProject.project_type = "new";
        if (obj_user != null) {
            localProject.created_by = obj_user.getUserId();
        } else {
            localProject.created_by = 0;
        }
        localProject.save();

        return localProject;
    }

    void saveFormValue (int report_id) {
        for(FormFieldObject obj_form_field : obj_report.getForm().getListFormField()){
            ReportValue value = new ReportValue();
            value.report_id = report_id;
            value.form_field_id = obj_form_field.getId();
            value.value = obj_form_field.getValue();
            value.save();
        }
    }

    void saveAttachment (int report_id, String currentDateTime) {
        if(!attachRecyclerViewAdapter.getAttachmentObjectList().isEmpty()){
            for(AttachmentObject obj_attachment : attachRecyclerViewAdapter.getAttachmentObjectList()){
                File check_file = new File(obj_attachment.getPath());
                if(check_file.exists()){
                    Attachment attachment = new Attachment();
                    attachment.name = obj_attachment.getName();
                    attachment.path = obj_attachment.getPath();
                    attachment.type = obj_attachment.getType();
                    attachment.report_id = report_id;
                    attachment.created_at = currentDateTime;
                    attachment.description = obj_attachment.getDescription();
                    attachment.save();
                }
                check_file = null;
            }
        }
    }

    void saveRegionAndDistrict(LocalProject localProject){
        if (!Region.getRegionsByParentId(0).isEmpty()) {
            LocalProject.detach_region(localProject.project_id, localProject.project_type);
            Set regionSets = regionSpinnerComponent.get_selected_region_collection();
            Object[] regionObjects = regionSets.toArray(new Integer[regionSets.size()]);

            for(int i = 0; i < regionObjects.length; i++){
                Log.d("region_id", regionObjects[i].toString());
                LocalProject.attach_region(localProject.project_id, localProject.project_type, Integer.valueOf(regionObjects[i].toString()));
            }
        }
    }

    void saveRegionAndDistrict(Project project){
        if (!Region.getRegionsByParentId(0).isEmpty()) {
            Project.detach_region(project.project_id, project.project_type);
            Set regionSets = regionSpinnerComponent.get_selected_region_collection();
            Object[] regionObjects = regionSets.toArray(new Integer[regionSets.size()]);

            for(int i = 0; i < regionObjects.length; i++){
                Log.d("region_id", regionObjects[i].toString());
                LocalProject.attach_region(project.project_id, project.project_type, Integer.valueOf(regionObjects[i].toString()));
            }
        }
    }

    void saveReport (Report report, String currentDateTime){
        if (obj_user != null) {
            report.created_by = obj_user.getUserId();
        } else {
            report.created_by = 0;
        }
        report.project_id = obj_report.getProjectId();
        report.project_type = obj_report.getProjectType();
        report.form_id = obj_report.getForm().getId();
        report.title = "";
        report.description = obj_report.getDescription();
        report.lat = obj_report.getLatitude();
        report.lng = obj_report.getLongitude();
        report.created_at = currentDateTime;
        report.project_type = obj_report.getProject().getType();
        report.pushed = 0;
        report.reporter_name = obj_report.getReporterName();
        report.reporter_email = obj_report.getReporterEmail();
        report.save();
    }

    // Observer method
    @Override
    public void update(Observable background, Object service){
        Log.d(TAG, "Service connecting ...");
        if (service.getClass().getSimpleName().equals("BackgroundRunnerService")) {
            Log.d(TAG, "Service connected success");
            backgroundRunnerService = (BackgroundRunnerService) service;
            backgroundRunnerService.setCurrentUser(obj_user);
        } else {
            Log.e(TAG,"Service connected fail");
        }
    }
}
