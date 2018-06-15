package com.thetigerparty.argodflib.Subclass.Report.FieldSubview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.thetigerparty.argodflib.Object.FormFieldObject;
import com.thetigerparty.argodflib.Object.ShowIfObject;
import com.thetigerparty.argodflib.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttpttp on 2015/8/12.
 */
public class DropDownListSubview extends LinearLayout implements DynamicForm {
    FormFieldObject obj_form_field;
    Context context;

    TextView tv_field_name;
    Spinner spinner_field_value;

    public DropDownListSubview(Context context) {
        super(context);
    }

    public DropDownListSubview(Context context, FormFieldObject obj_form_field) {
        super(context);

        this.context = context;
        this.obj_form_field = obj_form_field;

        setupView();
    }

    void setupView(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.report_activity_drop_down_list_subview, this, true);

        tv_field_name = (TextView) findViewById(R.id.tv_field_name);
        tv_field_name.setText(obj_form_field.getName());

        spinner_field_value = (Spinner) findViewById(R.id.spinner_field_value);

        final List<String> list_drop_options = new ArrayList<>();
        try{
            JSONArray array_options = new JSONArray(obj_form_field.getOptions());
            for(int i = 0; i < array_options.length(); i++){
                list_drop_options.add(array_options.getString(i));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter_drop = new ArrayAdapter<>(context,
                                                               android.R.layout.simple_spinner_item,
                                                               list_drop_options);
        adapter_drop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_field_value.setAdapter(adapter_drop);

        spinner_field_value.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                obj_form_field.setValue(list_drop_options.get(position));

                for (ShowIfObject showIfObject : obj_form_field.getShowIfObjects()) {
                    if (showIfObject.getFormFieldObject().getView() != null) {
                        if (list_drop_options.get(position).equals(showIfObject.getValue())) {
                            showIfObject.getFormFieldObject().getView().setVisibility(View.VISIBLE);
                        } else {
                            showIfObject.getFormFieldObject().getView().setVisibility(View.GONE);
                            showIfObject.getFormFieldObject().setValue("");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //-- init drop down list selected value
        spinner_field_value.setSelection(obj_form_field.getValue().equals("") ?
                        list_drop_options.indexOf(obj_form_field.getDefaultValue()) :
                        list_drop_options.indexOf(obj_form_field.getValue())
        );
    }

    public FormFieldObject getFormFieldObject() {
        return obj_form_field;
    }

    public void clearValue() {
        spinner_field_value.setSelection(0);
    }
}
