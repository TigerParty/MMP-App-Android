package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

/**
 * Created by fredtsao on 2017/4/27.
 */
@Table(name = "relation_user_own_project")
public class RelationUserOwnProject extends Model {
    @Column(name = "project_id")
    public int project_id;

    @Column(name = "project_type")
    public String project_type;

    @Column(name = "user_id")
    public int user_id;

    public static void deleteTable() {
        new Delete()
                .from(RelationUserOwnProject.class)
                .execute();
    }
}
