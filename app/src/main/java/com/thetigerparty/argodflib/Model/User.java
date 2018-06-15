package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

/**
 * Created by ttpttp on 2015/8/4.
 */
@Table(name = "user")
public class User extends Model {
    @Column(name = "user_id")
    public int user_id;

    @Column(name = "name")
    public String name;

    @Column(name = "password")
    public String password;

    @Column(name = "permission_level_id")
    public int permission_level_id;

    @Column(name = "email")
    public String email;

    public static User select(String name) {
        return new Select()
                .from(User.class)
                .where("name = ?", name)
                .executeSingle();
    }

    public static User get(int userId) {
        return new Select()
                .from(User.class)
                .where("user_id = ? ", userId)
                .executeSingle();
    }


    public static void deleteTable() {
        new Delete().from(User.class).execute();
    }
}
