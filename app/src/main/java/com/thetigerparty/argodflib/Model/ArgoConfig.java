package com.thetigerparty.argodflib.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

/**
 * Created by fredtsao on 12/20/16.
 */
@Table(name = "argo_config")
public class ArgoConfig extends Model {

    @Column(name = "key")
    public String key;
    @Column(name = "value")
    public String value;

    public static String getUsername() {
        ArgoConfig usernameConfig = new Select("value").from(ArgoConfig.class).where("`key` = 'remember_username'").executeSingle();
        return usernameConfig.value;
    }

    public static String getPassword() {
        ArgoConfig usernameConfig = new Select("value").from(ArgoConfig.class).where("`key` = 'remember_password'").executeSingle();
        return usernameConfig.value;
    }

    public static boolean hasConfig() {
        int count = new Select().from(ArgoConfig.class).execute().size();
        return count > 0;
    }

    public static void updateUsernameAndPassword(String username, String password) {
        new Update(ArgoConfig.class).set("`value` = ?", username).where("`key` = 'remember_username'").execute();
        new Update(ArgoConfig.class).set("`value` = ?", password).where("`key` = 'remember_password'").execute();
    }

    public static String getReporterName() {
        ArgoConfig reporterConfig = new Select("value").from(ArgoConfig.class).where("`key` = 'remember_reporter_name'").executeSingle();
        return reporterConfig.value;
    }

    public static String getReporterEmail() {
        ArgoConfig reporterConfig = new Select("value").from(ArgoConfig.class).where("`key` = 'remember_reporter_email'").executeSingle();
        return reporterConfig.value;
    }

    public static void updateReporterEmailAndName(String email, String name) {
        new Update(ArgoConfig.class).set("`value` = ?", email).where("`key` = 'remember_reporter_email'").execute();
        new Update(ArgoConfig.class).set("`value` = ?", name).where("`key` = 'remember_reporter_name'").execute();
    }
}
