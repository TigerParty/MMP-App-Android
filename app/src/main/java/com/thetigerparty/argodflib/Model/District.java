package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by ttpttp on 2015/12/21.
 */
@Table(name = "district")
public class District extends Model {
    @Column(name = "district_id")
    public int district_id;

    @Column(name = "region_id")
    public int region_id;

    @Column(name = "name")
    public String name;

    public static List<District> select(int region_id){
        return new Select()
                .from(District.class)
                .where("region_id = ?", region_id)
                .orderBy("id ASC")
                .execute();
    }

    public static District selectSingle(int district_id){
        return new Select()
                .from(District.class)
                .where("district_id = ?", district_id)
                .executeSingle();
    }

    public static List<District> getAllDistrictName(){
        return new Select("name")
                .from(District.class)
                .execute();
    }

    public static void deleteTable(){
        new Delete().from(District.class).execute();
    }
}
