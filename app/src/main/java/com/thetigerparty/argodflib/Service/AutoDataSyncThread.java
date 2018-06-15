package com.thetigerparty.argodflib.Service;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.orhanobut.logger.Logger;

import com.thetigerparty.argodflib.BuildConfig;
import com.thetigerparty.argodflib.HelperClass.Config;
import com.thetigerparty.argodflib.HelperClass.HttpProcess;
import com.thetigerparty.argodflib.Model.Container;
import com.thetigerparty.argodflib.Model.District;
import com.thetigerparty.argodflib.Model.Field;
import com.thetigerparty.argodflib.Model.Form;
import com.thetigerparty.argodflib.Model.FormField;
import com.thetigerparty.argodflib.Model.PermissionLevel;
import com.thetigerparty.argodflib.Model.Project;
import com.thetigerparty.argodflib.Model.Region;
import com.thetigerparty.argodflib.Model.RelationProjectBelongRegion;
import com.thetigerparty.argodflib.Model.RelationUserOwnProject;
import com.thetigerparty.argodflib.Model.Report;
import com.thetigerparty.argodflib.Model.Setting;
import com.thetigerparty.argodflib.Model.User;
import com.thetigerparty.argodflib.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by fredtsao on 12/21/16.
 */

public class AutoDataSyncThread extends Thread implements Runnable {
    BackgroundRunnerService mainService;
    Context context;

    String DATA_SYNC_API = "";
    String SIMPLE_DATE_FORMAT = "";
    String PROGRESS_DIALOG_DATA_SYNC = "";
    static String TOAST_DATA_SYNC_FAIL = "";
    static String TOAST_DATA_SYNC_SUCCESS = "";
    String TOAST_LOGIN_FAIL = "";
    String TOAST_CHECK_INTERNET_FALSE = "";
    String TOAST_SYNC_DATA_FIRST;

    public AutoDataSyncThread(BackgroundRunnerService service) {
        Logger.d("Service use AutoDataSyncThread");
        this.setDaemon(true);
        this.mainService = service;
        this.context = service.getBaseContext();

        this.getResourceValue();
    }

    public AutoDataSyncThread(Context context) {
        this.context = context;
        this.getResourceValue();
    }

    public void run(){
        super.run();
        while (true) {
            try {
                Thread.sleep(3600000);
                if (Config.checkInternet(context)) {
                    download();
                }
            } catch (Exception e) {
                Logger.d(e.getMessage());
            }
        }
    }

    private void getResourceValue(){
        DATA_SYNC_API = BuildConfig.DOWNLOAD_DATA_API;
        SIMPLE_DATE_FORMAT = context.getString(R.string.sdf_ymd_hms);
        TOAST_DATA_SYNC_FAIL = context.getString(R.string.data_sync_fail);
        TOAST_DATA_SYNC_SUCCESS = context.getString(R.string.data_sync_success);
        TOAST_CHECK_INTERNET_FALSE = context.getString(R.string.check_internet_false);
        TOAST_LOGIN_FAIL = context.getString(R.string.login_fail);
        TOAST_SYNC_DATA_FIRST = context.getString(R.string.sync_data_first);
        PROGRESS_DIALOG_DATA_SYNC = context.getString(R.string.data_sync);
    }

