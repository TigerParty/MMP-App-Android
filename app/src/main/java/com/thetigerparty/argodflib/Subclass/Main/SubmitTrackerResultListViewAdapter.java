package com.thetigerparty.argodflib.Subclass.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thetigerparty.argodflib.MainActivity;
import com.thetigerparty.argodflib.Model.Tracker;
import com.thetigerparty.argodflib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fredtsao on 2017/9/5.
 */

public class SubmitTrackerResultListViewAdapter extends BaseAdapter {
    MainActivity activity;
    List<Tracker> list_tracker = new ArrayList<>();
    String submit_via = "http";

    public SubmitTrackerResultListViewAdapter(MainActivity activity, List<Tracker> list_tracker) {
        this.activity = activity;
        this.list_tracker = list_tracker;
    }

    static class ViewHolder{
        TextView tv_report_title;
        TextView tv_result;
    }

    @Override
    public int getCount() {
        return list_tracker.size();
    }

    @Override
    public Object getItem(int position) {
        return list_tracker.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list_tracker.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SubmitReportResultListViewAdapter.ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.submit_result_list_content, parent, false);

            holder = new SubmitReportResultListViewAdapter.ViewHolder();

            holder.tv_report_title = (TextView) convertView.findViewById(R.id.tv_report_title);
            holder.tv_result = (TextView) convertView.findViewById(R.id.tv_result);

            convertView.setTag(holder);
        }
        else{
            holder = (SubmitReportResultListViewAdapter.ViewHolder) convertView.getTag();
        }

        Tracker tracker = list_tracker.get(position);
        if (tracker != null) {
            holder.tv_report_title.setText(tracker.title);
        }

        if (tracker.pushed == 1) {
            holder.tv_result.setText(activity.getString(R.string.submit_report_result_success));
        } else {
            holder.tv_result.setText(activity.getString(R.string.submit_report_result_fail));
        }

        return convertView;
    }
}
