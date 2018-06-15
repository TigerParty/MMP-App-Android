package com.thetigerparty.argodflib.Subclass.Report.FieldSubview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.thetigerparty.argodflib.Object.FormFieldObject;
import com.thetigerparty.argodflib.Object.ShowIfObject;
import com.thetigerparty.argodflib.R;

/**
 * Created by ttpttp on 2015/8/12.
 */
public class CheckBoxSubview extends LinearLayout implements DynamicForm {
    FormFieldObject obj_form_field;
    Context context;

    CheckBox cb_field_value;
    public CheckBoxSubview(Context context) {
        super(context);
    }

    public CheckBoxSubview(Context context, FormFieldObject obj_form_field) {
        super(context);

        this.context = context;
        this.obj_form_field = obj_form_field;

        setupView();
    }

    void setupView(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.report_activity_check_box_subview, this, true);

        cb_field_value = (CheckBox) findViewById(R.id.cb_field_value);
        cb_field_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obj_form_field.setValue(cb_field_value.isChecked() ?
                                              "yes" :
                                              "no"
                );

                for (ShowIfObject showIfObject : obj_form_field.getShowIfObjects()) {
                    if (showIfObject.getFormFieldObject().getView() != null) {
                        if ((cb_field_value.isChecked() ? "yes" : "no").equals(showIfObject.getValue())) {
                            showIfObject.getFormFieldObject().getView().setVisibility(View.VISIBLE);
                        } else {
                            showIfObject.getFormFieldObject().getView().setVisibility(View.GONE);
                            showIfObject.getFormFieldObject().setValue("");
                        }
                    }
                }
            }
        });
        cb_field_value.setText(obj_form_field.getName());

        //-- init check box checked
        cb_field_value.setChecked(obj_form_field.getValue().equals("yes"));
    }

    public FormFieldObject getFormFieldObject() {
        return obj_form_field;
    }

    public void clearValue() {
        cb_field_value.setChecked(false);
    }
}
