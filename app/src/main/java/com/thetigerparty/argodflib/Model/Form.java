package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by ttpttp on 2015/8/4.
 */
@Table(name = "form")
public class Form extends Model {
    @Column(name = "form_id")
    public int form_id;

    @Column(name = "name")
    public String name;

    @Column(name = "is_photo_required")
    public boolean is_photo_required;

    public static Form selectSingle(int form_id){
        return new Select()
                .from(Form.class)
                .where("form_id = ?", form_id)
                .executeSingle();
    }

    public static List<Form> forms(){
        return new Select()
                .from(Form.class)
                .orderBy("id ASC")
                .execute();
    }

    public static List<FormField> formFields(int form_id){
        return new Select()
                .from(FormField.class)
                .where("form_id = ?", form_id)
                .orderBy("field_order ASC")
                .execute();
    }

    public static void deleteTable(){
        new Delete().from(Form.class).execute();
    }

    public static Form getDefaultForm() {
        return new Select()
                .from(Form.class)
                .executeSingle();
    }
}
