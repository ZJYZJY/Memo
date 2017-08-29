package com.donutcn.memo.event;

/**
 * com.donutcn.memo.event
 * Created by 73958 on 2017/8/28.
 */

public class ItemActionClickEvent {

    private final int ITEM_DELETE = 0;
    private final int ITEM_DOWNLOAD = 1;

    public int position;
    public int type;

    public ItemActionClickEvent(int position, int type){
        this.position = position;
        this.type = type;
    }
}
