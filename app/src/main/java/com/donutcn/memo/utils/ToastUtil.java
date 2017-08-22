package com.donutcn.memo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/8/2.
 */

public class ToastUtil {

    private static boolean isShow = true;
    private static  Toast mToast = null;

    private ToastUtil() {
        throw new UnsupportedOperationException("不能被实例化");
    }

    public static void controlShow(boolean isShowToast){
        isShow = isShowToast;
    }

    /**
     * cancel toast.
     */
    public static void cancel() {
        if(isShow && mToast != null){
            mToast.cancel();
        }
    }
    public static void show(Context context, CharSequence message) {
        if (isShow){
            if(mToast != null)
                cancel();
//            if(message == null){
//                return;
//            }
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    public static void show(Context context, int resId) {
        if (isShow){
            if(mToast != null)
                cancel();
            mToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    public static void show(Context context, CharSequence message, int duration) {
        if (isShow){
            if (mToast == null) {
                mToast = Toast.makeText(context, message, duration);
            } else {
                mToast.setText(message);
            }
            mToast.show();
        }
    }
}