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
@Table(name = "region")
public class Region extends Model {
    @Column(name = "region_id")
    public int region_id;

    @Column(name = "name")
    public String name;

    @Column(name = "parent_id")
    public int parnet_id;

    @Column(name = "label_name")
    public String label_name;

    @Column(name = "region_order")
    public int order;

    public static Region selectSingle(int region_id){
        return new Select()
                .from(Region.class)
                .where("region_id = ?", region_id)
                .executeSingle();
    }

    public static List<Region> regions(){
        return new Select()
                .from(Region.class)
                .orderBy("id ASC")
                .execute();
    }

    public static List<Region> getAllRegionName(){
        return new Select("name")
                .from(Region.class)
                .execute();
    }

    public static void deleteTable(){
        new Delete().from(Region.class).execute();
    }

    public static List<Region> getRegionsByParentId (int parent_id){
        return new Select()
                .from(Region.class)
                .where("parent_id = ?", parent_id)
                .execute();
    }

    public static Region getRegion(String region_name){
        return new Select()
                .from(Region.class)
                .where("name LIKE ?", '%' + region_name + '%')
                .executeSingle();
    }

    public static Region getRegion(int region_id){
        return new Select()
                .from(Region.class)
                .where("region_id = ?", region_id)
                .executeSingle();
    }
}
