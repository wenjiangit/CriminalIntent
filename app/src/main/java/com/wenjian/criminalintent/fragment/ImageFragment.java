package com.wenjian.criminalintent.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wenjian.criminalintent.R;
import com.wenjian.criminalintent.util.PictureUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends DialogFragment {

    private static final String EXTRA_IMAGE_PATH = "EXTRA_IMAGE_PATH";
    private String mImagePath;
    private ImageView mImageView;


    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(String imagePath){
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_IMAGE_PATH,imagePath);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE,0);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImagePath = getArguments().getString(EXTRA_IMAGE_PATH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        BitmapDrawable drawable = PictureUtil.getScaledDrawable(getActivity(), mImagePath);
        //可以通过matrix旋转照片方向
       /* Bitmap srcBitmap = drawable.getBitmap();
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        Bitmap bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, false);
        mImageView.setImageBitmap(bitmap);*/
        mImageView.setImageDrawable(drawable);
        return mImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtil.clearImageView(mImageView);
    }
}
