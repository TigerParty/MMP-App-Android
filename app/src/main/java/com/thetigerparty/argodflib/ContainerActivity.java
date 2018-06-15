package com.thetigerparty.argodflib;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.thetigerparty.argodflib.HelperClass.Dialog;
import com.thetigerparty.argodflib.Model.Container;
import com.thetigerparty.argodflib.Model.LocalProject;
import com.thetigerparty.argodflib.Model.Project;
import com.thetigerparty.argodflib.Object.ContainerObject;
import com.thetigerparty.argodflib.Object.ProjectObject;
import com.thetigerparty.argodflib.Object.UserObject;
import com.thetigerparty.argodflib.Service.BackgroundRunnerProvider;
import com.thetigerparty.argodflib.Service.BackgroundRunnerService;
import com.thetigerparty.argodflib.Subclass.ContainerList.ContainerListViewAdapter;
import com.thetigerparty.argodflib.Subclass.ProjectList.ProjectsListViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by fredtsao on 1/5/17.
 */

public class ContainerActivity extends Activity implements Observer {
    UserObject user_obj;
    ProjectObject project_obj;
    ContainerObject container_obj;

    List<ContainerObject> list_container_obj;
    List<ProjectObject> list_project_obj;
    List<String> list_bread_crumb = new ArrayList<>();

    TextView tv_header_title;
    TextView tv_bread_crumb;

    ListView lv_container_list;
    ListView lv_project_list;

    LinearLayout layout_container_list_view;

    Button bt_edit;
    Button bt_add_new;
    Button bt_header_back;
    ImageButton bt_circle_add_new;

