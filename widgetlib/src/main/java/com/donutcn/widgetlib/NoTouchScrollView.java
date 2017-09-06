package com.donutcn.widgetlib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * com.donutcn.widgetlib
 * Created by 73958 on 2017/9/6.
 */

public class NoTouchScrollView extends ScrollView {

    public NoTouchScrollView(Context context) {
        super(context);
    }

    public NoTouchScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoTouchScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
