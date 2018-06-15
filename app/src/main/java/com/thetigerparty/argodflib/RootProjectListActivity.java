package com.thetigerparty.argodflib;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
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
import com.thetigerparty.argodflib.Model.Container;
import com.thetigerparty.argodflib.Model.LocalProject;
import com.thetigerparty.argodflib.Model.Project;
import com.thetigerparty.argodflib.Model.Region;
import com.thetigerparty.argodflib.Object.ContainerObject;
import com.thetigerparty.argodflib.Object.ProjectObject;
import com.thetigerparty.argodflib.Object.UserObject;
import com.thetigerparty.argodflib.Service.BackgroundRunnerProvider;
import com.thetigerparty.argodflib.Service.BackgroundRunnerService;
import com.thetigerparty.argodflib.Subclass.ProjectList.ProjectsListViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by fredtsao on 1/5/17.
 */

public class RootProjectListActivity extends Activity implements Observer {
    String TOAST_SEARCH_NO_RESULT;

    private Button bt_create_project;
    private Button bt_search;
    private Button bt_header_back;

    private TextView tv_header_title;
    private ListView lv_root_project;
    private AutoCompleteTextView actv_search;

    private UserObject user_obj = null;
    private ContainerObject container_obj;

    private List<String> list_region_name = new ArrayList<>();
    private List<ProjectObject> list_project_obj = new ArrayList<>();

    BackgroundRunnerService backgroundRunnerService;
    BackgroundRunnerProvider backgroundRunnerProvider;
    BackgroundRunnerProvider.BackgroundRunnerServiceConnection backgroundRunnerServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_project_list_activity);

        setIntentValue();
        setView();
        setSearchAutoComplete();
        setDefaultRootContainer();

        backgroundRunnerProvider.add_observer(this);
        this.backgroundRunnerServiceConnection = backgroundRunnerProvider.getBackgroundRunnerServiceConnection();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, BackgroundRunnerService.class), backgroundRunnerServiceConnection, Service.BIND_ADJUST_WITH_ACTIVITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProjectList();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(backgroundRunnerServiceConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setView() {
        TOAST_SEARCH_NO_RESULT = getString(R.string.toast_search_no_result);

        bt_create_project = (Button)findViewById(R.id.bt_create_new_project);
        bt_create_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAddNewButton();
            }
        });

        bt_search = (Button)findViewById(R.id.bt_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProjectList();
            }
        });

        bt_header_back = (Button)findViewById(R.id.bt_header_back);
        bt_header_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_header_title = (TextView)findViewById(R.id.tv_header_title);
        tv_header_title.setText("Report");

        actv_search = (AutoCompleteTextView)findViewById(R.id.actv_search);
        actv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getProjectList();
            }
        });

        lv_root_project = (ListView)findViewById(R.id.lv_project);
    }

    private void setIntentValue() {
        if (getIntent().getExtras() != null) {
            user_obj = (UserObject) getIntent().getExtras().getSerializable("obj_user");
            backgroundRunnerProvider = (BackgroundRunnerProvider) getIntent().getExtras().getSerializable("background_runner");
        }
    }

    private void setSearchAutoComplete() {
        List<Region> list_region = Region.getAllRegionName();
        for (Region region : list_region) {
            list_region_name.add(region.name);
        }

        List<Project> list_project = Project.getAllProjectTitle();
        for (Project project : list_project) {
            list_region_name.add(project.title);
        }

        List<LocalProject> list_local_project = LocalProject.getAllProjectTitle();
        for (LocalProject localProject : list_local_project) {
            list_region_name.add(localProject.title);
        }

        ArrayAdapter<String> actv_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, list_region_name);
        actv_search.setAdapter(actv_adapter);
    }

    private void getProjectList() {
        list_project_obj = new ArrayList<>();
        List<Project> list_project = new ArrayList<>();
        List<LocalProject> list_local_project = new ArrayList<>();

        if (user_obj == null) {
            list_project = Project.getSearchRootProjects(actv_search.getText().toString());
            list_local_project = LocalProject.getSearchRootProjects(actv_search.getText().toString());
        } else {
            list_project = Project.getSearchRootProjects(actv_search.getText().toString(), user_obj.getUserId());
            list_local_project = LocalProject.getSearchRootProjects(actv_search.getText().toString(), user_obj.getUserId());
        }

        for (Project project : list_project) {
            ProjectObject project_obj = new ProjectObject(
                    project.project_id,
                    project.project_type,
                    project.title,
                    project.description,
                    project.default_form_id,
                    project.district_id,
                    project.created_by,
                    project.created_at,
                    project.parent_id,
                    project.container_id
            );
            list_project_obj.add(project_obj);
        }
        for (LocalProject localProject : list_local_project) {
            ProjectObject project_obj = new ProjectObject(
                    localProject.project_id,
                    localProject.project_type,
                    localProject.title,
                    localProject.description,
                    localProject.default_form_id,
                    localProject.district_id,
                    localProject.created_by,
                    localProject.created_at,
                    localProject.parent_id,
                    localProject.container_id
            );
            list_project_obj.add(project_obj);
        }

        if (list_local_project.isEmpty() && list_project.isEmpty()) {
            Toast.makeText(this, TOAST_SEARCH_NO_RESULT, Toast.LENGTH_LONG).show();
        }

        lv_root_project.setAdapter(new ProjectsListViewAdapter(this, list_project_obj));
        lv_root_project.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickRootProjectHolder(list_project_obj.get(position));
            }
        });
        Config.hideKeyboard(this);
    }

    private void clickAddNewButton() {
        Intent intent = new Intent(RootProjectListActivity.this, ReportActivity.class);
        intent.putExtra("obj_user", user_obj);
        intent.putExtra("background_runner", backgroundRunnerProvider);
        intent.putExtra("container_obj", container_obj);
        intent.putExtra("is_new_project", true);

        startActivity(intent);
    }

    public void clickRootProjectHolder(ProjectObject project_obj) {
        Intent intent = new Intent(RootProjectListActivity.this, ProjectActivity.class);
        intent.putExtra("obj_user", user_obj);
        intent.putExtra("background_runner", backgroundRunnerProvider);
        intent.putExtra("project_obj", project_obj);

        startActivity(intent);
    }

    private void setDefaultRootContainer() {
        Container container = Container.get(BuildConfig.DEFAULT_ROOT_CONTAINER_ID);
        this.container_obj = new ContainerObject(
                container.container_id,
                container.name,
                container.parent_id,
                container.form_id,
                container.reportable
        );
    }

    // Observer method
    @Override
    public void update(Observable background, Object service){
        Logger.d("Service connecting ...");
        if (service.getClass().getSimpleName().equals("BackgroundRunnerService")) {
            Logger.d("Service connected success");
            backgroundRunnerService = (BackgroundRunnerService) service;
            backgroundRunnerService.setCurrentUser(user_obj);
        } else {
            Logger.e("Service connected fail");
        }
    }
}
