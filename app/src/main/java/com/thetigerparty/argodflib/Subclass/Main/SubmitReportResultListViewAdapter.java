package com.thetigerparty.argodflib.Subclass.Main;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thetigerparty.argodflib.MainActivity;
import com.thetigerparty.argodflib.Model.Form;
import com.thetigerparty.argodflib.Model.LocalProject;
import com.thetigerparty.argodflib.Model.Project;
import com.thetigerparty.argodflib.Model.Report;
import com.thetigerparty.argodflib.Model.Tracker;
import com.thetigerparty.argodflib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttpttp on 2015/9/15.
 */
public class SubmitReportResultListViewAdapter extends BaseAdapter {
    MainActivity activity;
    List<Report> list_report = new ArrayList<>();
    String submit_via;

    public SubmitReportResultListViewAdapter(MainActivity activity, List<Report> list_report){
        this.activity = activity;
        this.list_report = list_report;
        this.submit_via = "HTTP";
    }

    public SubmitReportResultListViewAdapter(MainActivity activity, List<Report> list_report, String submit_via){
        this.activity = activity;
        this.list_report = list_report;
        this.submit_via = submit_via;
    }

    static class ViewHolder{
        TextView tv_report_title;
        TextView tv_result;
    }

    @Override
    public int getCount() {;
        return list_report.size();
    }

    @Override
    public Object getItem(int position) {
        return list_report.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list_report.get(position).getId().intValue();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.submit_result_list_content, parent, false);

            holder = new ViewHolder();

            holder.tv_report_title = (TextView) convertView.findViewById(R.id.tv_report_title);
            holder.tv_result = (TextView) convertView.findViewById(R.id.tv_result);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        Project project = Project.selectSingle(list_report.get(position).project_id, list_report.get(position).project_type);
        if (project != null) {
            holder.tv_report_title.setText(project.title);
        }
        LocalProject localProject = LocalProject.getProject(list_report.get(position).project_id, list_report.get(position).project_type);
        if (localProject != null) {
            holder.tv_report_title.setText(localProject.title);
        }

        Form report_form = Form.selectSingle(list_report.get(position).form_id);
        if (list_report.get(position).pushed == 1) {
            holder.tv_result.setText(activity.getString(R.string.submit_report_result_success));
        } else {
            if(submit_via.equals("SMS")){
                if (report_form.is_photo_required) {
                    holder.tv_result.setText(activity.getString(R.string.submit_report_result_photo_required));
                } else {
                    holder.tv_result.setText(activity.getString(R.string.submit_report_result_sms_limit));
                }
            } else {
                holder.tv_result.setText(activity.getString(R.string.submit_report_result_fail));
            }
        }

        return convertView;
    }
}
