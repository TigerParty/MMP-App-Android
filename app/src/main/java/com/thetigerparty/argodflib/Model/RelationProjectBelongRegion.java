package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

/**
 * Created by fredtsao on 9/13/16.
 */
@Table(name = "relation_project_belong_region")
public class RelationProjectBelongRegion extends Model{

    @Column(name = "project_id")
    public int project_id;

    @Column(name = "project_type")
    public String project_type;

    @Column(name = "region_id")
    public int region_id;

    public static void deleteTable(){
        new Delete()
                .from(RelationProjectBelongRegion.class)
                .execute();
    }
}