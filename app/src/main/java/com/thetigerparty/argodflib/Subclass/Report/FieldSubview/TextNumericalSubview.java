package com.thetigerparty.argodflib.Subclass.Report.FieldSubview;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thetigerparty.argodflib.HelperClass.Validator;
import com.thetigerparty.argodflib.Object.FormFieldObject;
import com.thetigerparty.argodflib.Object.ShowIfObject;
import com.thetigerparty.argodflib.R;

/**
 * Created by ttpttp on 2015/12/9.
 */
public class TextNumericalSubview extends LinearLayout implements DynamicForm {
    FormFieldObject obj_form_field;
    Context context;

    TextView tv_field_name;
    EditText et_field_value;

    public TextNumericalSubview(Context context) {
        super(context);
    }

    public TextNumericalSubview(Context context, FormFieldObject obj_form_field){
        super(context);

        this.context = context;
        this.obj_form_field = obj_form_field;

        setupView();
    }

    void setupView(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.report_activity_text_box_subview, this, true);

        tv_field_name = (TextView) findViewById(R.id.tv_field_name);
        tv_field_name.setText(obj_form_field.getName());

        et_field_value = (EditText) findViewById(R.id.et_field_value);
        et_field_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String value = et_field_value.getText().toString();

                if (value.isEmpty()) {
                    if (obj_form_field.getIsRequired()) {
                        et_field_value.setError(context.getString(R.string.et_error_is_required));
                    }
                } else {
                    if (Validator.isNumeric(value)) {
                        et_field_value.setError(null);
                        obj_form_field.setIsValidate(true);
                    } else {
                        et_field_value.setError(context.getString(R.string.et_error_should_numeric));
                        obj_form_field.setIsValidate(false);
                    }
                }

                obj_form_field.setValue(et_field_value.getText().toString());


                for (ShowIfObject showIfObject : obj_form_field.getShowIfObjects()) {
                    if (showIfObject.getFormFieldObject().getView() != null) {
                        if (s.toString().equals(showIfObject.getValue())) {
                            showIfObject.getFormFieldObject().getView().setVisibility(View.VISIBLE);
                        } else {
                            showIfObject.getFormFieldObject().getView().setVisibility(View.GONE);
                            showIfObject.getFormFieldObject().setValue("");
                        }
                    }
                }
            }
        });

        //-- init text area value
        et_field_value.setText(obj_form_field.getValue().equals("") ?
                        obj_form_field.getDefaultValue() :
                        obj_form_field.getValue()
        );
    }

    public FormFieldObject getFormFieldObject() {
        return obj_form_field;
    }

    public void clearValue() {
        obj_form_field.setValue("");
        et_field_value.setText("");
    }
}
