package com.thetigerparty.argodflib.HelperClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.DIRECTORY_DCIM;

/**
 * Created by fredtsao on 2017/9/2.
 */

public class FileHelper {
    private static String STORAGE_DCIM_PATH = Environment.getExternalStorageDirectory() + "/" + DIRECTORY_DCIM;
    private static String ARGODF_DCIM_PATH = STORAGE_DCIM_PATH + "/ArgoDFAlbum/";

    public static void getPath() {
        Logger.d(STORAGE_DCIM_PATH);
        Logger.d(ARGODF_DCIM_PATH);
    }

    public static File createPhotoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "JPGE_" + timeStamp + "_";
        File directory = new File(ARGODF_DCIM_PATH);

        if (!directory.exists()) {
            FileHelper.mkdir(ARGODF_DCIM_PATH);
        }

        return File.createTempFile(
                fileName,
                ".jpg",
                directory
        );
    }

    public static void addPhotoInAlbum(String path, Activity activity) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(path);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    public static void mkdir(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }  else {
            Logger.d("Directory path is exists");
        }
    }

    public static void rmdir(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            for (File childFile : directory.listFiles()) {
                Logger.d("Delete file: " + childFile.getName());
                childFile.delete();
            }
            directory.delete();
        }
    }

    public static String getMimeTypeByPath(String filePath) {
        String type = "";
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getRealPathFromUri(Context context, Uri uri) {
        String result = "";
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            cursor.moveToFirst();

            result = cursor.getString(column_index);

            cursor.close();
        }
        return result;
    }
}
