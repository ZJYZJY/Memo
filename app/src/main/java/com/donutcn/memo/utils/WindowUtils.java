package com.donutcn.memo.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.donutcn.memo.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/6.
 */

public class WindowUtils {

    /**
     * set custom toolbar title.
     *
     * @param context context
     * @param title   string resource id
     */
    public static void setToolBarTitle(Context context, @StringRes int title) {
        Activity activity = (Activity) context;
        ((TextView) activity.findViewById(R.id.toolbar_with_title)).setText(activity.getResources().getString(title));
    }

    /**
     * set custom toolbar title.
     *
     * @param context context
     * @param title   string
     */
    public static void setToolBarTitle(Context context, String title) {
        Activity activity = (Activity) context;
        ((TextView) activity.findViewById(R.id.toolbar_with_title)).setText(title);
    }

    /**
     * set toolbar button text.
     *
     * @param context context
     * @param text    string resource id
     */
    public static void setToolBarButton(Context context, @StringRes int text) {
        Activity activity = (Activity) context;
        ((TextView) activity.findViewById(R.id.toolbar_with_btn)).setText(activity.getResources().getString(text));
    }

//    private static void transparencyBar(Activity activity){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = activity.getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//
//        } else
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window =activity.getWindow();
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
//    }

    /**
     * set statusBar color for higher api devices
     *
     * @param context context
     * @param color   color resource id
     */
    public static void setStatusBarColor(Context context, @ColorRes int color, boolean darkMode) {
        Activity activity = (Activity) context;
        Window window = activity.getWindow();
//        transparencyBar(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API >= 23, use android original method to change statusBar text color.
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (darkMode){
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            window.setStatusBarColor(activity.getResources().getColor(color));
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(MIUISetStatusBarLightMode(window, darkMode)
                    || FlymeSetStatusBarLightMode(window, darkMode)){
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.setStatusBarColor(activity.getResources().getColor(color));
            }
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(MIUISetStatusBarLightMode(window, darkMode)
                    || FlymeSetStatusBarLightMode(window, darkMode)){
                WindowManager.LayoutParams winParams = window.getAttributes();
                final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                if (true) {
                    winParams.flags |= bits;
                } else {
                    winParams.flags &= ~bits;
                }
                window.setAttributes(winParams);
            }
        }
    }

    /**
     * set text color in statusBar, need MIUIV6+
     *
     * @param window window object
     * @param dark   true for dark color
     * @return boolean true for set up successfully
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(window, dark ? darkModeFlag : 0, darkModeFlag);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * set text color in statusBar, need MIUIV6+
     *
     * @param window window object
     * @param dark   true for dark color
     * @return boolean true for set up successfully
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
