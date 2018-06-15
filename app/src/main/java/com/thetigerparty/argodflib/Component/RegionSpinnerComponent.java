package com.thetigerparty.argodflib.Component;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.thetigerparty.argodflib.Model.Region;
import com.thetigerparty.argodflib.Object.RegionObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by fredtsao on 9/20/16.
 */
public class RegionSpinnerComponent extends LinearLayout {
    private LinearLayout parent_layout;
    private Context target_context;
    private ArrayList<RegionObject> regions;
    private List<Region> list_region_by_project;

    private RegionSpinnerComponent child_component;
    private Set selected_region_collection;

    public RegionSpinnerComponent(
            String region_label,
            ArrayList<RegionObject> regions,
            LinearLayout target_layout,
            List<Region> list_region_by_project,
            Set selected_region_collection,
            Context context
    ){
        super(context);

        this.parent_layout = target_layout;
        this.target_context = context;
        this.regions = regions;
        this.list_region_by_project = list_region_by_project;
        this.selected_region_collection = selected_region_collection;

        ArrayList<String> regions_name = toArrayRegionName(regions);

        TextView tv_label_name = create_region_label(context, region_label);
        Spinner spinner_regions = create_spinner(context, regions_name);
        init_default_region(spinner_regions, regions_name);
        set_onSelect_listener(spinner_regions);

        this.addView(tv_label_name);
        this.addView(spinner_regions);
    }

    private TextView create_region_label (Context context, String region_label) {
        TextView tv_label_name = new TextView(context);
        String Label = region_label.toUpperCase().charAt(0) + region_label.substring(1);
        tv_label_name.setText(Label);
        tv_label_name.setTextSize(20);
        return tv_label_name;
    }

    private Spinner create_spinner (Context context, ArrayList<String> regions_name) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, regions_name);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = new Spinner(context);
        spinner.setAdapter(adapter);
        return spinner;
    }

    private void init_default_region (Spinner spinner_regions, ArrayList<String> regions_name){
        back:
        for(Region select_region: list_region_by_project){
            for(String region_name : regions_name){
                if(region_name.equals(select_region.name)){
                    spinner_regions.setSelection(regions_name.indexOf(select_region.name));
                    continue back;
                }
            }
        }
    }

    private ArrayList<String> toArrayRegionName(ArrayList<RegionObject> regions){
        ArrayList<String> regions_name = new ArrayList<>();
        for(RegionObject region : regions){
            regions_name.add(region.getName());
        }
        return regions_name;
    }

    private ArrayList<RegionObject> toArrayRegionObject(List<Region> regions){
        ArrayList<RegionObject> array_regions = new ArrayList<>();
        for(Region region: regions){
            array_regions.add(new RegionObject(
                    region.region_id,
                    region.name,
                    region.parnet_id,
                    region.label_name,
                    region.order
            ));
        }
        return array_regions;
    }

    private void select_changed(RegionObject region){

        clean_up_children_components();

        if(region.getId() != 0){
            this.selected_region_collection.add(region.getId());
            List<Region> regions = Region.getRegionsByParentId(region.getId());
            ArrayList<RegionObject> child_regions = toArrayRegionObject(regions);

            if(!child_regions.isEmpty()){
                String region_label = child_regions.get(0).getLabel_name();

                RegionSpinnerComponent next_region_component = new RegionSpinnerComponent(
                        region_label,
                        child_regions,
                        parent_layout,
                        list_region_by_project,
                        selected_region_collection,
                        target_context
                );
                this.child_component = next_region_component;

                parent_layout.addView(next_region_component);
            }
        }
    }

    private void set_onSelect_listener (Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_changed(get_region_by_position(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private RegionObject get_region_by_position(int position){
        return regions.get(position);
    }

    private void clean_up_children_components() {
        try {
            remove_old_selected_region_id();
            child_component.removeAllViews();
        } catch (Throwable throwable) {
        }
    }

    public Set get_selected_region_collection () {
        return selected_region_collection;
    }

    private void remove_old_selected_region_id () {
        for (RegionObject region :regions){
            selected_region_collection.remove(region.getId());
        }
    }

    @Override
    public void removeAllViews() {
        remove_old_selected_region_id();
        super.removeAllViews();
        child_component.removeAllViews();
    }
}