    BackgroundRunnerService backgroundRunnerService;
    BackgroundRunnerProvider backgroundRunnerProvider;
    BackgroundRunnerProvider.BackgroundRunnerServiceConnection backgroundRunnerServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_activity);
        setIntentValue();
        setView();
        setBreadCrumb();

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
        getContainerList();
        getProjectList();
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
        super.onDestroy();
        unbindService(backgroundRunnerServiceConnection);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setView() {
        this.layout_container_list_view = (LinearLayout)findViewById(R.id.layout_container_list_view);
        LinearLayout layout_edit_button = (LinearLayout)findViewById(R.id.layout_edit_button);

        this.lv_container_list = (ListView)findViewById(R.id.lv_container);

        this.lv_project_list = (ListView)findViewById(R.id.lv_project);

        this.tv_header_title = (TextView)findViewById(R.id.tv_header_title);
        this.tv_header_title.setText("Report");

        this.tv_bread_crumb = (TextView) findViewById(R.id.tv_bread_crumb);

        this.bt_header_back = (Button)findViewById(R.id.bt_header_back);
        this.bt_header_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.bt_edit = (Button)findViewById(R.id.bt_edit);
        this.bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContainerActivity.this, ReportActivity.class);
                intent.putExtra("obj_user", user_obj);
                intent.putExtra("background_runner", backgroundRunnerProvider);
                intent.putExtra("container_obj", container_obj);
                intent.putExtra("parent_project_obj", project_obj);
                intent.putExtra("is_new_project", false);
                startActivity(intent);
            }
        });
        this.bt_edit.setVisibility(View.GONE);
        layout_edit_button.setVisibility(View.GONE);

        this.bt_add_new = (Button)findViewById(R.id.bt_add_new);
        this.bt_circle_add_new = (ImageButton)findViewById(R.id.bt_circle_add_new);

        this.bt_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (project_obj.getType().equals("server")) {
                    Intent intent = new Intent(ContainerActivity.this, ReportActivity.class);
                    intent.putExtra("obj_user", user_obj);
                    intent.putExtra("background_runner", backgroundRunnerProvider);
                    intent.putExtra("container_obj", container_obj);
                    intent.putExtra("parent_project_obj", project_obj);
                    intent.putExtra("is_new_project", true);
                    startActivity(intent);
                } else {
                    Dialog.BlockProjectIntoSubProjectPageDialog(ContainerActivity.this);
                }
            }
        });
        this.bt_circle_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (project_obj.getType().equals("server")) {
                    Intent intent = new Intent(ContainerActivity.this, ReportActivity.class);
                    intent.putExtra("obj_user", user_obj);
                    intent.putExtra("background_runner", backgroundRunnerProvider);
                    intent.putExtra("container_obj", container_obj);
                    intent.putExtra("parent_project_obj", project_obj);
                    intent.putExtra("is_new_project", true);
                    startActivity(intent);
                } else {
                    Dialog.BlockProjectIntoSubProjectPageDialog(ContainerActivity.this);
                }
            }
        });
        if (!this.container_obj.getReportable()) {
            this.bt_circle_add_new.setVisibility(View.GONE);
            this.bt_add_new.setVisibility(View.GONE);
        } else {
            this.bt_circle_add_new.setVisibility(View.VISIBLE);
            this.bt_add_new.setVisibility(View.VISIBLE);
        }
    }

    private void setIntentValue() {
        if (!getIntent().getExtras().isEmpty()) {
            this.user_obj = (UserObject) getIntent().getExtras().getSerializable("obj_user");
            this.project_obj = (ProjectObject) getIntent().getExtras().getSerializable("project_obj");
            this.container_obj = (ContainerObject) getIntent().getExtras().getSerializable("container_obj");
            this.backgroundRunnerProvider = (BackgroundRunnerProvider) getIntent().getExtras().getSerializable("background_runner");
        }
    }

    private void setBreadCrumb() {
        list_bread_crumb = new ArrayList<>();
        Container parentContainer = Container.get(project_obj.getContainerId());
        list_bread_crumb.add(" " + parentContainer.name + " >");
        list_bread_crumb.add(" " + project_obj.getTitle() + " >");
        list_bread_crumb.add(" " + container_obj.getContainerName() + " ");

        String crumbs = "";
        for (String crumb : list_bread_crumb) {
            crumbs += crumb;
        }
        tv_bread_crumb.setText(crumbs);
    }

    private void getContainerList() {
        list_container_obj = new ArrayList<>();
        final List<Container> list_container = Container.getContainerlistByParentId(container_obj.getContainerId());
        for (Container container : list_container) {
            ContainerObject containerObj = new ContainerObject(
                    container.container_id,
                    container.name,
                    container.parent_id,
                    container.form_id,
                    container.reportable
            );
            list_container_obj.add(containerObj);
        }

        lv_container_list.setAdapter(new ContainerListViewAdapter(this, list_container_obj));
        lv_container_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (project_obj.getType().equals("server")) {
                    Intent intent = new Intent(ContainerActivity.this, ContainerActivity.class);
                    intent.putExtra("obj_user", user_obj);
                    intent.putExtra("project_obj", project_obj);
                    intent.putExtra("container_obj", list_container_obj.get(position));
                    intent.putExtra("background_runner", backgroundRunnerProvider);
                    startActivity(intent);
                } else {
                    Dialog.BlockProjectIntoSubProjectPageDialog(ContainerActivity.this);
                }
            }
        });
        if (list_container_obj.isEmpty()) {
            layout_container_list_view.setVisibility(View.GONE);
        }
    }

    private void getProjectList() {
        list_project_obj = new ArrayList<>();
        List<Project> list_server_project = Project.getSearchProjects("", container_obj.getContainerId(), project_obj.getId(), project_obj.getType());
        List<LocalProject> list_local_project = LocalProject.getSearchProjects("", container_obj.getContainerId(), project_obj.getId(), project_obj.getType());
        for (Project project : list_server_project) {
            list_project_obj.add(
                    new ProjectObject(
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
                    )
            );
        }
        for (LocalProject localProject : list_local_project) {
            list_project_obj.add(
                    new ProjectObject(
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
                    )
            );
        }

        lv_project_list.setAdapter(new ProjectsListViewAdapter(this, list_project_obj));
        lv_project_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(project_obj.getType().equals("server")) {
                    Intent intent = new Intent(ContainerActivity.this, ProjectActivity.class);
                    intent.putExtra("obj_user", user_obj);
                    intent.putExtra("project_obj", list_project_obj.get(position));
                    intent.putExtra("container_obj", container_obj);
                    intent.putExtra("background_runner", backgroundRunnerProvider);
                    startActivity(intent);
                } else {
                    Dialog.BlockProjectIntoSubProjectPageDialog(ContainerActivity.this);
                }
            }
        });
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
