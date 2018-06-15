package com.thetigerparty.argodflib.Subclass.Report.FieldSubview;

import com.thetigerparty.argodflib.Object.FormFieldObject;

/**
 * Created by Frankie on 3/31/16.
 */
public interface DynamicForm {
    FormFieldObject getFormFieldObject();

    void setVisibility(int visibility);

    void clearValue();
}
