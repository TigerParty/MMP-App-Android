package com.thetigerparty.argodflib.Object;

import java.io.Serializable;

/**
 * Created by fredtsao on 1/6/17.
 */

public class ContainerObject implements Serializable {

    int container_id = 1;
    String name = "";
    int parent_id = 0;
    int form_id = 1;
    boolean reportable = true;

    public ContainerObject() {
        super();
    }

    public ContainerObject(
            int container_id,
            String name,
            int parent_id,
            int form_id,
            boolean reportable
    ) {
        this.container_id = container_id;
        this.name = name;
        this.parent_id = parent_id;
        this.form_id = form_id;
        this.reportable = reportable;
    }

    public void setContainerId(int container_id) {
        this.container_id = container_id;
    }

    public int getContainerId() {
        return container_id;
    }

    public void setContainerName(String name) {
        this.name = name;
    }

    public String getContainerName() {
        return name;
    }

    public void setContainerParentId(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getContainerParentId() {
        return parent_id;
    }

    public void setForm_id(int form_id) {
        this.form_id = form_id;
    }

    public int getForm_id() {
        return form_id;
    }

    public void setReportable(boolean reportable) {
        this.reportable = reportable;
    }

    public boolean getReportable() {
        return reportable;
    }
}
