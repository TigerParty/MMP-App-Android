package com.thetigerparty.argodflib.Subclass.ProjectList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thetigerparty.argodflib.ContainerActivity;
import com.thetigerparty.argodflib.Object.ProjectObject;
import com.thetigerparty.argodflib.R;
import com.thetigerparty.argodflib.RootProjectListActivity;

import java.util.List;

/**
 * Created by fredtsao on 1/5/17.
 */

public class ProjectsListViewAdapter extends BaseAdapter {

    Activity activity;
    List<ProjectObject> list_project_obj;

    public ProjectsListViewAdapter(RootProjectListActivity activity, List<ProjectObject> list_project_obj) {
        this.activity = activity;
        this.list_project_obj = list_project_obj;
    }

    public ProjectsListViewAdapter(ContainerActivity activity, List<ProjectObject> list_project_obj) {
        this.activity = activity;
        this.list_project_obj = list_project_obj;
    }

    static class ViewHolder {
        TextView tv_project_name;
    }

    @Override
    public int getCount() {
        return list_project_obj.size();
    }

    @Override
    public Object getItem(int position) {
        return list_project_obj.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list_project_obj.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.project_list_view_content, parent, false);

            holder = new ViewHolder();
            holder.tv_project_name = (TextView)convertView.findViewById(R.id.tv_project_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        final ProjectObject project_obj = list_project_obj.get(position);
        holder.tv_project_name.setText(project_obj.getTitle());

        return convertView;
    }
}
