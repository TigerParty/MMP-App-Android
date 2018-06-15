package com.thetigerparty.argodflib.Object;

import java.io.Serializable;

/**
 * Created by ttpttp on 2017/6/8.
 */

public class ShowIfObject implements Serializable {
    private FormFieldObject formFieldObject;
    private String value;

    public void setFormFieldObject(FormFieldObject formFieldObject) {
        this.formFieldObject = formFieldObject;
    }

    public FormFieldObject getFormFieldObject() {
        return formFieldObject;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
