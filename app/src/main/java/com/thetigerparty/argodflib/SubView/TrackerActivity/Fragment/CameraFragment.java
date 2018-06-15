package com.thetigerparty.argodflib.SubView.TrackerActivity.Fragment;

import android.Manifest;
import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.thetigerparty.argodflib.HelperClass.CameraUtil;
import com.thetigerparty.argodflib.HelperClass.FileHelper;
import com.thetigerparty.argodflib.Object.AttachmentObject;
import com.thetigerparty.argodflib.R;
import com.thetigerparty.argodflib.TrackerActivity;
import com.thetigerparty.argodflib.ViewModel.TrackerActivity.CameraFragmentViewModel;
import com.thetigerparty.argodflib.databinding.TrackerActivityCameraFragmentBinding;

import java.io.IOException;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by fredtsao on 2017/8/24.
 */

public class CameraFragment extends Fragment implements SurfaceHolder.Callback{
    private final static String TAG = "CameraFragment";

    private View view;
    private TrackerActivity activity;

    private CameraFragmentViewModel viewModel = new CameraFragmentViewModel();

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Bitmap takenPicture;

    private Camera camera;
    private ImageButton btTakePicture;
    private Button btRetake;
    private Button btUse;

    private Camera.ShutterCallback shutterCallback;
    private Camera.PictureCallback pictureCallback;

    public CameraFragment(){}

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.shutterCallback = new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                Log.d(TAG, "onShutter");
            }
        };

        this.pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                Log.d(TAG, "onPictureTaken");

                stopPreview();

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    //-- rotate 90 degree to fix picture problem
                    Matrix matrix = new Matrix();
                    matrix.setRotate(90);
                    takenPicture = Bitmap.createBitmap(
                            bitmap,
                            0,
                            0,
                            bitmap.getWidth(),
                            bitmap.getHeight(),
                            matrix,
                            true
                    );

                    bitmap.recycle();
                }
            }
        };


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        TrackerActivityCameraFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.tracker_activity_camera_fragment, container, false);
        binding.setCameraViewModel(viewModel);
        view = binding.getRoot();

        if (!EasyPermissions.hasPermissions(getActivity(), Manifest.permission.CAMERA)) {
            Log.w(TAG, "onCreateView: No permission to access camera");

            return view;
        }

        surfaceView = (SurfaceView) view.findViewById(R.id.surface_camera);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        btTakePicture = (ImageButton) view.findViewById(R.id.bt_take_picture);
        btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onTakePictureClick");
                btTakePicture.setEnabled(false);
                camera.takePicture(shutterCallback, null, pictureCallback);
            }
        });

        btRetake = (Button) view.findViewById(R.id.bt_retake);
        btRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onRetakeClick");

                if (takenPicture != null) {
                    takenPicture.recycle();
                }
                btTakePicture.setEnabled(true);
                startPreview();
            }
        });

        btUse = (Button) view.findViewById(R.id.bt_usePhoto);
        btUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onUseClick");
                stopPreview();

                try {
                    if(takenPicture != null) {
                        String url = MediaStore.Images.Media.insertImage(
                                getActivity().getContentResolver(),
                                takenPicture,
                                String.valueOf(System.currentTimeMillis()),
                                null
                        );

                        if (url != null) {
                            Uri uri = Uri.parse(url);
                            String filePath = FileHelper.getRealPathFromUri(activity, uri);
                            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
                            String fileType = FileHelper.getMimeTypeByPath(filePath);

                            activity.currentAttachmentObject = new AttachmentObject();
                            activity.currentAttachmentObject.setName(fileName);
                            activity.currentAttachmentObject.setPath(filePath);
                            activity.currentAttachmentObject.setType(fileType);

                            Log.d(TAG, String.format("New file inserted into gallery: %s", filePath));

                            activity.changeFragment(activity.locationFragment);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = (TrackerActivity) getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");

        stopPreview();

        if (takenPicture != null && !takenPicture.isRecycled()) {
            Log.d(TAG, "TakenPicture do recycle.");
            takenPicture.recycle();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");

        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");

        stopPreview();
    }

    private void startPreview() {
        if (camera == null) {
            camera = Camera.open();

            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        CameraUtil.setCameraDisplayOrientation(getActivity(), Camera.CameraInfo.CAMERA_FACING_BACK, camera);
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(parameters);
        camera.startPreview();

        viewModel.setUsingCamera(true);
    }

    private void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        viewModel.setUsingCamera(false);
    }
}
