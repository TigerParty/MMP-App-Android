package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

/**
 * Created by ttpttp on 2015/8/4.
 */
@Table(name = "report_value")
public class ReportValue extends Model {
    @Column(name = "report_id")
    public int report_id;

    @Column(name = "form_field_id")
    public int form_field_id;

    @Column(name = "value")
    public String value;

    public static void delete(int report_id){
        new Delete().from(ReportValue.class).where("report_id = ?", report_id).execute();
    }

    public static void deleteTable(){
        new Delete().from(ReportValue.class).execute();
    }
}