    public void download() throws Exception {
        try {
            String response = HttpProcess.httpGet(DATA_SYNC_API);
            if (!response.equals("fail")) {
                this.arrange(response);
                this.updateLastSyncedTime();
            } else {
                throw new Exception("download failed");
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
            throw e;
        }
    }

    private void arrange(String response) throws JSONException {
        try {
            ActiveAndroid.beginTransaction();
            // Clean table
            User.deleteTable();
            Project.deleteTable();
            Form.deleteTable();
            FormField.deleteTable();
            Field.deleteTable();
            Region.deleteTable();
            District.deleteTable();
            Container.deleteTable();
            RelationProjectBelongRegion.deleteTable();
            RelationUserOwnProject.deleteTable();
            PermissionLevel.deleteTable();

            // Arrange data
            JSONObject downloadData = new JSONObject(response);
            JSONArray users = downloadData.getJSONArray("User");
            for (int i = 0; i < users.length() ; i++) {
                JSONObject user = users.getJSONObject(i);

                User storeUser = new User();
                storeUser.user_id = user.getInt("id");
                storeUser.name = user.getString("name");
                storeUser.password = user.getString("pwd");
                storeUser.email = user.getString("email");
                storeUser.permission_level_id = !user.isNull("permission_level_id") ?
                                                    user.getInt("permission_level_id") : 0;
                storeUser.save();
            }

            JSONArray projects = downloadData.getJSONArray("Project");
            for (int i = 0; i < projects.length() ; i++) {
                JSONObject project = projects.getJSONObject(i);

                Project storeProject = new Project();
                storeProject.project_id = project.getInt("id");
                storeProject.title = project.getString("title");
                storeProject.description = project.getString("description");
                storeProject.created_at = project.getString("created_at");
                storeProject.created_by = !project.isNull("created_by") ?
                                        project.getInt("created_by"): 0;
                storeProject.project_type = "server";
                storeProject.default_form_id = !project.isNull("default_form_id") ?
                                                project.getInt("default_form_id") : 1;
                storeProject.deleted_at = !project.isNull("deleted_at") ?
                                            project.getString("deleted_at") : null;
                storeProject.container_id = project.isNull("container_id") ? 1 : project.getInt("container_id");
                storeProject.parent_id = project.isNull("parent_id") ? 0 : project.getInt("parent_id");
                storeProject.edit_level_id = !project.isNull("edit_level") ? project.getJSONObject("edit_level").getInt("priority") : 0;
                storeProject.save();

                if (!project.isNull("deleted_at")) {
                    Report.deleteReportsByProjectId(storeProject.project_id);
                }
                Report.updateProjectTypeByProjectId(storeProject.project_id, "server");

                if (!project.isNull("owners")) {
                    JSONArray owners = project.getJSONArray("owners");
                    for (int j = 0; j < owners.length(); j++) {
                        JSONObject owner = owners.getJSONObject(j);
                        JSONObject pivot = owner.getJSONObject("pivot");
                        RelationUserOwnProject rel = new RelationUserOwnProject();
                        rel.user_id = pivot.getInt("user_id");
                        rel.project_id = pivot.getInt("project_id");
                        rel.project_type = "server";
                        rel.save();
                    }
                }
            }

            JSONArray dynamicForms = downloadData.getJSONArray("Form");
            for (int i = 0; i < dynamicForms.length() ; i++) {
                JSONObject dynamicForm = dynamicForms.getJSONObject(i);

                Form storeDynamicForm = new Form();
                storeDynamicForm.form_id = dynamicForm.getInt("id");
                storeDynamicForm.name = dynamicForm.getString("name");
                storeDynamicForm.is_photo_required = (dynamicForm.getInt("is_photo_required") == 1);
                storeDynamicForm.save();
            }

            JSONArray formFields = downloadData.getJSONArray("FormField");
            for (int i = 0; i < formFields.length() ; i++) {
                JSONObject formField = formFields.getJSONObject(i);

                FormField storeFormField = new FormField();
                storeFormField.name = formField.getString("name");
                storeFormField.form_field_id = formField.getInt("id");
                storeFormField.form_id = formField.getInt("form_id");
                storeFormField.field_id = formField.getInt("field_template_id");
                storeFormField.is_required = (formField.getInt("is_required") == 1);
                storeFormField.options = !formField.isNull("options") ?
                                            formField.getString("options") : null;
                storeFormField.default_value = !formField.isNull("default_value") ?
                                                formField.getString("default_value") : null;
                storeFormField.order = !formField.isNull("order") ?
                                        formField.getInt("order") : 0;
                storeFormField.show_if = !formField.isNull("show_if") ?
                                            formField.getString("show_if") : null;
                storeFormField.edit_level_priority = formField.getInt("edit_level_priority");
                storeFormField.formula = !formField.isNull("formula") ? formField.getString("formula") : null;
                storeFormField.save();
            }

            JSONArray fieldTemplates = downloadData.getJSONArray("Field");
            for (int i = 0; i < fieldTemplates.length() ; i++) {
                JSONObject fieldTemplate = fieldTemplates.getJSONObject(i);

                Field storeFieldTemplate = new Field();
                storeFieldTemplate.field_id = fieldTemplate.getInt("id");
                storeFieldTemplate.name = fieldTemplate.getString("html");
                storeFieldTemplate.save();
            }

            JSONArray regions = downloadData.getJSONArray("Region");
            for (int i = 0; i < regions.length() ; i++) {
                JSONObject region = regions.getJSONObject(i);
                Region storeRegion = new Region();
                storeRegion.region_id = region.getInt("id");
                storeRegion.name = region.getString("name");
                storeRegion.label_name = region.getString("label_name");
                storeRegion.order =  region.isNull("order")? 0 : region.getInt("order");
                storeRegion.parnet_id = !region.isNull("parent_id") ?
                                            region.getInt("parent_id") : 0;
                storeRegion.save();
                JSONArray pivotProjects = region.getJSONArray("projects");
                for (int j = 0; j < pivotProjects.length(); j++) {
                    JSONObject pivotProject = pivotProjects.getJSONObject(j);
                    JSONObject pivot = pivotProject.getJSONObject("pivot");
                    Project.attach_region(pivot.getInt("project_id"), "server", pivot.getInt("region_id"));
                }
            }

            JSONArray containers = downloadData.getJSONArray("Container");
            for (int i = 0; i < containers.length(); i++) {
                JSONObject container = containers.getJSONObject(i);
                Container storeContainer = new Container();
                storeContainer.container_id = container.getInt("id");
                storeContainer.name = container.getString("name");
                storeContainer.parent_id = container.isNull("parent_id") ? 0 : container.getInt("parent_id");
                storeContainer.form_id = container.isNull("form_id") ? 0 : container.getInt("form_id");
                storeContainer.reportable = container.isNull("reportable") ? true : (container.getInt("reportable") == 1);
                storeContainer.save();
            }

            JSONArray permissions = downloadData.getJSONArray("PermissionLevel");
            for (int i = 0; i < permissions.length(); i++) {
                JSONObject permission = permissions.getJSONObject(i);
                PermissionLevel storePermission = new PermissionLevel();
                storePermission.permission_id = !permission.isNull("id") ? permission.getInt("id") : 0;
                storePermission.priority = !permission.isNull("priority") ? permission.getInt("priority") : 0;
                storePermission.name = !permission.isNull("name") ? permission.getString("name") : null;
                storePermission.save();
            }

            List<Form> listDynamicForms = Form.forms();
            Report.deleteFormNotExist(listDynamicForms);
            ActiveAndroid.setTransactionSuccessful();
        } catch (Exception e) {
            Logger.e(e.getMessage());
            throw e;
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private void updateLastSyncedTime() {
        DateFormat dateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        Date today = new Date();

        Setting setting = Setting.select();
        if (setting == null){
            setting = new Setting();
        }
        else{
            setting = Setting.select();
        }
        setting.last_synced_datetime = dateFormat.format(today);
        setting.save();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (context != null) {
            context = null;
        }

        Runtime.getRuntime().gc();
    }
}
