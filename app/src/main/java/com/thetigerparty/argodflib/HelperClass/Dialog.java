package com.thetigerparty.argodflib.HelperClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.thetigerparty.argodflib.BuildConfig;
import com.thetigerparty.argodflib.LoginActivity;
import com.thetigerparty.argodflib.MainActivity;
import com.thetigerparty.argodflib.Model.Report;
import com.thetigerparty.argodflib.Model.Tracker;
import com.thetigerparty.argodflib.Object.AttachmentObject;
import com.thetigerparty.argodflib.R;
import com.thetigerparty.argodflib.ReportActivity;
import com.thetigerparty.argodflib.Subclass.Main.SubmitReportResultListViewAdapter;
import com.thetigerparty.argodflib.Subclass.Main.SubmitTrackerResultListViewAdapter;
import com.thetigerparty.argodflib.TrackerActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by ttpttp on 2015/8/24.
 */
public class Dialog {
    public static void exitAppDialog(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.exit_app))
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public static void needToUpdateDialog(final Context context, final String link){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.need_to_update))
                .setCancelable(false)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(link));
                        if (intent.resolveActivity(context.getPackageManager()) !=  null) {
                            Log.d("Dialog", "needToUpdateDialog: Resolve activity");
                            context.startActivity(intent);
                        } else {
                            Log.d("Dialog", "needToUpdateDialog: can not resolve activity");
                        }
                    }
                })
                .show();
    }

    public static void submitResultDialog(final MainActivity context, List<Report> list_report){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.submit_result_dialog_content, null);
        ListView lv_result = (ListView) view.findViewById(R.id.lv_result);
        SubmitReportResultListViewAdapter srlv = new SubmitReportResultListViewAdapter(context, list_report);
        lv_result.setAdapter(srlv);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.submit_report_result))
               .setCancelable(false)
               .setView(view)
               .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    public static void submitResultTrackerDialog(final MainActivity context, List<Tracker> list_tracker){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.submit_result_dialog_content, null);
        ListView lv_result = (ListView) view.findViewById(R.id.lv_result);

        SubmitTrackerResultListViewAdapter stlv = new SubmitTrackerResultListViewAdapter(context, list_tracker);
        lv_result.setAdapter(stlv);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.submit_report_result))
                .setCancelable(false)
                .setView(view)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    public static void submitResultDialog(final MainActivity context, List<Report> list_report, String submit_via){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.submit_result_dialog_content, null);
        ListView lv_result = (ListView) view.findViewById(R.id.lv_result);
        SubmitReportResultListViewAdapter srlv = new SubmitReportResultListViewAdapter(context, list_report, submit_via);
        lv_result.setAdapter(srlv);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.submit_report_result))
                .setCancelable(false)
                .setView(view)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    public static void logoutDialog(final MainActivity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.logout))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        context.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public static void GPSDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.open_gps))
               .setCancelable(false)
               .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                   }
               })
               .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                   public void onClick(final DialogInterface dialog, int id) {
                       dialog.cancel();
                   }
               }).show();
    }

    public static void deleteAttachmentDialog(final ReportActivity context, final LinearLayout layout, final AttachmentObject obj_attachment){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.delete_this_attachment))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.layout_attachment.removeView(layout);
                        context.obj_report.getListObjAttachment().remove(obj_attachment);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public static void deleteAttachmentDialog(final TrackerActivity context, final AttachmentObject attachmentObject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.delete_this_attachment))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        context.arrayAttachmentObject.remove(attachmentObject);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public static void overMaxAttachmentsSizeDialog(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.over_max_attachments))
                .setCancelable(false)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void saveReportSuccessDialog(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.report_saved))
                .setCancelable(false)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(intent);
                    }
                })
                .show();
    }

    public static void exitReportDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.cancel_this_report))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        context.finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public static void exitTrackerActivityDialog(final TrackerActivity activity ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.cancel_this_tracker))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public static void submitSMSFailedDialog(final MainActivity context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.over_sms_limit))
                .setMessage(context.getString(R.string.try_again_wifi))
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void submitSMSConfirmDialog(final MainActivity context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.sms_confirm))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.useSMS();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context.cancelSMS();
                    }
                })
                .show();
    }

    public static void BlockProjectIntoSubProjectPageDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.block_project_into_local_sub_project))
                .setCancelable(false)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .show();
    }

    public static void checkNotSubmitLocalprojectDialog(final LoginActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.has_not_submit_local_project))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.deleteLocalProjectByOtherUserId();
                        activity.enterMainActivity();
                    }
                })
                .setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

}
