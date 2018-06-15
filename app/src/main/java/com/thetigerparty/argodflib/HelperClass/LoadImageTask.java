package com.thetigerparty.argodflib.HelperClass;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by fredtsao on 10/28/16.
 */

public class LoadImageTask extends AsyncTask<File, Void, Bitmap> {
    private int IMAGE_VIEW_WIDTH;
    private int IMAGE_VIEW_HEIGHT;

    private final WeakReference<ImageView> imageViewWeakReference;

    public LoadImageTask(ImageView imageView){
        imageViewWeakReference = new WeakReference<>(imageView);
    }

    public LoadImageTask(ImageView imageView, int width, int height){
        imageViewWeakReference = new WeakReference<>(imageView);
        IMAGE_VIEW_WIDTH = width;
        IMAGE_VIEW_HEIGHT = height;
    }

    @Override
    protected Bitmap doInBackground(File... files) {

        return ImageProcess.decodeSampledBitmapFromFile(
                files[0],
                IMAGE_VIEW_WIDTH,
                IMAGE_VIEW_HEIGHT);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if(bitmap != null){
            final ImageView imageView = imageViewWeakReference.get();
            if(imageView != null){
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
