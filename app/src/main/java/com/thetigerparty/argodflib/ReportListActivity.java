package com.thetigerparty.argodflib;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.thetigerparty.argodflib.HelperClass.Config;
import com.thetigerparty.argodflib.Model.Attachment;
import com.thetigerparty.argodflib.Model.District;
import com.thetigerparty.argodflib.Model.Form;
import com.thetigerparty.argodflib.Model.Region;
import com.thetigerparty.argodflib.Model.Report;
import com.thetigerparty.argodflib.Object.AttachmentObject;
import com.thetigerparty.argodflib.Object.DistrictObject;
import com.thetigerparty.argodflib.Object.FormFieldObject;
import com.thetigerparty.argodflib.Object.FormObject;
import com.thetigerparty.argodflib.Object.ProjectObject;
import com.thetigerparty.argodflib.Object.RegionObject;
import com.thetigerparty.argodflib.Object.ReportObject;
import com.thetigerparty.argodflib.Object.UserObject;
import com.thetigerparty.argodflib.Service.BackgroundRunnerProvider;
import com.thetigerparty.argodflib.Service.BackgroundRunnerService;
import com.thetigerparty.argodflib.Subclass.ReportList.ReportListViewAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by ttpttp on 2015/8/14.
 */
public class ReportListActivity extends Activity implements Observer{
    TextView tv_project_name;
    TextView tv_header_title;

    EditText et_search;

    Button bt_search;
    Button bt_back;
    Button bt_header_back;

    ListView lv_report;
    ReportListViewAdapter rlva;

    List<ReportObject> list_obj_report = new ArrayList<>();

    MsgHandler handler = new MsgHandler(this);

    UserObject obj_user = null;
    ProjectObject obj_project = new ProjectObject();

    BackgroundRunnerService backgroundRunnerService;
    BackgroundRunnerProvider backgroundRunnerProvider;
    BackgroundRunnerProvider.BackgroundRunnerServiceConnection backgroundRunnerServiceConnection;

    final static int REFRESH_LIST_VIEW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_list_activity);

        getIntentValue();
        findView();

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
        return super.onCreateOptionsMenu(menu);
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
    protected void onResume() {
        super.onResume();

        showReportList();
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

        tv_project_name = null;
        bt_search = null;
        bt_back = null;
        et_search = null;
        handler = null;
        lv_report = null;
        rlva = null;
        list_obj_report = null;
        obj_project = null;
        obj_user = null;

        backgroundRunnerProvider = null;
        backgroundRunnerServiceConnection = null;
        backgroundRunnerService = null;

        Runtime.getRuntime().gc();
    }

    static class MsgHandler extends Handler {
        WeakReference<ReportListActivity> reportListActivity;
        MsgHandler(ReportListActivity activity){
            reportListActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg){
            ReportListActivity activity = reportListActivity.get();
            switch(msg.what)
            {
                case REFRESH_LIST_VIEW:
                    activity.showReportList();
                    break;
            }
        }
    }

    void getIntentValue(){
        if(getIntent().getExtras() != null) {
            obj_project = (ProjectObject) getIntent().getExtras().getSerializable("obj_project");
            backgroundRunnerProvider = (BackgroundRunnerProvider) getIntent().getExtras().getSerializable("background_runner");
            obj_user = (UserObject) getIntent().getExtras().getSerializable("obj_user");
        }
    }

    void findView(){
        tv_project_name = (TextView) this.findViewById(R.id.tv_project_name);
        tv_project_name.setText(obj_project.getTitle());

        et_search = (EditText) this.findViewById(R.id.et_search);

        bt_search = (Button) this.findViewById(R.id.bt_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(REFRESH_LIST_VIEW);
            }
        });

        lv_report = (ListView) this.findViewById(R.id.lv_report);

        bt_header_back = (Button) this.findViewById(R.id.bt_header_back);
        bt_header_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_header_title = (TextView) this.findViewById(R.id.tv_header_title);
        tv_header_title.setText("Report");
    }

    void showReportList(){
        list_obj_report.clear();

        List<Report> list_report = Report.reports(obj_project.getId(), obj_project.getType(), et_search.getText().toString());

        if(!list_report.isEmpty()){
            for (Report report : list_report){
                if(report != null){
                    ReportObject obj_report = new ReportObject();
                    obj_report.setId(report.getId().intValue());
                    obj_report.setTitle(report.title);
                    obj_report.setCreatedAt(report.created_at);
                    obj_report.setPushed(report.pushed);
                    list_obj_report.add(obj_report);
                }
            }
        }

        rlva = new ReportListViewAdapter(this, list_obj_report);
        lv_report.setAdapter(rlva);

        Config.hideKeyboard(this);
    }

    public void editOnClick(ReportObject obj_report){
        Report report = Report.select(obj_report.getId());
        if(report != null){
            obj_report.setProjectId(report.project_id);
            obj_report.setProjectType(report.project_type);
            //-- set region and district object
            RegionObject obj_region = new RegionObject();
            Region region;

            DistrictObject obj_district = new DistrictObject();
            District district = District.selectSingle(obj_project.getDistrictId());
            if(district != null){
                obj_district.setId(district.district_id);
                obj_district.setRegionId(district.region_id);
                obj_district.setName(district.name);

                region = Region.selectSingle(district.region_id);
                if(region != null){
                    obj_region.setId(region.region_id);
                    obj_region.setName(region.name);
                }
            }
            obj_report.setDistrict(obj_district);
            obj_report.setRegion(obj_region);

            //-- set form object
            Form form = Form.selectSingle(report.form_id);
            String form_name = "";
            if(form != null){
                form_name = form.name;
            }
            obj_report.setForm(
                    new FormObject(
                            report.form_id,
                            form_name,
                            new ArrayList<FormFieldObject>(),
                            form.is_photo_required
                    )
            );

            //-- set attachment object
            List<AttachmentObject> list_obj_attachment = new ArrayList<>();
            List<Attachment> list_attachment = Report.attachments(obj_report.getId());
            if(!list_attachment.isEmpty()){
                for (Attachment attachment : list_attachment){
                    if(attachment != null){
                        list_obj_attachment.add(new AttachmentObject(attachment.getId().intValue(),
                                                attachment.name,
                                                attachment.path,
                                                attachment.type,
                                                report.getId().intValue(),
                                                attachment.description
                                ));
                    }
                }
            }
            obj_report.setListObjAttachment(list_obj_attachment);

            //-- set other attributes
            obj_report.setProject(obj_project);
            obj_report.setDescription(report.description);
            obj_report.setLatitude(report.lat);
            obj_report.setLongitude(report.lng);
            obj_report.setCreatedBy(report.created_by);
            obj_report.setReporterName(report.reporter_name);
            obj_report.setReporterEmail(report.reporter_email);
        }

        Intent intent = new Intent(ReportListActivity.this, ReportActivity.class);
        intent.putExtra("obj_report", obj_report);
        intent.putExtra("background_runner", backgroundRunnerProvider);
        if (obj_user != null) {
            intent.putExtra("obj_user", obj_user);
        }
        startActivity(intent);
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
}
