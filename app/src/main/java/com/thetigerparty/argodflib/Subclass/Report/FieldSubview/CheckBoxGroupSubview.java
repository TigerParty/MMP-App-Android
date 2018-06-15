package com.thetigerparty.argodflib.Subclass.Report.FieldSubview;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thetigerparty.argodflib.Object.FormFieldObject;
import com.thetigerparty.argodflib.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by fredtsao on 2016/5/25.
 */
public class CheckBoxGroupSubview extends LinearLayout implements DynamicForm {
    FormFieldObject obj_form_field;
    Context context;

    TextView tv_field_value;

    JSONArray cg_field_value = new JSONArray();
    JSONObject cg_json_value = new JSONObject();

    List<CheckBox> cg_field_options = new ArrayList<>();

    public CheckBoxGroupSubview (Context context) {
        super(context);
    }
    public CheckBoxGroupSubview (Context context, FormFieldObject obj_form_field) {
        super(context);
        this.context = context;
        this.obj_form_field = obj_form_field;

        setView();
    }

    private List<String> get_options () {
        final List<String> list_checkbox_options = new ArrayList<>();
        try {
            JSONArray array_options = new JSONArray(obj_form_field.getOptions());
            for (int i = 0; i < array_options.length(); i++) {
                list_checkbox_options.add(array_options.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list_checkbox_options;
    }

    private List<String> get_Values () {
        final List<String> list_checkbox_values = new ArrayList<>();
        try {
            if(!obj_form_field.getValue().equals("")){
                JSONArray array_values = new JSONArray(obj_form_field.getValue());
                for (int i = 0; i < array_values.length(); i++){
                    list_checkbox_values.add(array_values.getString(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list_checkbox_values;
    }

    private List<String> get_defaults () {
        final List<String> list_checkbox_defaults = new ArrayList<>();
        try {
            if(!obj_form_field.getDefaultValue().equals("")){
                String[] defaults = obj_form_field.getDefaultValue().split(",");
                for (int i= 0; i < defaults.length; i++){
                    list_checkbox_defaults.add(defaults[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list_checkbox_defaults;
    }

    private void setValues(String valueName, boolean isChecked) {
        try {
            cg_json_value.put(valueName, isChecked);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        valuesToString();
    }

    private void valuesToString() {
        cg_field_value = new JSONArray();
        Iterator<String> iter = cg_json_value.keys();
        try {
            while (iter.hasNext()) {
                String key = iter.next();

                if(cg_json_value.get(key).equals(true)){
                    cg_field_value.put(key);
                }
            }
        } catch (JSONException JE) {
            JE.printStackTrace();
        }

        obj_form_field.setValue(cg_field_value.toString());
    }

    void setView (){
        final LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.report_activity_check_box_group_subview, this, true);
        final LinearLayout cg_field_layout = (LinearLayout)findViewById(R.id.cg_field_layout);

        tv_field_value = (TextView) findViewById(R.id.tv_field_name);
        tv_field_value.setText(obj_form_field.getName());

        List<String> list_checkbox_defaults = get_defaults();
        List<String> list_checkbox_options = get_options();
        List<String> list_checkbox_values = get_Values();

        for (int i = 0; i < list_checkbox_options.size(); i++) {
            final CheckBox cg_field_option = new CheckBox(context);
            cg_field_option.setId(i);
            cg_field_option.setText(list_checkbox_options.get(i));

            boolean noValues = list_checkbox_values.size() == 0;
            boolean hasDefaults = list_checkbox_defaults.size() > 0;

            if(noValues) {
                if (hasDefaults){
                    for(int j = 0; j < list_checkbox_defaults.size(); j++) {
                        if(list_checkbox_options.get(i).equals(list_checkbox_defaults.get(j))){
                            cg_field_option.setChecked(true);
                            setValues(cg_field_option.getText().toString(), cg_field_option.isChecked());
                        }
                    }
                }
            } else {
                for (int k = 0; k < list_checkbox_values.size(); k++){
                    if(list_checkbox_options.get(i).equals(list_checkbox_values.get(k))){
                        cg_field_option.setChecked(true);
                        setValues(cg_field_option.getText().toString(), cg_field_option.isChecked());
                    }
                }
            }

            cg_field_option.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setValues(cg_field_option.getText().toString(), cg_field_option.isChecked());
                    if(obj_form_field.getIsRequired() && !obj_form_field.getValue().isEmpty()){
                        cg_field_option.setError(null);
                    }
                }
            });

            cg_field_options.add(cg_field_option);
            cg_field_layout.addView(cg_field_option);
        }
    }

    @Override
    public FormFieldObject getFormFieldObject() {
        return obj_form_field;
    }

    @Override
    public void clearValue() {
        for(int i = 0; i < get_options().size(); i++){
            CheckBox option = cg_field_options.get(i);
            option.setChecked(false);
        }
    }
}
