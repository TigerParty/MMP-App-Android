package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by fredtsao on 2017/9/3.
 */
@Table(name = "tracker")
public class Tracker extends Model {
    @Column(name = "title")
    public String title;

    @Column(name = "path")
    public String path;

    @Column(name = "created_by")
    public int created_by;

    @Column(name = "created_at")
    public String created_at;

    @Column(name = "pushed")
    public int pushed;

    public static List<Tracker> getNotPushedTracker() {
        return new Select()
                .from(Tracker.class)
                .where("pushed = 0")
                .execute();
    }

    public static List<Attachment> getAttachments(int tracker_id) {
        return new Select()
                .from(Attachment.class)
                .where("tracker_id = ?", tracker_id)
                .execute();
    }
}
