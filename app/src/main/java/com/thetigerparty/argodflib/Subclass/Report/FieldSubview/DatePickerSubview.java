package com.thetigerparty.argodflib.Subclass.Report.FieldSubview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thetigerparty.argodflib.Object.FormFieldObject;
import com.thetigerparty.argodflib.Object.ShowIfObject;
import com.thetigerparty.argodflib.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ttpttp on 2016/1/7.
 */
public class DatePickerSubview extends LinearLayout implements DynamicForm {
    FormFieldObject obj_form_field;
    Context context;

    TextView tv_field_name;
    DatePicker dp;

    public DatePickerSubview(Context context) {
        super(context);
    }

    public DatePickerSubview(Context context, FormFieldObject obj_form_field) {
        super(context);

        this.context = context;
        this.obj_form_field = obj_form_field;

        setupView();
    }

    void setupView(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.report_activity_datepicker_subview, this, true);

        tv_field_name = (TextView) findViewById(R.id.tv_field_name);
        tv_field_name.setText(obj_form_field.getName());

        dp = (DatePicker) findViewById(R.id.dp_field_value);
        dp.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        dp.init(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.sdf_ymd));
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);

                obj_form_field.setValue(sdf.format(calendar.getTime()));

                for (ShowIfObject showIfObject : obj_form_field.getShowIfObjects()) {
                    if (showIfObject.getFormFieldObject().getView() != null) {
                        if (sdf.format(calendar.getTime()).equals(showIfObject.getValue())) {
                            showIfObject.getFormFieldObject().getView().setVisibility(View.VISIBLE);
                        } else {
                            showIfObject.getFormFieldObject().getView().setVisibility(View.GONE);
                            showIfObject.getFormFieldObject().setValue("");
                        }
                    }
                }
            }
        });

        //-- set value
        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.sdf_ymd));
        Calendar calendar = Calendar.getInstance();
        try {
            if(!obj_form_field.getValue().equals("")) {
                calendar.setTime(sdf.parse(obj_form_field.getValue()));

                dp.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }
            else {
                setValueWithDateNow();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setValueWithDateNow() {
        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.sdf_ymd));
        Calendar calendar = Calendar.getInstance();
        dp.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        obj_form_field.setValue(sdf.format(calendar.getTime()));
    }

    public FormFieldObject getFormFieldObject() {
        return obj_form_field;
    }

    public void clearValue() {
        obj_form_field.setValue("");
    }
}
