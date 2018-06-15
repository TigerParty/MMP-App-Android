package com.thetigerparty.argodflib.Object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttpttp on 2015/8/4.
 */
public class ProjectObject implements Serializable {

    int id = 0;
    String type = "new";
    String title = "";
    String description = "";
    int default_form_id = 0;
    int district_id = 0;
    int created_by = 0;
    String created_at = "";
    int parent_id = 0;
    int container_id = 1;

    public ProjectObject(){
        super();
    }

    public ProjectObject(int id,
                         String type,
                         String title,
                         String description,
                         int default_form_id,
                         int district_id,
                         int created_by,
                         String created_at,
                         int parent_id,
                         int container_id
    ){
        super();

        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.default_form_id = default_form_id;
        this.district_id = district_id;
        this.created_by = created_by;
        this.created_at = created_at;
        this.parent_id = parent_id;
        this.container_id = container_id;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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

    public void setDefaultFormId(int default_form_id) {
        this.default_form_id = default_form_id;
    }

    public int getDefaultFormId() {
        return default_form_id;
    }

    public void setDistrictId(int district_id) {
        this.district_id = district_id;
    }

    public int getDistrictId() {
        return district_id;
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

    public void setContainerId(int container_id) {
        this.container_id = container_id;
    }

    public int getContainerId() {
        return container_id;
    }

    public void setParentId(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getParentId() {
        return parent_id;
    }
}
