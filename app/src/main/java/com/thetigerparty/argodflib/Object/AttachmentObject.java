package com.thetigerparty.argodflib.Object;

import java.io.Serializable;

/**
 * Created by ttpttp on 2015/8/7.
 */
public class AttachmentObject implements Serializable{

    public final static String KEY_HEADER = "header";
    public final static String KEY_CONTENT = "content";

    int id = 0;
    String name = "";
    String path = "";
    String type = "";
    int report_id = 0;
    String description = "";
    int tracker_id = 0;

    public AttachmentObject(){
        super();
    }

    public AttachmentObject(int id,
                            String name,
                            String path,
                            String type,
                            int report_id,
                            String description){
        super();

        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
        this.report_id = report_id;
        this.description = description;
    }

    public AttachmentObject(int id,
                            String name,
                            String path,
                            String type,
                            int report_id,
                            int tracker_id,
                            String description) {
        super();
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
        this.report_id = report_id;
        this.tracker_id = tracker_id;
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setReportId(int report_id) {
        this.report_id = report_id;
    }

    public int getReportId() {
        return report_id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void  setTracker_id(int tracker_id) {
        this.tracker_id = tracker_id;
    }

    public int getTracker_id() {
        return tracker_id;
    }
}
