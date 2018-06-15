package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

/**
 * Created by ttpttp on 2015/8/4.
 */
@Table(name = "field")
public class Field extends Model{
    @Column(name = "field_id")
    public int field_id;

    @Column(name = "name")
    public String name;

    public static Field select(int field_id){
        return new Select()
                .from(Field.class)
                .where("field_id = ?", field_id)
                .executeSingle();
    }

    public static void deleteTable(){
        new Delete().from(Field.class).execute();
    }
}
