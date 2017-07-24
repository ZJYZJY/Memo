package com.donutcn.memo.fragment.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MessageFragment extends BaseScrollFragment implements
        SwipeRefreshLayout.OnRefreshListener, Observer {

    private Context mContext;

    private SwipeMenuRecyclerView mMessage_rv;

    private SwipeRefreshLayout mRefreshLayout;

    private ReceiveNewMessagesEvent mReceiveNewMessagesEvent;

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
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mRefreshLayout.setOnRefreshListener(this);

        mMessage_rv.setLayoutManager(new LinearLayoutManager(mContext));
        mMessage_rv.addItemDecoration(new ListViewDecoration(mContext,
                R.dimen.item_decoration_height, 0, 0));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mReceiveNewMessagesEvent = new ReceiveNewMessagesEvent(mContext, 0);
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

        mRefreshLayout.setRefreshing(false);
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            mReceiveNewMessagesEvent.onReceiveNewMessages(1, position);
        }
    };

    @Override
    public void onRefresh() {
        Refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        Refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mReceiveNewMessagesEvent.deleteObservers();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof RequestRefreshEvent && (int) arg == 0) {
//            mMessage_rv.scrollToPosition(0);
//            mRefreshLayout.startRefresh();
        }
    }
}
