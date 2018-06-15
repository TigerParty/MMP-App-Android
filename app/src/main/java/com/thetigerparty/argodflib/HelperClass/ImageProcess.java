package com.thetigerparty.argodflib.HelperClass;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.thetigerparty.argodflib.Model.Attachment;
import com.thetigerparty.argodflib.Model.Report;
import com.thetigerparty.argodflib.Model.Tracker;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ttpttp on 2015/8/4.
 */
public class ImageProcess{

    public static int IMAGE_LONGER_LENGTH = 600;

    public static String TEMP_DIRECTORY = Environment.getExternalStorageDirectory().toString()+"/ArgoDFTempDir/";

    public static String getPath(Uri uri, Activity activity)
    {
        String result = "";
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null)
        {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            cursor.moveToFirst();

            result = cursor.getString(column_index);

            cursor.close();
        }

        return result;
    }

    public static File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/DCIM/");

        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }

    public static void galleryAddPic(String path, Activity activity)
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(path);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds = true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromFile(File file, int reqWidth, int reqHeight) {
        String mime_type = getMimeTypeByPath(file.getPath());
        String file_type = mime_type.substring(0, mime_type.indexOf("/"));

        if (file_type.equals("video")) {
            return ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        } else {
            // First decode with inJustDecodeBounds = true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getPath(), options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeFile(file.getPath(), options);
        }
    }

    public static boolean checkImageSize(File file) {
        boolean check = false;
        String fileExt = getExt(file.getAbsolutePath().toLowerCase());
        Log.d("Attachment extension", fileExt);
        if(fileExt.equals("jpg") || fileExt.equals("png") || fileExt.equals("jpeg"))
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getPath(), options);
            int height = options.outHeight;
            int width = options.outWidth;

            System.out.println(height);
            System.out.println(width);

            if(height >= width) {
                float scale = (float) height / IMAGE_LONGER_LENGTH;
                if(scale > 1.1) {
                    check = true;
                }
            }
            else {
                float scale = (float) width / IMAGE_LONGER_LENGTH;
                if(scale > 1.1){
                    check = true;
                }
            }
        }

        return check;
    }

    public static File compressImage(File file, Report report) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bm_origin = BitmapFactory.decodeFile(file.getPath(), options);
        int height = options.outHeight;
        int width = options.outWidth;

        float bitmapRatio = (float)width / (float) height;
        Log.d("Bitmap ratio", String.valueOf(bitmapRatio));

        if(bitmapRatio > 1)
        {
            width = IMAGE_LONGER_LENGTH;
            height = (int) (width / bitmapRatio);
        }
        else
        {
            height = IMAGE_LONGER_LENGTH;
            width = (int) (height * bitmapRatio);
        }
        Log.d("Image compressed height", String.valueOf(height));
        Log.d("Image compressed width", String.valueOf(width));

        Bitmap bm_new = Bitmap.createScaledBitmap(bm_origin, width, height, false);
        drawTextOnBitmap(bm_new, report);

        File compressed_file = null;
        try{
            makeDir();

            compressed_file = new File(TEMP_DIRECTORY, file.getName());
            FileOutputStream fos = new FileOutputStream(compressed_file);
            bm_new.compress(Bitmap.CompressFormat.JPEG, 50, fos);

            fos.flush();
            fos.close();
            bm_origin.recycle();
            bm_new.recycle();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return compressed_file;
    }

    public static File compressImage(File file, Attachment attachment) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bm_origin = BitmapFactory.decodeFile(file.getPath(), options);
        int height = options.outHeight;
        int width = options.outWidth;

        float bitmapRatio = (float)width / (float) height;
        Log.d("Bitmap ratio", String.valueOf(bitmapRatio));

        if(bitmapRatio > 1)
        {
            width = IMAGE_LONGER_LENGTH;
            height = (int) (width / bitmapRatio);
        }
        else
        {
            height = IMAGE_LONGER_LENGTH;
            width = (int) (height * bitmapRatio);
        }
        Log.d("Image compressed height", String.valueOf(height));
        Log.d("Image compressed width", String.valueOf(width));

        Bitmap bm_new = Bitmap.createScaledBitmap(bm_origin, width, height, false);
        drawTextOnBitmap(bm_new, attachment);

        File compressed_file = null;
        try{
            makeDir();

            compressed_file = new File(TEMP_DIRECTORY, file.getName());
            FileOutputStream fos = new FileOutputStream(compressed_file);
            bm_new.compress(Bitmap.CompressFormat.JPEG, 50, fos);

            fos.flush();
            fos.close();
            bm_origin.recycle();
            bm_new.recycle();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return compressed_file;
    }

    public static void drawTextOnBitmap(Bitmap bitmap, Report report){
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize(20);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawText(report.created_at, 0, bitmap.getHeight() - 50, paint);
        canvas.drawText(report.lat + ", " + report.lng, 0, bitmap.getHeight() - 30, paint);
    }

    public static void drawTextOnBitmap(Bitmap bitmap, Attachment attachment){
        Canvas canvas = new Canvas(bitmap);
        try{
            JSONObject description = new JSONObject(attachment.description.toString());
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setTextSize(20);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

            canvas.drawBitmap(bitmap, 0, 0, paint);
            canvas.drawText(attachment.created_at, 0, bitmap.getHeight() - 50, paint);
            canvas.drawText(description.get("lat") + ", " + description.get("lng"), 0, bitmap.getHeight() - 30, paint);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ImageProcess", e.toString());
        }
    }

    public static String getExt(String filePath)
    {
        return filePath.substring((filePath.lastIndexOf(".") + 1), filePath.length());
    }

    public static void makeDir()
    {
        File fileDir = new File(TEMP_DIRECTORY);
        if(!fileDir.exists())
        {
            fileDir.mkdirs();
        }
    }

    public static void removeDir()
    {
        File fileDir = new File(TEMP_DIRECTORY);
        if(fileDir.exists() && fileDir.isDirectory())
        {
            for (File childFile : fileDir.listFiles())
            {
                Log.d("Delete file", childFile.getName());
                childFile.delete();
            }
        }
    }

    public static String getMimeTypeByPath (String file_path) {
        String type = "";
        String extension = MimeTypeMap.getFileExtensionFromUrl(file_path);
        if(extension != null){
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
