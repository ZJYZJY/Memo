package com.donutcn.memo.utils;

import android.support.v7.util.DiffUtil;

import com.donutcn.memo.entity.BriefContent;

import java.util.List;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/8/18.
 */
@Deprecated
public class MemoDiffUtil extends DiffUtil.Callback {

    private List<BriefContent> mOldData, mNewData;

    public MemoDiffUtil(List<BriefContent> mOldData, List<BriefContent> mNewData) {
        this.mOldData = mOldData;
        this.mNewData = mNewData;
    }

    @Override
    public int getOldListSize() {
        return mOldData != null ? mOldData.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewData != null ? mNewData.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldData.get(oldItemPosition).getId().equals(mNewData.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        BriefContent oldItem = mOldData.get(oldItemPosition);
        BriefContent newItem = mNewData.get(newItemPosition);
        if(!oldItem.getTitle().equals(newItem.getTitle())){
            return false;
        }
        if(!oldItem.getContent().equals(newItem.getContent())){
            return false;
        }
        if(!oldItem.getComment().equals(newItem.getComment())){
            return false;
        }
        if(!oldItem.getReadCount().equals(newItem.getReadCount())){
            return false;
        }
        if(!oldItem.getUpVote().equals(newItem.getUpVote())){
            return false;
        }
        if(!oldItem.getTime().equals(newItem.getTime())){
            return false;
        }
        return true;
    }
}
