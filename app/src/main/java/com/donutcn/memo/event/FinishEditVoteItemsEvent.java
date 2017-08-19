package com.donutcn.memo.event;

import java.util.List;

/**
 * com.donutcn.memo.event
 * Created by 73958 on 2017/8/5.
 */

public class FinishEditVoteItemsEvent {

    private List<String> voteItem;
    private boolean isSingle;

    public FinishEditVoteItemsEvent(List<String> voteItem, boolean isSingle){
        this.voteItem = voteItem;
        this.isSingle = isSingle;
    }

    public List<String> getVoteItem() {
        return voteItem;
    }

    public boolean isSingle() {
        return isSingle;
    }
}
