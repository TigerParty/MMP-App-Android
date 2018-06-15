package com.thetigerparty.argodflib.Model;

import android.text.TextUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

/**
 * Created by ttpttp on 2015/8/4.
 */
@Table(name = "report")
public class Report extends Model {
    @Column(name = "project_id")
    public int project_id;

    @Column(name = "project_type")
    public String project_type;

    @Column(name = "form_id")
    public int form_id;

    @Column(name = "title")
    public String title;

    @Column(name = "description")
    public String description;

    @Column(name = "lat")
    public double lat;

    @Column(name = "lng")
    public double lng;

    @Column(name = "created_by")
    public int created_by;

    @Column(name = "created_at")
    public String created_at;

    @Column(name = "pushed")
    public int pushed;

    @Column(name = "reporter_email")
    public String reporter_email;

    @Column(name = "reporter_name")
    public String reporter_name;

    public static Report select(int id){
        return new Select()
                .from(Report.class)
                .where("id = ?", id)
                .executeSingle();
    }

    public static List<Report> reports(int project_id, String name){
        return new Select()
                .from(Report.class)
                .where("project_id = ? AND title LIKE ?", project_id, '%' + name + '%')
                .orderBy("LOWER(title) ASC")
                .execute();
    }

    public static List<Report> reports(int project_id, String project_type, String keyword) {
        return new Select()
                .from(Report.class)
                .where("project_id = ?" +
                       "AND project_type = ? " +
                       "AND description LIKE ?", project_id, project_type, '%' + keyword + '%')
                .execute();
    }

    public static List<Report> reportsNotPushed(){
        return new Select()
                .from(Report.class)
                .where("pushed = 0")
                .execute();
    }

    public static int selectCountWithPushed(int project_id, String project_type){
        return new Select()
                .from(Report.class)
                .where("pushed = 1 AND project_id = ? AND project_type = ?", project_id, project_type)
                .execute().size();
    }

    public static List<ReportValue> reportValues(int report_id){
        return new Select()
                .from(ReportValue.class)
                .where("report_id = ?", report_id)
                .orderBy("id ASC")
                .execute();
    }

    public static List<Attachment> attachments(int report_id){
        return new Select()
                .from(Attachment.class)
                .where("report_id = ?", report_id)
                .orderBy("id ASC")
                .execute();
    }

    public static String getLatestImagePath(int report_id){
        String path = "";

        List<Attachment> list_attachment = Report.attachments(report_id);
        if (! list_attachment.isEmpty()) {
            Attachment last_attachment = list_attachment.get(list_attachment.size() - 1);
            if(last_attachment != null){
                path = last_attachment.path;
            }
        }

        return path;
    }

    public static void deleteReportsByProjectId(int project_id){
        new Update(Report.class)
                .set("pushed = 2")
                .where("project_id = ? AND project_type = 'server'", project_id)
                .execute();
    }

    public static void deleteFormNotExist(List<Form> forms) {
        Character[] placeholders_array = new Character[forms.size()];
        Long[] ids = new Long[forms.size()];
        for (int i = 0; i < forms.size(); i++) {
            placeholders_array[i] = '?';
            ids[i] = Long.valueOf(forms.get(i).form_id);
        }

        String placeholders = TextUtils.join(",", placeholders_array);

        new Delete()
                .from(Report.class)
                .where("form_id NOT IN (" + placeholders +")", ids)
                .execute();
    }

    public static void updateProjectTypeByProjectId(int project_id, String project_type){
        new Update(Report.class)
                .set("project_type = ?", project_type)
                .where("project_id = ?", project_id)
                .execute();
    }

    public static Report getLastReport(int project_id, String project_type) {
        return new Select()
                .from(Report.class)
                .where("project_id = ? " +
                       "AND project_type = ?",
                        project_id, project_type)
                .orderBy("created_at DESC")
                .executeSingle();
    }
}
