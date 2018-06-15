package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Created by ttpttp on 2015/8/4.
 */
@Table(name = "setting")
public class Setting extends Model {
    @Column(name = "last_synced_datetime")
    public String last_synced_datetime;

    public static Setting select(){
        return new Select()
                .from(Setting.class)
                .where("id = ?", 1)
                .executeSingle();
    }
}
