package com.thetigerparty.argodflib.Subclass.ProjectList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.thetigerparty.argodflib.Model.Project;
import com.thetigerparty.argodflib.Model.Report;
import com.thetigerparty.argodflib.Object.ProjectObject;
import com.thetigerparty.argodflib.ProjectListActivity;
import com.thetigerparty.argodflib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttpttp on 2015/8/17.
 */
public class ProjectListViewAdapter extends BaseAdapter {

    ProjectListActivity activity;
    List<ProjectObject> list_obj_project = new ArrayList<>();

    public ProjectListViewAdapter(ProjectListActivity activity, List<ProjectObject> list_obj_project){
        this.activity = activity;
        this.list_obj_project = list_obj_project;
    }

    static class ViewHolder{
        TextView tv_project_name;
        Button bt_create;
        Button bt_edit;
    }

    @Override
    public int getCount() {
        return list_obj_project.size();
    }

    @Override
    public Object getItem(int position) {
        return list_obj_project.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.project_list_content, parent, false);

            holder = new ViewHolder();

            holder.tv_project_name = (TextView) convertView.findViewById(R.id.tv_project_name);
            holder.bt_create = (Button) convertView.findViewById(R.id.bt_create);
            holder.bt_edit = (Button) convertView.findViewById(R.id.bt_edit);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        //-- disable create button once pass new project with new report via sms
        ProjectObject projectOB = list_obj_project.get(position);
        List<Report> reports = (Report.reports(projectOB.getId(), ""));
        if ((projectOB.getType() == "new") && (reports.size() > 0) && (reports.get(0).pushed == 1)) {
            holder.bt_create.setEnabled(false);
        }
        else {
            holder.bt_create.setEnabled(true);
        }

        holder.tv_project_name.setText(list_obj_project.get(position).getTitle());
        holder.bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.createOnClick(list_obj_project.get(position));
            }
        });
        holder.bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.editOnClick(list_obj_project.get(position));
            }
        });

        return convertView;
    }
}
