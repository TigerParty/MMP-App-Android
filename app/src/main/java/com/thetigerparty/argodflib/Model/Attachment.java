package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

/**
 * Created by ttpttp on 2015/8/7.
 */
@Table(name = "attachment")
public class Attachment extends Model {
    @Column(name = "name")
    public String name;

    @Column(name = "path")
    public String path;

    @Column(name = "type")
    public String type;

    @Column(name = "report_id")
    public int report_id;

    @Column(name = "tracker_id")
    public int tracker_id;

    @Column(name = "created_at")
    public String created_at;

    @Column(name = "description")
    public String description;

    public static void delete(int report_id){
        new Delete()
            .from(Attachment.class)
            .where("report_id = ?", report_id)
            .execute();
    }
}
