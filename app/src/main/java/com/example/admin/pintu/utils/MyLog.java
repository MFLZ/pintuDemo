package com.example.admin.pintu.utils;

/**
 * Created by admin on 2017/7/28.
 */

import android.util.Log;

/**
 * 日志输出
 *
 * @author MFLZ
 */
public class MyLog {
    /**
     * 日志输出开关
     */
    public static boolean idDebug = true;

    /**
     * 获取类名、方法名、行号
     *
     * @return
     */
    private static String createMessage() {
        StackTraceElement ste = new Throwable().getStackTrace()[2];
        String className = ste.getClassName();
        String name = className.substring(className.lastIndexOf(".") + 1);
        int line = ste.getLineNumber();
        return name + "." + ste.getMethodName() + "(L:" + line + ")";
    }

    public static void v(String str) {
        if (idDebug) {
            Log.v(createMessage(), str);
        }

    }

    public static void d(String str) {
        if (idDebug) {
            Log.d(createMessage(), str);
        }

    }

    public static void i(String str) {
        if (idDebug) {
            Log.i(createMessage(), str);
        }

    }

    public static void w(String str) {
        if (idDebug) {
            Log.w(createMessage(), str);
        }

    }

    public static void e(String str) {
        if (idDebug) {
            Log.e(createMessage(), str);
        }

    }


}
