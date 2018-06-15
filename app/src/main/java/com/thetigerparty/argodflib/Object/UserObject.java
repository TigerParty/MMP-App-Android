package com.thetigerparty.argodflib.Object;

import java.io.Serializable;

/**
 * Created by ttpttp on 2015/8/4.
 */
public class UserObject implements Serializable {

    int user_id = 0;
    String name = "";
    String password = "";
    int permissionPriority = 4;
    String email = "";

    public UserObject(){
        super();
    }

    public UserObject(int user_id, String name, String password, int permissionPriority, String email){
        super();

        this.user_id = user_id;
        this.name = name;
        this.password = password;
        this.permissionPriority = permissionPriority;
        this.email = email;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPermissionPriority(int permissionPriority) {
        this.permissionPriority = permissionPriority;
    }

    public int getPermissionPriority() {
        return permissionPriority;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
