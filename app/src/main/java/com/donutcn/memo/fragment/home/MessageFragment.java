package com.donutcn.memo.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donutcn.memo.R;
import com.donutcn.memo.activity.MessageDetail;
import com.donutcn.memo.adapter.BriefMessageAdapter;
import com.donutcn.memo.base.BaseScrollFragment;
import com.donutcn.memo.entity.BriefMessage;
import com.donutcn.memo.event.ChangeRedDotEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.view.ListViewDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends BaseScrollFragment {

    private SwipeMenuRecyclerView mMessage_rv;
    private SmartRefreshLayout mRefreshLayout;

    private Context mContext;
    private BriefMessageAdapter mAdapter;
    private List<BriefMessage> mList;

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
        mRefreshLayout.setEnableLoadmore(false);

        mMessage_rv.setLayoutManager(new LinearLayoutManager(mContext));
        mMessage_rv.addItemDecoration(new ListViewDecoration(mContext, R.dimen.item_decoration_height));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mList = new ArrayList<>();
        mAdapter = new BriefMessageAdapter(mContext, mList);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mMessage_rv.setAdapter(mAdapter);
        Refresh();
    }

    public void Refresh() {


//        mAdapter.notifyDataSetChanged();
        mRefreshLayout.finishRefresh();
    }

    private OnRefreshListener mRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(RefreshLayout refreshlayout) {
            Refresh();
        }
    };

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            EventBus.getDefault().postSticky(new ChangeRedDotEvent(1, -1));
            clearRedDot(mList.get(position).getId());
            Intent intent = new Intent(mContext, MessageDetail.class);
            intent.putExtra("message_id", mList.get(position).getId());
            startActivity(intent);
        }
    };

    private BriefMessage sameMessage(String id){
        for(BriefMessage msg : mList){
            if(msg.getId().equals(id)){
                return msg;
            }
        }
        return null;
    }

    private void clearRedDot(String id){
        for(BriefMessage msg : mList){
            if(msg.getId().equals(id)){
                msg.setNewMsgCount(0);
            }
        }
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

    /**
     * receive new push message
     * @param event message object.
     */
    @Subscribe(sticky = true)
    public void onReceiveBriefMessage(BriefMessage event){
        BriefMessage msg = sameMessage(event.getId());
        if (msg == null) {
            mList.add(0, event);
        } else {
            int msgCount = msg.getNewMsgCount();
            event.setNewMsgCount(event.getNewMsgCount() + msgCount);
            mList.remove(msg);
            mList.add(0, event);
        }
        mAdapter.notifyDataSetChanged();
    }
}
