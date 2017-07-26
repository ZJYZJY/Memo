package com.donutcn.memo.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.MessageAdapter;
import com.donutcn.memo.base.BaseScrollFragment;
import com.donutcn.memo.event.ReceiveNewMessagesEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.view.ListViewDecoration;
import com.donutcn.memo.view.RefreshHeaderView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends BaseScrollFragment {

    private Context mContext;

    private SwipeMenuRecyclerView mMessage_rv;

    private TwinklingRefreshLayout mRefreshLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mMessage_rv = (SwipeMenuRecyclerView) view.findViewById(R.id.recycler_view);
        setRecyclerView(mMessage_rv);
        mRefreshLayout = (TwinklingRefreshLayout) view.findViewById(R.id.swipe_layout);
        mRefreshLayout.setOnRefreshListener(mRefreshListenerAdapter);
        mRefreshLayout.setHeaderView(new RefreshHeaderView(mContext));

        mMessage_rv.setLayoutManager(new LinearLayoutManager(mContext));
        mMessage_rv.addItemDecoration(new ListViewDecoration(mContext,
                R.dimen.item_decoration_height, 0, 0));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
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

    private RefreshListenerAdapter mRefreshListenerAdapter = new RefreshListenerAdapter() {
        @Override
        public void onRefresh(TwinklingRefreshLayout refreshLayout) {
            super.onRefresh(refreshLayout);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.finishRefreshing();
                }
            }, 1000);
        }

        @Override
        public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
            super.onLoadMore(refreshLayout);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.finishLoadmore();
                }
            }, 1000);
        }

        @Override
        public void onFinishRefresh() {
            super.onFinishRefresh();
            Toast.makeText(getContext(), "onFinishRefresh", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinishLoadMore() {
            super.onFinishLoadMore();
            Toast.makeText(getContext(), "onFinishLoadMore", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onRequestRefreshEvent(RequestRefreshEvent event){
        if(event.getRefreshPosition() == 1){
            mMessage_rv.scrollToPosition(0);
            mRefreshLayout.startRefresh();
        }
    }
}
