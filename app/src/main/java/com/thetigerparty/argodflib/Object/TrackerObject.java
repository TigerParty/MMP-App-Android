package com.thetigerparty.argodflib.Object;

import java.io.Serializable;

/**
 * Created by fredtsao on 2017/9/3.
 */

public class TrackerObject implements Serializable {
    int id = 0;
    String title = "";
    String path = "";
    int pushed = 0;
    int created_by = 0;
    String created_at = "";

    public TrackerObject() {
        super();
    }

    public TrackerObject(int id,
                         String title,
                         String path,
                         int pushed,
                         int created_by,
                         String created_at) {
        super();

        this.id = id;
        this.title = title;
        this.path = path;
        this.pushed = pushed;
        this.created_by = created_by;
        this.created_at = created_at;
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

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPushed(int pushed) {
        this.pushed = pushed;
    }

    public int getPushed() {
        return pushed;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCreated_at() {
        return created_at;
    }
}
