package com.donutcn.widgetlib;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

import com.donutcn.widgetlib.listener.Pullable;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

/**
 * com.donutcn.widgetlib
 * Created by 73958 on 2017/7/19.
 */

public class PullableRecyclerView extends SwipeMenuRecyclerView implements Pullable {

    public PullableRecyclerView(Context context) {
        super(context);
    }

    public PullableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        if (layoutManager.getItemCount() == 0) {
            // can drop-down when there is no item.
            return true;
        } else if (layoutManager.findFirstVisibleItemPosition() == 0
                && getChildAt(0).getTop() >= 0) {
            // slide to the top.
            return true;
        } else
            return false;
    }

    @Override
    public boolean canPullUp() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        if (layoutManager.getItemCount() == 0) {
            // can not pull-up when there is no item.
            return false;
        } else if (layoutManager.findLastVisibleItemPosition() == (layoutManager.getItemCount() - 1)) {
            // slide to the bottom.
            if (layoutManager.getChildAt(layoutManager.getChildCount() - 1) != null
                    && layoutManager.getChildAt(layoutManager.getChildCount() - 1).getBottom() <= getMeasuredHeight())
                return true;
        }
        return false;
    }
}
