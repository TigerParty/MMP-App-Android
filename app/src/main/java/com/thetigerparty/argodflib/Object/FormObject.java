package com.thetigerparty.argodflib.Object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttpttp on 2015/8/5.
 */
public class FormObject implements Serializable{

    int id = 0;
    String name = "";
    List<FormFieldObject> list_obj_form_field = new ArrayList<>();
    boolean is_photo_required = false;

    public FormObject(){
        super();
    }

    public FormObject(
            int id,
            String name,
            List<FormFieldObject> list_obj_form_field,
            boolean is_photo_required
    ){
        super();

        this.id = id;
        this.name = name;
        this.list_obj_form_field = list_obj_form_field;
        this.is_photo_required = is_photo_required;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setListFormField(List<FormFieldObject> list_obj_form_field) {
        this.list_obj_form_field = list_obj_form_field;
    }

    public List<FormFieldObject> getListFormField() {
        return list_obj_form_field;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void setIsPhotoRequired(boolean is_photo_required){
        this.is_photo_required = is_photo_required;
    }

    public boolean getIsPhotoRequired(){
        return is_photo_required;
    }
}
