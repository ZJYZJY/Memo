package com.donutcn.memo.event;

/**
 * com.donutcn.memo.event
 * Created by 73958 on 2017/8/10.
 */

public class ChangeContentEvent {

    private final int CONTENT_ID = 0;
    private final int USER_ID = 1;
    private final int REFRESH_FOLLOW_LIST = 1;

    private int type;
    private String id;

    public ChangeContentEvent(String id, int type){
        this.id = id;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}
