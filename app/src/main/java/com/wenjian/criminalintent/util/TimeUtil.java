package com.wenjian.criminalintent.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by douliu on 2016/12/6.
 */
public class TimeUtil {

    public static final String DEFAULT_TEMPLATE = "yyyy-MM-dd HH:mm:ss";

    /**
     * Date转化为年月日时分秒的String
     * @param date
     * @return
     */
    public static String date2String(Date date){
        return new SimpleDateFormat(DEFAULT_TEMPLATE, Locale.getDefault()).format(date);
    }
}
