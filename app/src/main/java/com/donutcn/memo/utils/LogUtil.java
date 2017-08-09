package com.donutcn.memo.utils;

import android.util.Log;

/**
 * com.zjy.trafficassist.utils
 * Created by 73958 on 2017/3/22.
 */

public class LogUtil {

    private static boolean LOGV = true;

    private static boolean LOGD = true;

    private static boolean LOGI = true;

    private static boolean LOGW = true;

    private static boolean LOGE = true;

    private static String TAG_FILTER = "MEMO_";

    public static void v(String msg) {
        if (LOGV) { Log.v(TAG_FILTER + getTag(), msg); }
    }
    public static void d(String msg) {
        if (LOGD) { Log.d(TAG_FILTER + getTag(), msg); }
    }
    public static void i(String msg) {
        if (LOGI) { Log.i(TAG_FILTER + getTag(), msg); }
    }
    public static void w(String msg) {
        if (LOGW) { Log.w(TAG_FILTER + getTag(), msg); }
    }
    public static void e(String msg) {
        if (LOGE) { Log.e(TAG_FILTER + getTag(), msg); }
    }

    public static void v(String tag, String msg) {
        if (LOGV) { Log.v(TAG_FILTER + getTag() + tag, msg); }
    }
    public static void d(String tag, String msg) {
        if (LOGD) { Log.d(TAG_FILTER + getTag() + tag, msg); }
    }
    public static void i(String tag, String msg) {
        if (LOGI) { Log.i(TAG_FILTER + getTag() + tag, msg); }
    }
    public static void w(String tag, String msg) {
        if (LOGW) { Log.w(TAG_FILTER + getTag() + tag, msg); }
    }
    public static void e(String tag, String msg) {
        if (LOGE) { Log.e(TAG_FILTER + getTag() + tag, msg); }
    }

    private static String getTag() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String callingClass = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass
                        .lastIndexOf('.') + 1);
                break;
            }
        }
        return callingClass + " ";
    }
}
