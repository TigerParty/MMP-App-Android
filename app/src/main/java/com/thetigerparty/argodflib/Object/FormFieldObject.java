package com.thetigerparty.argodflib.Object;

import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ttpttp on 2015/8/5.
 */
public class FormFieldObject implements Serializable{

    int id = 0;
    FieldObject obj_field = new FieldObject();
    String name = "";
    String default_value = "";
    String options = "";
    String value = "";
    int order = 0;
    int editLevelPriority = 0;
    String show_if = "";
    boolean is_validate = true;
    boolean is_required = false;
    private ArrayList<ShowIfObject> showIfObjects = new ArrayList<>();
    View view;
    String formula = "";

    public FormFieldObject(){
        super();
    }

    public FormFieldObject(int id,
                           FieldObject obj_field,
                           String name,
                           String default_value,
                           String options,
                           String value,
                           int order,
                           String show_if,
                           int editLevelPriority,
                           boolean is_required,
                           ArrayList<ShowIfObject> showIfObjects,
                           View view,
                           String formula){
        super();

        this.id = id;
        this.obj_field = obj_field;
        this.name = name;
        this.default_value = default_value;
        this.options = options;
        this.value = value;
        this.order = order;

        if (show_if != null) {
            this.show_if = show_if;
        }

        this.editLevelPriority = editLevelPriority;

        this.is_required = is_required;
        this.showIfObjects = showIfObjects;
        this.view = view;
        this.formula = formula;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setField(FieldObject obj_field) {
        this.obj_field = obj_field;
    }

    public FieldObject getField() {
        return obj_field;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDefaultValue(String default_value) {
        this.default_value = default_value;
    }

    public String getDefaultValue() {
        return default_value;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getOptions() {
        return options;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public void setIsValidate(boolean is_validate) {
        this.is_validate = is_validate;
    }

    public boolean getIsValidate() {
        return is_validate;
    }

    public void setEditLevelPriority(int editLevelPriority) {
        this.editLevelPriority = editLevelPriority;
    }

    public int getEditLevelPriority() {
        return editLevelPriority;
    }

    public String getShowIf() { return show_if; }

    public void setIsRequired(boolean is_required){
        this.is_required = is_required;
    }

    public boolean getIsRequired() {
        return is_required;
    }

    public void setShowIfObjects(ArrayList<ShowIfObject> showIfObjects) {
        this.showIfObjects = showIfObjects;
    }

    public ArrayList<ShowIfObject> getShowIfObjects() {
        return showIfObjects;
    }

    public void setView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getFormula() {
        return formula;
    }
}
