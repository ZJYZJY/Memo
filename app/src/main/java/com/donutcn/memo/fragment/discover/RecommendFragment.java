package com.donutcn.memo.fragment.discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.base.BaseMemoFragment;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.fragment.api.FetchContent;
import com.donutcn.memo.presenter.MemoPresenter;
import com.donutcn.memo.activity.SearchActivity;
import com.donutcn.memo.adapter.MemoAdapter;
import com.donutcn.memo.event.ChangeContentEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.type.ItemLayoutType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class RecommendFragment extends BaseMemoFragment {

    @Override
    public void initMemoPresenter() {
        mMemoPresenter = new MemoPresenter(this, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommend, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView mSearch_tv = (TextView) view.findViewById(R.id.recommend_search);
        mSearch_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchActivity.class));
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        mList = new ArrayList<>();
        mAdapter = new MemoAdapter(mContext, mList, ItemLayoutType.AVATAR_IMG);
        mAdapter.setOnItemClickListener(this);
        mMemo_rv.setAdapter(mAdapter);
        mMemoPresenter.refresh(mList);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMemoPresenter.refresh(mList);
    }

    @Subscribe
    public void onRequestRefreshEvent(RequestRefreshEvent event){
        if(event.getRefreshPosition() == 2){
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
        }
    }
}
