package com.thetigerparty.argodflib.Subclass.ReportList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.thetigerparty.argodflib.HelperClass.ImageProcess;
import com.thetigerparty.argodflib.Model.Report;
import com.thetigerparty.argodflib.Object.ReportObject;
import com.thetigerparty.argodflib.R;
import com.thetigerparty.argodflib.ReportListActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttpttp on 2015/8/17.
 */
public class ReportListViewAdapter extends BaseAdapter{
    final int IMAGE_WIDTH_LENGTH = 80;
    final int IMAGE_HEIGHT_LENGTH = 60;

    ReportListActivity activity;
    List<ReportObject> list_obj_report = new ArrayList<>();

    public ReportListViewAdapter(ReportListActivity activity, List<ReportObject> list_obj_report){
        this.activity = activity;
        this.list_obj_report = list_obj_report;
    }

    static class ViewHolder {
        ImageView iv;
        TextView tv_status;
        TextView tv_report_title;
        TextView tv_created_at;
        Button bt_edit;
    }

    class LoadImageTask extends AsyncTask<Object, Void, Bitmap>{
        public final WeakReference<ImageView> imageViewWeakReference;
        public Object data;

        public LoadImageTask(ImageView imageView){
            imageViewWeakReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Object... param) {
            data = param[0];
            Bitmap bm = null;
            //-- decode from file
            if(param[0] instanceof File){
                bm = ImageProcess.decodeSampledBitmapFromFile(
                        (File) param[0],
                        IMAGE_WIDTH_LENGTH,
                        IMAGE_HEIGHT_LENGTH);
            }
            //-- decode from drawable
            else if(param[0] instanceof Integer){
                bm = ImageProcess.decodeSampledBitmapFromResource(
                        activity.getResources(),
                        Integer.parseInt(param[0].toString()),
                        IMAGE_WIDTH_LENGTH,
                        IMAGE_HEIGHT_LENGTH);
            }

            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if(isCancelled()){
                bitmap = null;
            }

            if(bitmap != null){
                final ImageView imageView = imageViewWeakReference.get();
                final LoadImageTask loadImageTask = getLoadImageTask(imageView);
                if (this == loadImageTask) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<LoadImageTask> loadImageTaskTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             LoadImageTask loadImageTask) {
            super(res, bitmap);
            loadImageTaskTaskReference = new WeakReference<>(loadImageTask);
        }

        public LoadImageTask getLoadImageTask() {
            return loadImageTaskTaskReference.get();
        }
    }

    public static boolean cancelPotentialWork(Object data, ImageView imageView) {
        final LoadImageTask loadImageTask = getLoadImageTask(imageView);

        if (loadImageTask != null) {
            final Object bitmapData = loadImageTask.data;
            if (bitmapData != data) {
                loadImageTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static LoadImageTask getLoadImageTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getLoadImageTask();
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return list_obj_report.size();
    }

    @Override
    public Object getItem(int position) {
        return list_obj_report.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list_obj_report.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(activity);
            convertView = inflater.inflate(R.layout.report_list_content, parent, false);

            holder = new ViewHolder();

            holder.iv = (ImageView) convertView.findViewById(R.id.iv_preview);
            holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            holder.tv_report_title = (TextView) convertView.findViewById(R.id.tv_report_title);
            holder.tv_created_at = (TextView) convertView.findViewById(R.id.tv_created_at);
            holder.bt_edit = (Button) convertView.findViewById(R.id.bt_edit);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        String path = Report.getLatestImagePath(list_obj_report.get(position).getId());
        if(path.equals("")){
            loadImage(R.drawable.default_image, holder.iv);
        }else{
            File file = new File(path);
            if(file.exists()){
                loadImage(file, holder.iv);
            }
        }

        holder.tv_report_title.setText(list_obj_report.get(position).getTitle());
        holder.tv_created_at.setText(list_obj_report.get(position).getCreatedAt());
        if(list_obj_report.get(position).getPushed() == 0){
            holder.tv_status.setText("Local");
        }
        else{
            holder.tv_status.setText("Submitted");
        }
        holder.bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.editOnClick(list_obj_report.get(position));
            }
        });

        return convertView;
    }

    public void loadImage(Object param, ImageView imageView){
        if (cancelPotentialWork(param, imageView)) {
            final LoadImageTask loadImageTask = new LoadImageTask(imageView);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(activity.getResources(), null, loadImageTask);
            imageView.setImageDrawable(asyncDrawable);
            loadImageTask.execute(param);
        }
    }
}
