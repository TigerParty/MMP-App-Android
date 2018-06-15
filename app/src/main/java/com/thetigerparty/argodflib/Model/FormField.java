package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

/**
 * Created by ttpttp on 2015/8/4.
 */
@Table(name = "form_field")
public class FormField extends Model {
    @Column(name = "form_field_id")
    public int form_field_id;

    @Column(name = "name")
    public String name;

    @Column(name = "form_id")
    public int form_id;

    @Column(name = "field_id")
    public int field_id;

    @Column(name = "default_value")
    public String default_value;

    @Column(name = "options")
    public String options;

    @Column(name = "field_order")
    public int order;

    @Column(name = "show_if")
    public String show_if;

    @Column(name = "is_required")
    public boolean is_required;

    @Column(name = "edit_level_priority")
    public int edit_level_priority;

    @Column(name = "formula")
    public String formula;

    public static void deleteTable(){
        new Delete().from(FormField.class).execute();
    }
}
