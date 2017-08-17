package com.donutcn.memo.event;

/**
 * com.donutcn.memo.event
 * Created by 73958 on 2017/7/23.
 */

public class ChangeRedDotEvent {

    private int dotPosition;
    private int count;

    public ChangeRedDotEvent(int dotPosition, int count) {
        this.dotPosition = dotPosition;
        this.count = count;
    }

    public int getDotPosition() {
        return dotPosition;
    }

    public int getCount() {
        return count;
    }
}
