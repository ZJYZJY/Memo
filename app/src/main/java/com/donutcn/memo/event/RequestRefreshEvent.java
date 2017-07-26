package com.donutcn.memo.event;

/**
 * com.donutcn.memo.event
 * Created by 73958 on 2017/7/24.
 */

public class RequestRefreshEvent {

    private int refreshPosition;

    public RequestRefreshEvent(int position){
        this.refreshPosition = position;
    }

    public int getRefreshPosition() {
        return refreshPosition;
    }
}
