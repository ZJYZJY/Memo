package com.donutcn.memo.event;

import java.util.List;

/**
 * com.donutcn.memo.event
 * Created by 73958 on 2017/8/5.
 */

public class FinishEditVoteItemsEvent {

    private List<String> voteItem;

    public FinishEditVoteItemsEvent(List<String> voteItem){
        this.voteItem = voteItem;
    }

    public List<String> getVoteItem() {
        return voteItem;
    }
}
