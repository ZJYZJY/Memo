package com.donutcn.memo.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/5.
 * Commonly used helper classes for unit conversions
 */

public class DensityUtils {

    /**
     * dp to px
     * @param context
     * @param dpVal
     * @return px
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }


    /**
     * px to dp
     * @param context
     * @param pxVal
     * @return dp
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }
}
