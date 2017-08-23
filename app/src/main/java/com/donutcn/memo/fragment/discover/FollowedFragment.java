package com.donutcn.memo.fragment.discover;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.donutcn.memo.adapter.MemoAdapter;
import com.donutcn.memo.base.BaseMemoFragment;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.event.ChangeContentEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.presenter.MemoPresenter;
import com.donutcn.memo.type.ItemLayoutType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class FollowedFragment extends BaseMemoFragment {

    @Override
    public void initMemoPresenter() {
        mMemoPresenter = new MemoPresenter(this, 3);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        mList = new ArrayList<>();
        mAdapter = new MemoAdapter(mContext, mList, ItemLayoutType.AVATAR_IMG);
        mAdapter.setOnItemClickListener(this);
        mMemo_rv.setAdapter(mAdapter);
    }

    @Subscribe
    public void onRequestRefreshEvent(RequestRefreshEvent event){
        if(event.getRefreshPosition() == 4){
            mMemo_rv.scrollToPosition(0);
            mRefreshLayout.autoRefresh(0);
        }
    }

    @Subscribe(sticky = true)
    public void onChangeContentEvent(ChangeContentEvent event) {
        if (event.getType() == 0) {
            String contentId = event.getId();
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getId().equals(contentId)) {
                    mList.remove(i);
                    mAdapter.notifyItemRemoved(i);
                    break;
                }
            }
        } else if (event.getType() == 1) {
            String userId = event.getId();
            List<BriefContent> old = mList;
            List<BriefContent> toBeRemoved = new ArrayList<>();
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getUserId().equals(userId)) {
                    toBeRemoved.add(mList.get(i));
                }
            }
            mList.removeAll(toBeRemoved);
//            DiffUtil.calculateDiff(new MemoDiffUtil(old, mList), true).dispatchUpdatesTo(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else if (event.getType() == 2) {
            mList.clear();
            mRefreshLayout.autoRefresh(0);
        }
    }
}
