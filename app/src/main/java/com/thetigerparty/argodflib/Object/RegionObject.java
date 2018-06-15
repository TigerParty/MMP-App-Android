package com.thetigerparty.argodflib.Object;

import java.io.Serializable;

/**
 * Created by ttpttp on 2015/12/21.
 */
public class RegionObject implements Serializable {
    int id = 0;
    String name = "";
    int parent_id = 0;
    String label_name;
    int order = 0;

    public RegionObject(){super();}

    public RegionObject(
            int id,
            String name,
            int parent_id,
            String label_name,
            int order
    ){
        super();

        this.id = id;
        this.name = name;
        this.parent_id = parent_id;
        this.label_name = label_name;
        this.order = order;
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

    public void setParent_id(int parent_id){
        this.parent_id = parent_id;
    }

    public int getParent_id(){
        return parent_id;
    }

    public void setLabel_name(String label_name){
        this.label_name = label_name;
    }

    public String getLabel_name(){
        return label_name;
    }

    public void setOrder(int order){
        this.order = order;
    }

    public int getOrder(){
        return order;
    }
}
