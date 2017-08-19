package com.donutcn.memo.utils;

import android.support.v7.util.DiffUtil;

import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.entity.BriefMessage;

import java.util.List;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/8/18.
 */

public class MessageDiffUtil extends DiffUtil.Callback {

    private List<BriefMessage> mOldData, mNewData;

    public MessageDiffUtil(List<BriefMessage> mOldData, List<BriefMessage> mNewData) {
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
        BriefMessage oldItem = mOldData.get(oldItemPosition);
        BriefMessage newItem = mNewData.get(newItemPosition);
        if(oldItem.getTitle().equals(newItem.getTitle())){
            return false;
        }
        if(oldItem.getSubTitle().equals(newItem.getSubTitle())){
            return false;
        }
        if(oldItem.getNewMsgCount() == newItem.getNewMsgCount()){
            return false;
        }
        if(oldItem.getTime().equals(newItem.getTime())){
            return false;
        }
        return true;
    }
}
