package com.wenjian.criminalintent.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.wenjian.criminalintent.R;
import com.wenjian.criminalintent.util.JLog;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeCameraFragment extends Fragment {

    private static final String TAG = CrimeCameraFragment.class.getSimpleName();

    public static final String EXTRA_PHOTO_FILENAME = "EXTRA_PHOTO_FILENAME";

    @BindView(R.id.sf_crime_camera)
    SurfaceView mSfCrimeCamera;
    @BindView(R.id.bt_take_picture)
    Button mBtTakePicture;
    @BindView(R.id.progress_container)
    FrameLayout mProgressContainer;

    private Camera mCamera;

    /**相机捕获图像时的回调*/
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            String fileName = UUID.randomUUID()+".jpg";
            FileOutputStream out = null;
            boolean success = true;
            try {
                out = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
                out.write(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                success = false;
                JLog.e(TAG, "Error writing to file:"+fileName,e);
            }finally {
                if (out != null){
                    try {
                        out.close();
                    } catch (IOException e) {
                        JLog.e(TAG,"Error closing file:"+fileName,e);
                        success = false;
                    }
                }
            }
            
            if (success){
                JLog.i(TAG, "JPG saved at:"+fileName);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_PHOTO_FILENAME,fileName);
                getActivity().setResult(Activity.RESULT_OK,intent);
            }else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
        }
    };

    private static final int PERMISSION_CAMERA_REQUEST = 0;
    private Unbinder mUnbinder;

    public CrimeCameraFragment() {
        // Required empty public constructor
    }

    public static CrimeCameraFragment newInstance() {
        return new CrimeCameraFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime_camera, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    private void initViews() {
        mProgressContainer.setVisibility(View.INVISIBLE);
        SurfaceHolder holder = mSfCrimeCamera.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(surfaceHolder);
                    }
                } catch (IOException e) {
                    JLog.e(TAG, "Error setting up preview display:",e);
                }
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                if (mCamera == null) return;
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), i1, i2);
                parameters.setPreviewSize(s.width,s.height);
                parameters.setPictureSize(s.width,s.height);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (mCamera != null) mCamera.stopPreview();
            }
        });
    }

    @OnClick(R.id.bt_take_picture)
    public void onClick() {
        mProgressContainer.setVisibility(View.VISIBLE);
        if (mCamera!= null){
            mCamera.takePicture(mShutterCallback,null,mPictureCallback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
//                Toast.makeText(getActivity(),"相机非常重要对于这个应用",Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.friendly_tip)
                        .setMessage(R.string.less_camera_permission)
                        .setCancelable(true)
                        .setIcon(R.mipmap.icon)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().finish();
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_CAMERA_REQUEST);
            }
        } else {
            mCamera = Camera.open(0);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CAMERA_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCamera = Camera.open(0);
                } else {
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> list, int width, int height) {
        Camera.Size bestSize = list.get(0);
        int lagerstArea = bestSize.width * bestSize.height;
        for (Camera.Size s : list) {
            int area = s.width * s.height;
            if (area > lagerstArea) {
                bestSize = s;
                lagerstArea = area;
            }
        }
        return bestSize;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null){
            mUnbinder.unbind();
        }
    }
}
