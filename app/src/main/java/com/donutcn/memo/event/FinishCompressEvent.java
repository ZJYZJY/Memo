package com.donutcn.memo.event;

/**
 * com.donutcn.memo.event
 * Created by 73958 on 2017/8/9.
 */

public class FinishCompressEvent {

    private boolean success;

    public FinishCompressEvent(boolean success){
        this.success = success;
    }

    public boolean isSuccess(){
        return success;
    }
}
