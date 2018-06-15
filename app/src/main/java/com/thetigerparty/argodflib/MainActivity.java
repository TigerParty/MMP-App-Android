package com.thetigerparty.argodflib;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.orhanobut.logger.Logger;
import com.thetigerparty.argodflib.HelperClass.Dialog;
import com.thetigerparty.argodflib.HelperClass.HttpProcess;
import com.thetigerparty.argodflib.HelperClass.JsonArrayHelper;
import com.thetigerparty.argodflib.Model.Attachment;
import com.thetigerparty.argodflib.Model.Form;
import com.thetigerparty.argodflib.Model.LocalProject;
import com.thetigerparty.argodflib.Model.Project;
import com.thetigerparty.argodflib.Model.Region;
import com.thetigerparty.argodflib.Model.Report;
import com.thetigerparty.argodflib.Model.ReportValue;
import com.thetigerparty.argodflib.Model.Tracker;
import com.thetigerparty.argodflib.Object.UserObject;
import com.thetigerparty.argodflib.Service.BackgroundRunnerProvider;
import com.thetigerparty.argodflib.Service.BackgroundRunnerService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends Activity implements Observer{
    final static String TAG = "MainActivity";

    final static int SUBMIT_FINISHED = 1;
    final static int SUBMIT_FAILED = 2;
    final static int CHECK_INTERNET_FALSE = 3;
    final static int NO_REPORT_SUBMIT = 4;
    final static int OVER_SMS_LIMIT = 5;
    final static int SUBMIT_SUCCESS = 6;
    final static int SEND_SMS_CONFIRM = 7;
    final static int SUBMIT_SMS_CANCEL = 8;
    final static int SUBMIT_SMS_FINISHED = 9;
    final static int SUBMIT_FINISHED_TRACKER = 10;
    ProgressDialog pd;
    Button bt_new;
    Button bt_submit;
    Button bt_logout;
    Button bt_new_tracker;
    List<Report> list_not_pushed_report;
    List<Tracker> list_not_pushed_tracker;
    UserObject obj_user = null;
    MsgHandler handler = new MsgHandler(this);
    String SUBMIT_REPORT_API;
    Integer nMsgParts;
    String SMSErrorMsg = "";

    BackgroundRunnerService backgroundRunnerService;
    BackgroundRunnerProvider backgroundRunnerProvider;
    BackgroundRunnerProvider.BackgroundRunnerServiceConnection backgroundRunnerServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        getIntentValue();
        getResourceValue();
        findView();

        backgroundRunnerProvider = new BackgroundRunnerProvider();
        backgroundRunnerProvider.add_observer(this);
        this.backgroundRunnerServiceConnection = backgroundRunnerProvider.getBackgroundRunnerServiceConnection();
    }

    @Override
    protected void onStart(){
        super.onStart();
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
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Dialog.logoutDialog(this);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop(){
        super.onStop();
        unbindService(backgroundRunnerServiceConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundRunnerProvider.deleteObserver(this);

        pd = null;
        bt_new = null;
        bt_submit = null;
        bt_logout = null;
        list_not_pushed_report = null;
        obj_user = null;
        handler = null;

        backgroundRunnerProvider = null;
        backgroundRunnerServiceConnection = null;
        backgroundRunnerService = null;

        Runtime.getRuntime().gc();
    }

    void getIntentValue(){
        if(getIntent().getExtras() != null) {
            obj_user = (UserObject) getIntent().getExtras().getSerializable("obj_user");
        }
    }

    void getResourceValue(){
        SUBMIT_REPORT_API = BuildConfig.SUBMIT_DATA_API;
    }

    void findView(){
        bt_new = (Button) this.findViewById(R.id.bt_new);
        bt_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RootProjectListActivity.class);
                intent.putExtra("background_runner", backgroundRunnerProvider);
                intent.putExtra("obj_user", obj_user);
                startActivity(intent);
            }
        });

        bt_submit = (Button) this.findViewById(R.id.bt_submit);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_onClick();
            }
        });

        bt_logout = (Button) this.findViewById(R.id.bt_logout);
        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.logoutDialog(MainActivity.this);
            }
        });

        bt_new_tracker = (Button) this.findViewById(R.id.bt_tracker);
        bt_new_tracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TrackerActivity.class);
                intent.putExtra("obj_user", obj_user);
                startActivity(intent);
            }
        });
    }

    private JSONArray processReport(Report report) {
        JSONArray jsArrayReport = new JSONArray();
        JSONObject jsObjReport = new JSONObject();
        JSONArray jsArrayFieldValue = new JSONArray();
        JSONArray jsArrayRegion = new JSONArray();
        JSONArray jsArrayAttachment = new JSONArray();

        //-- make json with report
        try{
            if(LocalProject.hasProject(report.project_id) && report.project_type.equals("new")){
                LocalProject localProject = LocalProject.getProject(report.project_id, "new");
                jsObjReport.put("project_title",localProject.title);
                jsObjReport.put("project_id",localProject.project_id);
                jsObjReport.put("project_type", localProject.project_type);
                jsObjReport.put("district_id", localProject.district_id);
                jsObjReport.put("parent_id", (localProject.parent_id == 0 ? JSONObject.NULL : localProject.parent_id));
                jsObjReport.put("container_id", localProject.container_id);

                List<Region> list_region =  LocalProject.getRegions(report.project_id);
                for (Region region : list_region) {
                    JSONObject jsObjRegion = new JSONObject();
                    jsObjRegion.put("id", region.region_id);
                    jsArrayRegion.put(jsObjRegion);
                }
            }
            else if (Project.hasProject(report.project_id) && report.project_type.equals("server")){
                Project project = Project.selectSingle(report.project_id, "server");
                jsObjReport.put("project_title", project.title);
                jsObjReport.put("project_id",project.project_id);
                jsObjReport.put("project_type", project.project_type);
                jsObjReport.put("district_id", project.district_id);
                jsObjReport.put("parent_id", (project.parent_id == 0 ? JSONObject.NULL : project.parent_id));
                jsObjReport.put("container_id", project.container_id);

                List<Region> list_region = Project.getRegions(report.project_id);
                for (Region region : list_region) {
                    JSONObject jsObjRegion = new JSONObject();
                    jsObjRegion.put("id", region.region_id);
                    jsArrayRegion.put(jsObjRegion);
                }
            }
            else {
                // got illegal report, the report will be assigned to project 0 by api
                jsObjReport.put("project_type", "illegal");
            }

            jsObjReport.put("form_id", report.form_id);
            jsObjReport.put("title", report.title);
            jsObjReport.put("description", report.description);
            jsObjReport.put("lat", report.lat);
            jsObjReport.put("lng", report.lng);
            jsObjReport.put("created_by", (report.created_by == 0 ? JSONObject.NULL : report.created_by));
            jsObjReport.put("created_at", report.created_at);
            jsObjReport.put("version", BuildConfig.VERSION_NAME);
            jsObjReport.put("region", jsArrayRegion);

            List<ReportValue> list_value = Report.reportValues(report.getId().intValue());
            for(ReportValue value : list_value){
                JSONObject jsObjValue = new JSONObject();
                jsObjValue.put("form_field_id", value.form_field_id);
                jsObjValue.put("value", value.value);
                jsArrayFieldValue.put(jsObjValue);
            }
            List<Attachment> list_attachment = Report.attachments(report.getId().intValue());
            for (Attachment attach : list_attachment) {
                JSONObject jsObjAttach = new JSONObject();
                jsObjAttach.put("description", attach.description);
                jsObjAttach.put("file_name", attach.name);
                jsArrayAttachment.put(jsObjAttach);
            }

            jsObjReport.put("fields", jsArrayFieldValue);
            jsObjReport.put("attachment", jsArrayAttachment);
            jsObjReport.put("reporter_name", report.reporter_name);
            jsObjReport.put("reporter_email", report.reporter_email);

            jsArrayReport.put(jsObjReport);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return jsArrayReport;
    }

    private JSONObject package_report_to_json(Report report) {
        JSONObject jsonObjectReport = new JSONObject();
        JSONArray jsonArrayFieldValue = new JSONArray();
        JSONArray jsArrayRegion = new JSONArray();

        try {
            // -- process local project
            if(LocalProject.hasProject(report.project_id) && report.project_type.equals("new")){
                LocalProject localProject = LocalProject.getProject(report.project_id);
                jsonObjectReport.put("project_title",localProject.title);
                jsonObjectReport.put("project_id",localProject.project_id);
                jsonObjectReport.put("project_type", localProject.project_type);
                jsonObjectReport.put("district_id", localProject.district_id);
                jsonObjectReport.put("parent_id", (localProject.parent_id == 0 ? JSONObject.NULL : localProject.parent_id));
                jsonObjectReport.put("container_id", localProject.container_id);

                List<Region> list_region =  LocalProject.getRegions(report.project_id);
                for (Region region : list_region) {
                    JSONObject jsObjRegion = new JSONObject();
                    jsObjRegion.put("id", region.region_id);
                    jsArrayRegion.put(jsObjRegion);
                }
            }
            // -- project server project
            else if (Project.hasProject(report.project_id) && report.project_type.equals("server")) {
                Project project = Project.selectSingle(report.project_id, report.project_type);
                jsonObjectReport.put("project_title", project.title);
                jsonObjectReport.put("project_id",project.project_id);
                jsonObjectReport.put("project_type", project.project_type);
                jsonObjectReport.put("district_id", project.district_id);
                jsonObjectReport.put("parent_id", (project.parent_id == 0 ? JSONObject.NULL : project.parent_id));
                jsonObjectReport.put("container_id", project.container_id);

                List<Region> list_region = Project.getRegions(report.project_id);
                for (Region region : list_region) {
                    JSONObject jsObjRegion = new JSONObject();
                    jsObjRegion.put("id", region.region_id);
                    jsArrayRegion.put(jsObjRegion);
                }
            }
            else {
                // got illegal report, the report will be assigned to project 0 by api
                jsonObjectReport.put("project_type", "illegal");
            }

            List<ReportValue> list_value = Report.reportValues(report.getId().intValue());
            for(ReportValue value : list_value){
                JSONObject jsObjValue = new JSONObject();
                jsObjValue.put("form_field_id", value.form_field_id);
                jsObjValue.put("value", value.value);
                jsonArrayFieldValue.put(jsObjValue);
            }

            jsonObjectReport.put("fields", jsonArrayFieldValue);
            jsonObjectReport.put("form_id", report.form_id);
            jsonObjectReport.put("title", report.title);
            jsonObjectReport.put("description", report.description);
            jsonObjectReport.put("lat", report.lat);
            jsonObjectReport.put("lng", report.lng);
            jsonObjectReport.put("created_by", (report.created_by == 0 ? JSONObject.NULL : report.created_by));
            jsonObjectReport.put("created_at", report.created_at);
            jsonObjectReport.put("version", BuildConfig.VERSION_NAME);
            jsonObjectReport.put("region", jsArrayRegion);
            jsonObjectReport.put("reporter_name", report.reporter_name);
            jsonObjectReport.put("reporter_email", report.reporter_email);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("package report", e.toString());
        }

        return jsonObjectReport;
    }

    private JSONArray packageTracker(Tracker tracker) {
        JSONArray jsArrTracker = new JSONArray();
        JSONArray jsArrAttachmentByTracker = new JSONArray();
        JSONObject jsObjTracker = new JSONObject();

        try {
            // package tracker to json
            jsObjTracker.put("tracker_id", tracker.getId());
            jsObjTracker.put("path", tracker.path);
            jsObjTracker.put("created_at", tracker.created_at);
            jsObjTracker.put("created_by", tracker.created_by != 0 ? tracker.created_by : JSONObject.NULL);
            jsObjTracker.put("title", tracker.title);

            // package attachment to json
            List<Attachment> listAttachment= Tracker.getAttachments(tracker.getId().intValue());
            for (Attachment attachment : listAttachment) {
                JSONObject jsonObjAttachment = new JSONObject();
                jsonObjAttachment.put("file_name", attachment.name);
                jsonObjAttachment.put("description", attachment.description);
                jsArrAttachmentByTracker.put(jsonObjAttachment);
            }
            jsObjTracker.put("attachment", jsArrAttachmentByTracker);
            jsArrTracker.put(jsObjTracker);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

        return jsArrTracker;
    }

    public static class MsgHandler extends Handler {
        WeakReference<MainActivity> mainActivity;
        MsgHandler(MainActivity activity){
            mainActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg){
            MainActivity activity = mainActivity.get();
            switch(msg.what) {
                case SUBMIT_FINISHED:
                    Dialog.submitResultDialog(activity, activity.list_not_pushed_report);
                    activity.pd.dismiss();
                    break;
                case SUBMIT_FINISHED_TRACKER:
                    Dialog.submitResultTrackerDialog(activity,  activity.list_not_pushed_tracker);
                    activity.pd.dismiss();
                    break;
                case SUBMIT_SMS_FINISHED:
                    Dialog.submitResultDialog(activity, activity.list_not_pushed_report, "SMS");
                    activity.pd.dismiss();
                    break;
                case SUBMIT_SUCCESS:
                    Toast.makeText(activity, activity.getString(R.string.submit_success), Toast.LENGTH_LONG).show();
                    activity.pd.dismiss();
                    break;

                case SUBMIT_FAILED:
                    Toast.makeText(activity, activity.getString(R.string.submit_fail), Toast.LENGTH_LONG).show();
                    activity.pd.dismiss();
                    break;

                case CHECK_INTERNET_FALSE:
                    Toast.makeText(activity, activity.getString(R.string.check_internet_false), Toast.LENGTH_LONG).show();
                    activity.pd.dismiss();
                    break;

                case NO_REPORT_SUBMIT:
                    Toast.makeText(activity, activity.getString(R.string.no_report_submit), Toast.LENGTH_LONG).show();
                    activity.pd.dismiss();
                    break;
                case OVER_SMS_LIMIT:
                    Dialog.submitSMSFailedDialog(activity);
                    activity.pd.dismiss();
                    break;
                case SEND_SMS_CONFIRM:
                    Dialog.submitSMSConfirmDialog(activity);
                    break;
                case SUBMIT_SMS_CANCEL:
                    Toast.makeText(activity, activity.getString(R.string.sms_cancel) , Toast.LENGTH_LONG).show();
                    activity.pd.dismiss();
                    break;
            }
        }
    }

    private boolean is_connected_internet() {

        try {
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiInfo.isConnectedOrConnecting()) {
                return true;
            }
            else if (mobileInfo.isConnected()) {
                return true;
            }
            else {
                return false;
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean is_sim_card_status_available(){
        TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();

        if (simState == TelephonyManager.SIM_STATE_READY) {
            return true;
        }
        else if (simState == TelephonyManager.SIM_STATE_ABSENT) {
            return false;
        }
        else if (simState == TelephonyManager.SIM_STATE_NETWORK_LOCKED) {
            return false;
        }
        else if (simState == TelephonyManager.SIM_STATE_PIN_REQUIRED) {
            return false;
        }
        else if (simState == TelephonyManager.SIM_STATE_PUK_REQUIRED) {
            return false;
        }
        else if (simState == TelephonyManager.SIM_STATE_UNKNOWN) {
            return false;
        }
        else if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            return false;
        }
        else {
            return false;
        }
    }

    private void submit_via_http() {
        boolean http_failed = false;
        boolean hasTracker = false;
        boolean hasReport = false;

        for(Report report_obj : list_not_pushed_report){
            if (report_obj == null){
                continue;
            }

            List<Attachment> list_attachment = Report.attachments(report_obj.getId().intValue());
            JSONArray report = processReport(report_obj);

            try {
                String response = HttpProcess.httpPost(SUBMIT_REPORT_API, report.toString(), list_attachment, report_obj);
                if(!HttpProcess.getResult(response).equals("success")){
                    throw new Exception("HTTP Response Error");
                }

                update_local_storage(new JSONObject(response), report_obj);
                hasReport = true;
            } catch (Exception e){
                http_failed = true;
            }
        }

        for (Tracker tracker : list_not_pushed_tracker) {
            if (tracker == null) {
                continue;
            }

            List<Attachment> list_attachement_by_tracker = Tracker.getAttachments(tracker.getId().intValue());
            JSONArray jsonTracker = packageTracker(tracker);

            try {
                String response = HttpProcess.httpPost(SUBMIT_REPORT_API, jsonTracker.toString(), list_attachement_by_tracker);
                if (!HttpProcess.getResult(response).equals("success")) {
                    throw new Exception("HTTP Response Error");
                }

                update_local_storage(tracker);
                hasTracker = true;
            } catch (Exception e) {
                http_failed = true;
            }
        }

        if(http_failed){
            handler.sendEmptyMessage(SUBMIT_FAILED);
        } else {
            handler.sendEmptyMessage(SUBMIT_SUCCESS);
        }

        if (hasReport) {
            handler.sendEmptyMessage(SUBMIT_FINISHED);
        }
        if (hasTracker) {
            handler.sendEmptyMessage(SUBMIT_FINISHED_TRACKER);
        }
    }

    private void submit_via_sms() {
        String SENT = "SMS_SENT";
        JSONArray report_list_now = new JSONArray();
        JSONArray submit_report_list = new JSONArray();
        boolean has_failed = false;

        ActiveAndroid.beginTransaction();
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if ((!getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) || (tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT)) {
                throw new Exception("SMS Not Support");
            }

            for (Report report : list_not_pushed_report) {
                Form report_form = Form.selectSingle(report.form_id);
                if (!report_form.is_photo_required) {
                    JSONObject report_obj = package_report_to_json(report);
                    report_list_now.put(report_obj);

                    if (base64_encode(report_list_now).length() < getResources().getInteger(R.integer.submit_via_sms_length)) {
                        submit_report_list.put(report_obj);
                        report.pushed = 1;
                        report.save();
                    } else {
                        has_failed = true;
                        report_list_now = JsonArrayHelper.pop(report_list_now);
                    }

                } else {
                    has_failed = true;
                }
            }

            report_list_now = null;

            if (submit_report_list.length() > 0) {
                //-- set json with type and encode text by base64
                String base64 = base64_encode(submit_report_list);

                //-- setup SMS
                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> msgparts = smsManager.divideMessage(base64);
                nMsgParts = msgparts.size();
                ArrayList<PendingIntent> pendingIntents = new ArrayList(msgparts.size());

                for (int i = 0; i < msgparts.size(); i++) {
                    Intent sentIntent = new Intent(SENT);
                    pendingIntents.add(PendingIntent.getBroadcast(this, 0, sentIntent, 0));
                }

                for (int i = 0; i < submit_report_list.length(); i++) {
                    SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.sdf_ymd_hms));
                    String currentDateTime = sdf.format(new Date());

                    JSONObject json_report = submit_report_list.getJSONObject(i);
                    if (LocalProject.getProject(json_report.getInt("project_id")) != null) {
                        LocalProject localProject = LocalProject.getProject(json_report.getInt("project_id"));
                        localProject.deleted_at = currentDateTime;
                        localProject.save();
                    }
                }

                smsManager.sendMultipartTextMessage(BuildConfig.SMS_NUMBER, null, msgparts, pendingIntents, null);
            }

            if (has_failed){
                handler.sendEmptyMessage(OVER_SMS_LIMIT);
            } else {
                handler.sendEmptyMessage(SUBMIT_SUCCESS);
            }

            handler.sendEmptyMessage(SUBMIT_SMS_FINISHED);

            ActiveAndroid.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("submit via sms", e.toString());
            handler.sendEmptyMessage(OVER_SMS_LIMIT);
            handler.sendEmptyMessage(SUBMIT_SMS_FINISHED);
            handler.sendEmptyMessage(CHECK_INTERNET_FALSE);
        } finally {
            ActiveAndroid.endTransaction();
        }

        //-- register receiver for SMS
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) throws RuntimeException {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        SMSErrorMsg = "Generic failure";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        SMSErrorMsg = "No service";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        SMSErrorMsg = "Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        SMSErrorMsg = "Radio off";
                        break;
                }

                nMsgParts--;

                //-- check SMS status in last message
                if (nMsgParts <= 0) {
                    try {
                        if (!SMSErrorMsg.equals("")) {
                            throw new Exception(SMSErrorMsg);
                        }
                    }
                    catch (Exception e)
                    {
                        for (Report report_obj : list_not_pushed_report) {
                            report_obj.pushed = 0;
                            report_obj.save();
                        }

                        SMSErrorMsg = "";
                        handler.sendEmptyMessage(SUBMIT_FAILED);
                    }
                    finally
                    {
                        context.unregisterReceiver(this);
                    }
                }
            }
        }, new IntentFilter(SENT));
    }

    private void submit_onClick(){
        list_not_pushed_report = Report.reportsNotPushed();
        list_not_pushed_tracker = Tracker.getNotPushedTracker();
        pd = ProgressDialog.show(this, "", getString(R.string.submitting_report), true);
        if (list_not_pushed_report.isEmpty() && list_not_pushed_tracker.isEmpty()) {
            handler.sendEmptyMessage(NO_REPORT_SUBMIT);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(is_connected_internet()){
                    submit_via_http();
                } else {
                    if (BuildConfig.SMS_ENABLE) {
                        handler.sendEmptyMessage(SEND_SMS_CONFIRM);
                    } else {
                        handler.sendEmptyMessage(CHECK_INTERNET_FALSE);
                        handler.sendEmptyMessage(SUBMIT_FAILED);
                    }
                }
            }
        }).start();
    }

    private void update_local_storage(JSONObject response, Report report) {
        try {
            ActiveAndroid.beginTransaction();

            if(response.has("server_project_id")) {
                LocalProject localProject = LocalProject.getProject(report.project_id);

                Project project = new Project();
                project.project_id = response.getInt("server_project_id");
                project.project_type = "server";
                project.title = localProject.title;
                project.description = localProject.description;
                project.default_form_id = localProject.default_form_id;
                project.created_by = localProject.created_by;
                project.district_id = localProject.district_id;
                project.container_id = localProject.container_id;
                project.parent_id = localProject.parent_id;
                project.save();

                List<Region> regions = LocalProject.getRegions(localProject.project_id);
                LocalProject.detach_region(localProject.project_id, localProject.project_type);
                for(Region region : regions){
                    Project.attach_region(project.project_id, project.project_type, region.region_id);
                }

                localProject = null;

                List<Report> list_Reports_by_localproject = LocalProject.listReportsByLocalprojectId(report.project_id, "new");
                LocalProject.deleteProjectById(report.project_id);

                for(Report report_by_localproject : list_Reports_by_localproject){
                    report_by_localproject.project_type = "server";
                    report_by_localproject.project_id = response.getInt("server_project_id");
                    report_by_localproject.save();
                }

                int report_project_id = report.project_id;
                for (Report not_pushed_report : list_not_pushed_report){
                    if((not_pushed_report.project_id == report_project_id) && (not_pushed_report.project_type.equals("new"))){
                        not_pushed_report.project_id = response.getInt("server_project_id");
                        not_pushed_report.project_type = "server";
                        not_pushed_report.save();
                    }
                }
            }
            report.project_type = "server";
            report.pushed = 1;
            report.save();

            ActiveAndroid.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Save report", e.toString());
            e.printStackTrace();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private void update_local_storage(Tracker trcker) {
        try {
            ActiveAndroid.beginTransaction();

            trcker.pushed = 1;
            trcker.save();

            ActiveAndroid.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private String base64_encode(JSONArray reportArray){
        String base64_array = "";

        try {
            final byte[] data = reportArray.toString().getBytes("UTF-8");
            base64_array = Base64.encodeToString(data, Base64.DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("transform base64", e.toString());
        }

        return base64_array;
    }

    // Observer method
    @Override
    public void update(Observable background, Object service){
        Logger.d("Service connecting ...");
        if (service.getClass().getSimpleName().equals("BackgroundRunnerService")) {
            Logger.d("Service connected success");
            backgroundRunnerService = (BackgroundRunnerService) service;
            backgroundRunnerService.setCurrentUser(obj_user);
        } else {
            Logger.e("Service connected fail");
        }
    }

    public void useSMS(){
        //-- check has sms capabilities
        if(is_sim_card_status_available()){
            submit_via_sms();
        } else {
            handler.sendEmptyMessage(OVER_SMS_LIMIT);
            handler.sendEmptyMessage(SUBMIT_FINISHED);
            handler.sendEmptyMessage(CHECK_INTERNET_FALSE);
            Log.e("submit via sms","SMS function Not Support");
        }
    }

    public void cancelSMS() {
        handler.sendEmptyMessage(SUBMIT_SMS_CANCEL);
    }
}