package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

/**
 * Created by fredtsao on 2017/4/27.
 */
@Table(name = "permission_level")
public class PermissionLevel extends Model {
    @Column(name = "permission_id")
    public int permission_id;

    @Column(name = "name")
    public String name;

    @Column(name = "priority")
    public int priority;

    public static void deleteTable() {
        new Delete()
                .from(PermissionLevel.class)
                .execute();
    }

    public static int getPriorityById(int permissionId) {
        PermissionLevel permission = new Select()
                .from(PermissionLevel.class)
                .where("permission_id = ? ", permissionId)
                .executeSingle();
        return permission.priority;
    }
}
