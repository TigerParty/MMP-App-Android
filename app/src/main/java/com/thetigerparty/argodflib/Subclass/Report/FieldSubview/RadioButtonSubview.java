package com.thetigerparty.argodflib.Subclass.Report.FieldSubview;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.thetigerparty.argodflib.Object.FormFieldObject;
import com.thetigerparty.argodflib.Object.ShowIfObject;
import com.thetigerparty.argodflib.R;
import com.thetigerparty.argodflib.ReportActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttpttp on 2015/8/12.
 */
public class RadioButtonSubview extends LinearLayout implements DynamicForm {
    FormFieldObject obj_form_field;
    ReportActivity context;

    TextView tv_field_name;
    RadioGroup rg_field_value;

    public RadioButtonSubview(Context context) {
        super(context);
    }

    public RadioButtonSubview(ReportActivity context, FormFieldObject obj_form_field) {
        super(context);

        this.context = context;
        this.obj_form_field = obj_form_field;

        setupView();
    }

    void setupView(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.report_activity_radio_button_subview, this, true);

        tv_field_name = (TextView) findViewById(R.id.tv_field_name);
        tv_field_name.setText(obj_form_field.getName());

        final List<String> list_radio_options = new ArrayList<>();
        try{
            JSONArray array_options = new JSONArray(obj_form_field.getOptions());
            for(int i = 0; i < array_options.length(); i++){
                list_radio_options.add(array_options.getString(i));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        rg_field_value = (RadioGroup) findViewById(R.id.rg_field_value);
        rg_field_value.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.getChildAt(group.indexOfChild(group.findViewById(checkedId)));

                String value = (rb != null) ? rb.getText().toString() : "";
                obj_form_field.setValue(value);

                for (ShowIfObject showIfObject : obj_form_field.getShowIfObjects()) {
                    if (showIfObject.getFormFieldObject().getView() != null) {
                        if (value.equals(showIfObject.getValue())) {
                            showIfObject.getFormFieldObject().getView().setVisibility(View.VISIBLE);
                        } else {
                            showIfObject.getFormFieldObject().getView().setVisibility(View.GONE);
                            showIfObject.getFormFieldObject().setValue("");
                        }
                    }
                }

                Message message = context.handler.obtainMessage(context.OPTION_CHANGED_IN_DYNAMIC_FORM, obj_form_field);
                context.handler.sendMessage(message);
            }
        });

        final RadioButton[] rb_option = new RadioButton[list_radio_options.size()];
        for(int i = 0; i < list_radio_options.size(); i++){
            rb_option[i] = new RadioButton(context);
            rb_option[i].setText(list_radio_options.get(i));
            rg_field_value.addView(rb_option[i]);

            //-- init radio button checked
            if(obj_form_field.getValue().equals("")){
                if(list_radio_options.get(i).equals(obj_form_field.getDefaultValue())){
                    rb_option[i].setChecked(true);
                }
            }
            else{
                if(list_radio_options.get(i).equals(obj_form_field.getValue())){
                    rb_option[i].setChecked(true);
                }
            }
        }
    }

    public FormFieldObject getFormFieldObject() {
        return obj_form_field;
    }

    public void clearValue() {
        rg_field_value.clearCheck();
    }
}
