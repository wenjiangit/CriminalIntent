package com.wenjian.criminalintent.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;

/**
 * Created by douliu on 2016/12/7.
 */
public class PictureUtil {

    public static BitmapDrawable getScaledDrawable(Activity a,String path){
        Display display = a.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        float outWidth = options.outWidth;
        float outHeight = options.outHeight;

        int inSampleSize = 1;
        if (outHeight > destHeight || outWidth > destWidth){
            if (outHeight > outWidth){
                inSampleSize = Math.round(outWidth / destWidth);
            }else {
                inSampleSize = Math.round(outHeight / destHeight);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return new BitmapDrawable(a.getResources(),bitmap);
    }
}
