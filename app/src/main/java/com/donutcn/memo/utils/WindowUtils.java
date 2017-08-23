package com.donutcn.memo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.annotation.StringRes;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

    public static void toggleKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void toggleKeyboard(Activity activity, boolean show) {
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(show){
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * set statusBar color for higher api devices
     *
     * @param context context
     * @param color   color resource id
     */
    public static void setStatusBarColor(Context context, @ColorRes int color, boolean darkMode) {
        Activity activity = (Activity) context;
        Window window = activity.getWindow();
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

//    /** 增加View的paddingTop,增加的值为状态栏高度 */
//    public static void setPadding(Context context, View view) {
//        if (Build.VERSION.SDK_INT >= 16) {
//            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
//                    view.getPaddingRight(), view.getPaddingBottom());
//        }
//    }
//    /** 增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度)*/
//    public static void setPaddingSmart(Context context, View view) {
//        if (Build.VERSION.SDK_INT >= 16) {
//            ViewGroup.LayoutParams lp = view.getLayoutParams();
//            if (lp != null && lp.height > 0) {
//                lp.height += getStatusBarHeight(context);//增高
//            }
//            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
//                    view.getPaddingRight(), view.getPaddingBottom());
//        }
//    }
//
//    /** 增加View的高度以及paddingTop,增加的值为状态栏高度.一般是在沉浸式全屏给ToolBar用的 */
//    public static void setHeightAndPadding(Context context, View view) {
//        if (Build.VERSION.SDK_INT >= 16) {
//            ViewGroup.LayoutParams lp = view.getLayoutParams();
//            lp.height += getStatusBarHeight(context);//增高
//            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
//                    view.getPaddingRight(), view.getPaddingBottom());
//        }
//    }
//    /** 增加View上边距（MarginTop）一般是给高度为 WARP_CONTENT 的小控件用的*/
//    public static void setMargin(Context context, View view) {
//        if (Build.VERSION.SDK_INT >= 16) {
//            ViewGroup.LayoutParams lp = view.getLayoutParams();
//            if (lp instanceof ViewGroup.MarginLayoutParams) {
//                ((ViewGroup.MarginLayoutParams) lp).topMargin += getStatusBarHeight(context);//增高
//            }
//            view.setLayoutParams(lp);
//        }
//    }
//    /**
//     * 创建假的透明栏
//     */
//    public static void setTranslucentView(ViewGroup container, int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
//        if (Build.VERSION.SDK_INT >= 19) {
//            int mixtureColor = mixtureColor(color, alpha);
//            View translucentView = container.findViewById(android.R.id.custom);
//            if (translucentView == null && mixtureColor != 0) {
//                translucentView = new View(container.getContext());
//                translucentView.setId(android.R.id.custom);
//                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(container.getContext()));
//                container.addView(translucentView, lp);
//            }
//            if (translucentView != null) {
//                translucentView.setBackgroundColor(mixtureColor);
//            }
//        }
//    }
//
//    public static int mixtureColor(int color, @FloatRange(from = 0.0, to = 1.0) float alpha) {
//        int a = (color & 0xff000000) == 0 ? 0xff : color >>> 24;
//        return (color & 0x00ffffff) | (((int) (a * alpha)) << 24);
//    }
//
//    /** 获取状态栏高度 */
//    public static int getStatusBarHeight(Context context) {
//        int result = 24;
//        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resId > 0) {
//            result = context.getResources().getDimensionPixelSize(resId);
//        } else {
//            result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                    result, Resources.getSystem().getDisplayMetrics());
//        }
//        return result;
//    }
}
