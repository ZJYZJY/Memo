package com.donutcn.memo.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * com.donutcn.memo.fragment
 * Created by 73958 on 2017/7/20.
 */

public class BaseScrollFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;

    private int lastOffset, lastPosition;

    public void setRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mRecyclerView != null){
            layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(recyclerView.getLayoutManager() != null) {
                        getPositionAndOffset();
                    }
                }
            });
        }
    }

    /**
     * record RecyclerView current position and offset.
     */
    private void getPositionAndOffset() {
        View topView = layoutManager.getChildAt(0);
        if(topView != null) {
            // get the top offset of this view.
            lastOffset = topView.getTop();
            // get the item position.
            lastPosition = layoutManager.getPosition(topView);
        }
    }

    /**
     * scroll RecyclerView to specified position.
     */
    private void scrollToPosition() {
        if(mRecyclerView.getLayoutManager() != null && lastPosition >= 0) {
            layoutManager.scrollToPositionWithOffset(lastPosition, lastOffset);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mRecyclerView != null){
            scrollToPosition();
        }
    }
}
