package com.thetigerparty.argodflib.Subclass.ContainerList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thetigerparty.argodflib.ContainerActivity;
import com.thetigerparty.argodflib.Object.ContainerObject;
import com.thetigerparty.argodflib.R;
import com.thetigerparty.argodflib.ProjectActivity;

import java.util.List;

/**
 * Created by fredtsao on 1/6/17.
 */

public class ContainerListViewAdapter extends BaseAdapter {

    Activity activity;

    List<ContainerObject> list_container_obj;

    public ContainerListViewAdapter(ProjectActivity activity, List<ContainerObject> list_container_obj) {
        this.activity = activity;
        this.list_container_obj = list_container_obj;
    }

    public ContainerListViewAdapter(ContainerActivity activity, List<ContainerObject> list_container_obj) {
        this.activity = activity;
        this.list_container_obj = list_container_obj;
    }

    static class ViewHolder {
        TextView tv_container_name;
    }

    @Override
    public int getCount() {
        return list_container_obj.size();
    }

    @Override
    public Object getItem(int position) {
        return list_container_obj.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list_container_obj.get(position).getContainerId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.container_list_view_content, parent, false);

            holder = new ViewHolder();
            holder.tv_container_name = (TextView)convertView.findViewById(R.id.tv_container_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        final ContainerObject containerObject = list_container_obj.get(position);
        holder.tv_container_name.setText(containerObject.getContainerName());

        return convertView;
    }
}
