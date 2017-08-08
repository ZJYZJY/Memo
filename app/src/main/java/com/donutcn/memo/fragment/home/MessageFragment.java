package com.donutcn.memo.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.MessageAdapter;
import com.donutcn.memo.base.BaseScrollFragment;
import com.donutcn.memo.event.ReceiveNewMessagesEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.view.ListViewDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends BaseScrollFragment {

    private Context mContext;

    private SwipeMenuRecyclerView mMessage_rv;

    private SmartRefreshLayout mRefreshLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mMessage_rv = (SwipeMenuRecyclerView) view.findViewById(R.id.recycler_view);
        setRecyclerView(mMessage_rv);
        mRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.swipe_layout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setOnLoadmoreListener(mLoadmoreListener);

        mMessage_rv.setLayoutManager(new LinearLayoutManager(mContext));
        mMessage_rv.addItemDecoration(new ListViewDecoration(mContext, R.dimen.item_decoration_height));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Refresh();
    }

    public void Refresh() {
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            dataList.add("我是第" + i + "个。");
        }
        MessageAdapter adapter = new MessageAdapter(mContext, dataList);
        adapter.setOnItemClickListener(mOnItemClickListener);

        mMessage_rv.setAdapter(adapter);
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            EventBus.getDefault().post(new ReceiveNewMessagesEvent(1, position));
        }
    };

    private OnRefreshListener mRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(RefreshLayout refreshlayout) {
            refreshlayout.finishRefresh(1000);
        }
    };

    private OnLoadmoreListener mLoadmoreListener = new OnLoadmoreListener() {
        @Override
        public void onLoadmore(RefreshLayout refreshlayout) {
            refreshlayout.finishLoadmore(1000);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Refresh();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onRequestRefreshEvent(RequestRefreshEvent event){
        if(event.getRefreshPosition() == 1){
            mMessage_rv.scrollToPosition(0);
            mRefreshLayout.autoRefresh(0);
        }
    }
}
