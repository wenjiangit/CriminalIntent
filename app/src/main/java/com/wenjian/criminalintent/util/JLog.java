package com.wenjian.criminalintent.util;

import com.orhanobut.logger.Logger;
import com.wenjian.criminalintent.BuildConfig;

/**
 * Created by douliu on 2016/12/8.
 */
public class JLog {

    private static boolean DEBUG = BuildConfig.DEBUG;

    public static void e(String tag,String message,Throwable e){
        if (DEBUG){
            Logger.t(tag).e(e,message);
        }
    }

    public static void d(String tag,String message){
        if (DEBUG){
            Logger.t(tag).d(message);
        }
    }

    public static void i(String tag,String message){
        if (DEBUG){
            Logger.t(tag).i(message);
        }
    }

    public static void json(String tag, String json){
        if (DEBUG){
            Logger.t(tag).json(json);
        }
    }

}
