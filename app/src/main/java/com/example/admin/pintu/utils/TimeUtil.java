package com.example.admin.pintu.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2017/8/27.
 */

public class TimeUtil {

    private TimeUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**获取当前时间的格式化(yyyy-MM-dd)的String*/
    public static String getCurrentDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /**获取当前时间的格式化(yyyy-MM-dd HH:mm:ss)的String*/
    public static String getCurrTime(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 把timestamp各式化成yyyy/MM/dd kk:mm:ss
     *
     * @param dateTaken
     * @return
     */
    public static String timeString(long dateTaken) {
        return android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", dateTaken).toString();
    }

    /**
     * 把timestamp各式化成yyyy/MM/dd
     *
     * @param dateTaken
     * @return
     */
    public static String dateString(long dateTaken) {
        return android.text.format.DateFormat.format("yyyy-MM-dd", dateTaken).toString();
    }

    /**
     * 把timestamp各式化成MM-dd
     *
     * @param dateTaken
     * @return
     */
    public static String dateString2(long dateTaken) {
        return android.text.format.DateFormat.format("MM-dd HH:mm", dateTaken).toString();
    }




}
