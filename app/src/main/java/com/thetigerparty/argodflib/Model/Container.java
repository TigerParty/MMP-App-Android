package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by fredtsao on 1/6/17.
 */
@Table(name = "container")
public class Container extends Model {
    @Column(name = "container_id")
    public int container_id;

    @Column(name = "name")
    public String name;

    @Column(name = "parent_id")
    public int parent_id;

    @Column(name = "form_id")
    public int form_id;

    @Column(name = "reportable")
    public boolean reportable;

    public static void deleteTable() {
        new Delete().from(Container.class).execute();
    }

    public static List<Container> getContainerlistByParentId(int container_parent_id) {
        return new Select()
                .from(Container.class)
                .where("parent_id = ?", container_parent_id)
                .execute();
    }

    public static Container get(int container_id) {
        return new Select().from(Container.class).where("container_id = ?", container_id).executeSingle();
    }
}
