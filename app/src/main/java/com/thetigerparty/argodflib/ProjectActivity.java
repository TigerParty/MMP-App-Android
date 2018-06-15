package com.thetigerparty.argodflib;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.thetigerparty.argodflib.HelperClass.Dialog;
import com.thetigerparty.argodflib.Model.Attachment;
import com.thetigerparty.argodflib.Model.Container;
import com.thetigerparty.argodflib.Model.Form;
import com.thetigerparty.argodflib.Model.Report;
import com.thetigerparty.argodflib.Object.AttachmentObject;
import com.thetigerparty.argodflib.Object.ContainerObject;
import com.thetigerparty.argodflib.Object.FormObject;
import com.thetigerparty.argodflib.Object.ProjectObject;
import com.thetigerparty.argodflib.Object.ReportObject;
import com.thetigerparty.argodflib.Object.UserObject;
import com.thetigerparty.argodflib.Service.BackgroundRunnerProvider;
import com.thetigerparty.argodflib.Service.BackgroundRunnerService;
import com.thetigerparty.argodflib.Subclass.ContainerList.ContainerListViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by fredtsao on 1/5/17.
 */

public class ProjectActivity extends Activity implements Observer {
    UserObject user_obj;
    ReportObject report_obj;
    ProjectObject project_obj;
    ContainerObject container_obj;

    List<ContainerObject> list_container_obj;
    List<String> list_bread_crumb = new ArrayList<>();

    ListView lv_root_container;
    TextView tv_header_title;
    TextView tv_bread_crumb;

    Button bt_edit_report;
    Button bt_header_back;

    BackgroundRunnerService backgroundRunnerService;
    BackgroundRunnerProvider backgroundRunnerProvider;
    BackgroundRunnerProvider.BackgroundRunnerServiceConnection backgroundRunnerServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_container_list_activity);
        setIntentValue();
        setContainerObject();
        setReportObject();
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
        finish();
    }

    private void setView() {
        this.tv_header_title = (TextView)findViewById(R.id.tv_header_title);
        this.tv_header_title.setText("Report");

        this.tv_bread_crumb = (TextView)findViewById(R.id.tv_bread_crumb);

        this.lv_root_container = (ListView)findViewById(R.id.lv_container);

        this.bt_header_back = (Button)findViewById(R.id.bt_header_back);
        this.bt_header_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.bt_edit_report = (Button)findViewById(R.id.bt_edit);
        this.bt_edit_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectActivity.this, ReportActivity.class);
                intent.putExtra("obj_user", user_obj);
                intent.putExtra("background_runner", backgroundRunnerProvider);
                intent.putExtra("container_obj", container_obj);
                intent.putExtra("parent_project_obj", project_obj);
                intent.putExtra("is_new_project", false);
                intent.putExtra("obj_report", report_obj);
                startActivity(intent);
            }
        });
    }

    private void setIntentValue() {
        if (!getIntent().getExtras().isEmpty()) {
            this.user_obj = (UserObject) getIntent().getExtras().getSerializable("obj_user");
            this.backgroundRunnerProvider = (BackgroundRunnerProvider) getIntent().getExtras().getSerializable("background_runner");
            this.project_obj = (ProjectObject) getIntent().getExtras().getSerializable("project_obj");
            this.list_bread_crumb = (List<String>) getIntent().getExtras().getSerializable("list_bread_crumb");
        }
    }

    private void setContainerObject() {
        int container_id = this.project_obj.getContainerId();
        Container container = Container.get(container_id);
        this.container_obj = new ContainerObject(
                container.container_id,
                container.name,
                container.parent_id,
                container.form_id,
                container.reportable
        );
    }

    private void setBreadCrumb() {
        list_bread_crumb = new ArrayList<>();
        list_bread_crumb.add(" " + container_obj.getContainerName() + " >");
        list_bread_crumb.add(" " + project_obj.getTitle() + " ");

        String crumbs = "";
        for (String crumb: list_bread_crumb) {
            crumbs += crumb;
        }
        tv_bread_crumb.setText(crumbs);
    }

    private void getContainerList() {
        list_container_obj = new ArrayList<>();
        final List<Container> list_container = Container.getContainerlistByParentId(project_obj.getContainerId());
        if (!list_container.isEmpty()) {
            for (Container container : list_container) {
                ContainerObject obj = new ContainerObject(
                        container.container_id,
                        container.name,
                        container.parent_id,
                        container.form_id,
                        container.reportable
                );
                list_container_obj.add(obj);
            }
        }


        lv_root_container.setAdapter(new ContainerListViewAdapter(this, list_container_obj));
        lv_root_container.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (project_obj.getType().equals("server")) {
                    Intent intent = new Intent(ProjectActivity.this, ContainerActivity.class);
                    intent.putExtra("obj_user", user_obj);
                    intent.putExtra("project_obj", project_obj);
                    intent.putExtra("container_obj", list_container_obj.get(position));
                    intent.putExtra("background_runner", backgroundRunnerProvider);

                    startActivity(intent);
                } else {
                    Dialog.BlockProjectIntoSubProjectPageDialog(ProjectActivity.this);
                }
            }
        });
    }

    private void setReportObject() {
        Report last_report = Report.getLastReport(project_obj.getId(), project_obj.getType());
        if (last_report != null) {
            FormObject form_object = new FormObject();
            Form report_form = Form.selectSingle(last_report.form_id);
            form_object.setId(report_form.form_id);
            form_object.setName(report_form.name);
            form_object.setIsPhotoRequired(report_form.is_photo_required);

            this.report_obj = new ReportObject();
            report_obj.setId(Integer.parseInt(last_report.getId().toString()));
            report_obj.setProject(project_obj);
            report_obj.setProjectType(last_report.project_type);
            report_obj.setProjectId(last_report.project_id);
            report_obj.setDescription(last_report.description);
            report_obj.setLatitude(last_report.lat);
            report_obj.setLongitude(last_report.lng);
            report_obj.setForm(form_object);
            report_obj.setPushed(last_report.pushed);
            report_obj.setCreatedBy(last_report.created_by);
            report_obj.setReporterEmail(last_report.reporter_email);
            report_obj.setReporterName(last_report.reporter_name);

            List<AttachmentObject> list_obj_attachment = new ArrayList<>();
            List<Attachment> list_attachment = Report.attachments(report_obj.getId());
            if(!list_attachment.isEmpty()){
                for (Attachment attachment : list_attachment){
                    if(attachment != null){
                        list_obj_attachment.add(new AttachmentObject(attachment.getId().intValue(),
                                attachment.name,
                                attachment.path,
                                attachment.type,
                                last_report.getId().intValue(),
                                attachment.description
                        ));
                    }
                }
            }
            report_obj.setListObjAttachment(list_obj_attachment);
        }
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
