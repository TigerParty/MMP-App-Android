package com.thetigerparty.argodflib.Object;

import java.io.Serializable;

/**
 * Created by ttpttp on 2015/12/21.
 */
public class DistrictObject implements Serializable {
    int id = 0;
    int region_id;
    String name;

    public DistrictObject(){super();}

    public DistrictObject(int id, int region_id, String name){
        super();

        this.id = id;
        this.region_id = region_id;
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

    public void setRegionId(int region_id) {
        this.region_id = region_id;
    }

    public int getRegionId() {
        return region_id;
    }
}
