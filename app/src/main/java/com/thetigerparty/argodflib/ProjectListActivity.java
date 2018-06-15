package com.thetigerparty.argodflib;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.thetigerparty.argodflib.HelperClass.Config;
import com.thetigerparty.argodflib.Model.District;
import com.thetigerparty.argodflib.Model.Form;
import com.thetigerparty.argodflib.Model.LocalProject;
import com.thetigerparty.argodflib.Model.Project;
import com.thetigerparty.argodflib.Model.Region;
import com.thetigerparty.argodflib.Object.DistrictObject;
import com.thetigerparty.argodflib.Object.FormObject;
import com.thetigerparty.argodflib.Object.ProjectObject;
import com.thetigerparty.argodflib.Object.RegionObject;
import com.thetigerparty.argodflib.Object.ReportObject;
import com.thetigerparty.argodflib.Object.UserObject;
import com.thetigerparty.argodflib.Service.BackgroundRunnerProvider;
import com.thetigerparty.argodflib.Service.BackgroundRunnerService;
import com.thetigerparty.argodflib.Subclass.ProjectList.ProjectListViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by ttpttp on 2015/8/14.
 */
public class ProjectListActivity extends Activity implements Observer{

    String TOAST_SEARCH_NO_RESULT;

    AutoCompleteTextView actv_search;

    Button bt_create_new_project;
    Button bt_search;
    Button bt_back;
    Button bt_header_back;

    TextView tv_header_title;

    ListView lv_project;
    ProjectListViewAdapter va_pl;

    List<String> list_region_district_name = new ArrayList<>();
    List<ProjectObject> list_obj_project;

    UserObject obj_user = null;

    BackgroundRunnerService backgroundRunnerService;
    BackgroundRunnerProvider backgroundRunnerProvider;
    BackgroundRunnerProvider.BackgroundRunnerServiceConnection backgroundRunnerServiceConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_list_activity);

        getIntentValue();
        findView();

        setupSearchAutoComplete();

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

        showProjectList();
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

        bt_create_new_project = null;
        bt_search = null;
        lv_project = null;
        va_pl = null;
        list_obj_project = null;
        obj_user = null;

        bt_header_back = null;
        tv_header_title = null;

        backgroundRunnerProvider = null;
        backgroundRunnerServiceConnection = null;
        backgroundRunnerService = null;

        Runtime.getRuntime().gc();
    }

    void getIntentValue(){
        if(getIntent().getExtras() != null) {
            obj_user = (UserObject) getIntent().getExtras().getSerializable("obj_user");
            backgroundRunnerProvider = (BackgroundRunnerProvider) getIntent().getExtras().getSerializable("background_runner");
        }
    }

    void findView(){
        actv_search = (AutoCompleteTextView) this.findViewById(R.id.actv_search);
        actv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(list_region_district_name.get(position));
                showProjectList();
            }
        });

        bt_create_new_project = (Button) this.findViewById(R.id.bt_create_new_project);
        bt_create_new_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewProjectOnClick();
            }
        });

        bt_search = (Button) this.findViewById(R.id.bt_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProjectList();
            }
        });

        lv_project = (ListView) this.findViewById(R.id.lv_report);

        TOAST_SEARCH_NO_RESULT = getString(R.string.toast_search_no_result);

        tv_header_title = (TextView)findViewById(R.id.tv_header_title);
        tv_header_title.setText("Report");

        bt_header_back = (Button)this.findViewById(R.id.bt_header_back);
        bt_header_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void setupSearchAutoComplete(){
        List<District> list_district_name = District.getAllDistrictName();
        for (District district : list_district_name){
            list_region_district_name.add(district.name);
        }
        List<Region> list_region_name = Region.getAllRegionName();
        for (Region region : list_region_name){
            list_region_district_name.add(region.name);
        }
        List<Project> list_project_title = Project.getAllProjectTitle();
        for (Project project : list_project_title){
            list_region_district_name.add(project.title);
        }

        ArrayAdapter<String> actv_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, list_region_district_name);
        actv_search.setAdapter(actv_adapter);
    }

    void showProjectList(){
        list_obj_project = new ArrayList<>();

        List<Project> list_project = Project.projects(actv_search.getText().toString());
        if(list_project.size() > 0) {
            for (Project project : list_project){
                if(project != null){
                    ProjectObject obj_project = new ProjectObject(
                            project.project_id,
                            project.project_type,
                            project.title,
                            project.description,
                            project.default_form_id,
                            project.district_id,
                            project.created_by,
                            project.created_at,
                            project.parent_id,
                            project.container_id);
                    list_obj_project.add(obj_project);
                }
            }
        }
        List<LocalProject> list_local_project = LocalProject.getLocalProjects(actv_search.getText().toString());
        if (list_local_project.size() > 0) {
            for (LocalProject localProject : list_local_project) {
                if(localProject != null){
                    ProjectObject obj_project = new ProjectObject(
                            localProject.project_id,
                            localProject.project_type,
                            localProject.title,
                            localProject.description,
                            localProject.default_form_id,
                            localProject.district_id,
                            localProject.created_by,
                            localProject.created_at,
                            localProject.parent_id,
                            localProject.container_id);
                    list_obj_project.add(obj_project);
                }
            }
        }
        if(list_project.isEmpty() && list_local_project.isEmpty()){
            Toast.makeText(this, TOAST_SEARCH_NO_RESULT, Toast.LENGTH_LONG).show();
        }

        va_pl = new ProjectListViewAdapter(this, list_obj_project);
        lv_project.setAdapter(va_pl);

        Config.hideKeyboard(this);
    }

    public void createNewProjectOnClick() {
        Intent intent = new Intent(ProjectListActivity.this, ReportActivity.class);
        if (obj_user != null) {
            intent.putExtra("obj_user", obj_user);
        }
        intent.putExtra("background_runner", backgroundRunnerProvider);
        startActivity(intent);
    }

    public void createOnClick(ProjectObject obj_project){
        //-- setup report object
        ReportObject obj_report = new ReportObject();
        obj_report.setProject(obj_project);
        obj_report.setProjectType(obj_project.getType());
        obj_report.setProjectId(obj_project.getId());

        //-- setup region district
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

        //-- setup form
        FormObject obj_form = new FormObject();
        Form form = Form.selectSingle(obj_project.getDefaultFormId());
        if(form != null){
            obj_form.setName(form.name);
        }
        obj_report.setForm(obj_form);

        Intent intent = new Intent(ProjectListActivity.this, ReportActivity.class);
        intent.putExtra("obj_report", obj_report);
        if (obj_user != null) {
            intent.putExtra("obj_user", obj_user);
        }
        intent.putExtra("background_runner", backgroundRunnerProvider);
        startActivity(intent);
    }

    public void editOnClick(ProjectObject obj_project){
        Intent intent = new Intent(ProjectListActivity.this, ReportListActivity.class);
        if (obj_user != null) {
            intent.putExtra("obj_user", obj_user);
        }
        intent.putExtra("obj_project", obj_project);
        intent.putExtra("background_runner", backgroundRunnerProvider);
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
