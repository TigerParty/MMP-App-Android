package com.thetigerparty.argodflib.Component;

import com.thetigerparty.argodflib.Model.Region;
import com.thetigerparty.argodflib.Object.RegionObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fredtsao on 9/14/16.
 */
public class RegionComponent {

    List<RegionObject> region_collection = new ArrayList<>();

    public RegionComponent () {
        super();
        for(Region region:Region.regions()){
            this.region_collection.add(
                    new RegionObject(
                            region.region_id,
                            region.name,
                            region.parnet_id,
                            region.label_name,
                            region.order)
            );
        }
    }

    private RegionObject array_search(String region_name){
        RegionObject result = new RegionObject();
        for(RegionObject region : region_collection){
            if(region.getName().equals(region_name)){
                result = region;
            }
        }
        return result;
    }

    public RegionObject getRegion (String region_name) {
        return array_search(region_name);
    }
}
