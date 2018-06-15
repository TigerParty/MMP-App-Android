package com.thetigerparty.argodflib.Object;

import com.thetigerparty.argodflib.BuildConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttpttp on 2015/8/4.
 */
public class ReportObject implements Serializable {

    int id = 0;
    ProjectObject obj_project = new ProjectObject();
    FormObject obj_form = new FormObject();
    RegionObject obj_region = new RegionObject();
    DistrictObject obj_district = new DistrictObject();
    String title = "";
    String description = "";
    int project_id = 0;
    String project_type = "new";
    double latitude = BuildConfig.DEFAULT_MAP_CENTER_LATITUDE;
    double longitude = BuildConfig.DEFAULT_MAP_CENTER_LONGITUDE;
    List<AttachmentObject> list_obj_attachment = new ArrayList<>();
    int created_by = 0;
    String created_at = "";
    int pushed = 0;
    String reporter_name = "";
    String reporter_email = "";

    public ReportObject(){
        super();
    }

    public ReportObject(int id,
                        ProjectObject obj_project,
                        FormObject obj_form,
                        RegionObject obj_region,
                        DistrictObject obj_district,
                        String title,
                        String description,
                        double latitude,
                        double longitude,
                        List<AttachmentObject> list_obj_attachment,
                        int created_by,
                        String created_at,
                        int pushed,
                        String reporter_name,
                        String reporter_email){
        super();

        this.id = id;
        this.obj_project = obj_project;
        this.obj_form = obj_form;
        this.obj_region = obj_region;
        this.obj_district = obj_district;
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.list_obj_attachment = list_obj_attachment;
        this.created_by = created_by;
        this.created_at = created_at;
        this.pushed = pushed;
        this.reporter_name = reporter_name;
        this.reporter_email = reporter_email;
    }

    public void setProjectId (int project_id) {
        this.project_id = project_id;
    }

    public int getProjectId () {
        return project_id;
    }

    public void setProjectType (String project_type) {
        this.project_type = project_type;
    }

    public String getProjectType () {
        return project_type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setProject(ProjectObject obj_project) {
        this.obj_project = obj_project;
    }

    public ProjectObject getProject() {
        return obj_project;
    }

    public void setForm(FormObject obj_form) {
        this.obj_form = obj_form;
    }

    public FormObject getForm() {
        return obj_form;
    }

    public void setRegion(RegionObject obj_region) {
        this.obj_region = obj_region;
    }

    public RegionObject getRegion() {
        return obj_region;
    }

    public void setDistrict(DistrictObject obj_district) {
        this.obj_district = obj_district;
    }

    public DistrictObject getDistrict() {
        return obj_district;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setListObjAttachment(List<AttachmentObject> list_obj_attachment) {
        this.list_obj_attachment = list_obj_attachment;
    }

    public List<AttachmentObject> getListObjAttachment() {
        return list_obj_attachment;
    }

    public void setCreatedBy(int created_by) {
        this.created_by = created_by;
    }

    public int getCreatedBy() {
        return created_by;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setPushed(int pushed) {
        this.pushed = pushed;
    }

    public int getPushed() {
        return pushed;
    }

    public void setReporterName(String name) {
        this.reporter_name = name;
    }

    public String getReporterName() {
        return reporter_name;
    }

    public void setReporterEmail(String email) {
        this.reporter_email = email;
    }

    public String getReporterEmail() {
        return reporter_email;
    }
}
