package com.donutcn.widgetlib;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * com.donutcn.widgetlib
 * Created by 73958 on 2017/7/4.
 *
 * Forbid ViewPager to slide.
 */

public class NoScrollViewPager extends ViewPager {

    private boolean isScroll = false;

    public NoScrollViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public NoScrollViewPager(Context context) {
        super(context);
    }

    /**
     * If the default return value is modified, the child is not able to receive the event
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
    /**
     * Intercept?
     * Intercept : The event will pass on your own onTouchEvent().
     * Not intercept : The event will pass on to the child.
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isScroll && super.onInterceptTouchEvent(ev);
    }

    /**
     * consume event
     * consume :The event is over
     * not consume : Passing to the parent view
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return !isScroll || super.onTouchEvent(ev);
    }

    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }
}