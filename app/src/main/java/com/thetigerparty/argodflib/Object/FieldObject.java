package com.thetigerparty.argodflib.Object;

import java.io.Serializable;

/**
 * Created by ttpttp on 2015/8/5.
 */
public class FieldObject implements Serializable{

    int id;
    String name;

    public FieldObject(){
        super();
    }

    public FieldObject(int id, String name){
        super();

        this.id = id;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
