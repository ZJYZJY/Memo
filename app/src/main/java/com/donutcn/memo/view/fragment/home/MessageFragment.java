package com.donutcn.memo.view.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donutcn.memo.R;
import com.donutcn.memo.view.activity.ChatActivity;
import com.donutcn.memo.view.activity.MessageDetail;
import com.donutcn.memo.adapter.BriefMessageAdapter;
import com.donutcn.memo.base.BaseScrollFragment;
import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.BriefMessage;
import com.donutcn.memo.event.ChangeRedDotEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.interfaces.OnItemClickListener;
import com.donutcn.memo.utils.CollectionUtil;
import com.donutcn.memo.utils.FileCacheUtil;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.view.ListViewDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.donutcn.memo.utils.FileCacheUtil.MESSAGE_LIST_CACHE;

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
        EventBus.getDefault().register(this);
        super.onActivityCreated(savedInstanceState);
        mList = new ArrayList<>();
        mAdapter = new BriefMessageAdapter(mContext, mList);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mMessage_rv.setAdapter(mAdapter);
        String cache = FileCacheUtil.getCache(mContext, MESSAGE_LIST_CACHE, FileCacheUtil.CACHE_SHORT_TIMEOUT);
        if("".equals(cache))
            Refresh();
        else{
            List<BriefMessage> temp = new Gson().fromJson(cache, new TypeToken<List<BriefMessage>>(){}.getType());
            mList.addAll(temp);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void Refresh() {
        HttpUtils.getReplyList().enqueue(new Callback<ArrayResponse<BriefMessage>>() {
            @Override
            public void onResponse(Call<ArrayResponse<BriefMessage>> call,
                                   Response<ArrayResponse<BriefMessage>> response) {
                if(response.body() != null){
                    LogUtil.d(response.body().toString());
                    if(response.body().isOk()){
                        mList.clear();
                        mList.addAll(0, response.body().getData());
//                        mList = CollectionUtil.removeDuplicateWithOrder(mList);
                        mAdapter.notifyDataSetChanged();
                        FileCacheUtil.setMessageListCache(mContext, new Gson().toJson(mList));
                    } else {
//                        ToastUtil.show(mContext, response.body().getMessage());
                    }
                } else {
                    ToastUtil.show(mContext, "服务器未知错误");
                }
                mRefreshLayout.finishRefresh();
            }

            @Override
            public void onFailure(Call<ArrayResponse<BriefMessage>> call, Throwable t) {
                t.printStackTrace();
                mRefreshLayout.finishRefresh();
                ToastUtil.show(mContext, "消息列表连接失败");
            }
        });
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
            int length = mList.get(position).getType().length();
            if(length > 5 || length == 0){
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("username", mList.get(position).getId());
                intent.putExtra("name", mList.get(position).getTitle());
                intent.putExtra("avatar", mList.get(position).getType());
                startActivity(intent);
            } else {
                Intent intent = new Intent(mContext, MessageDetail.class);
                intent.putExtra("messageId", mList.get(position).getId());
                intent.putExtra("count", mList.get(position).getNewMsgCount());
                intent.putExtra("type", mList.get(position).getType());
                intent.putExtra("title", mList.get(position).getTitle());
                intent.putExtra("date", mList.get(position).getDate());
                intent.putExtra("position", position);
                startActivityForResult(intent, 0);
            }
            EventBus.getDefault().postSticky(new ChangeRedDotEvent(1, -1));
            clearRedDot(mList.get(position).getId());
            mAdapter.notifyItemChanged(position);
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
        FileCacheUtil.setMessageListCache(mContext, new Gson().toJson(mList));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if(resultCode > 0){
                mList.remove(resultCode);
                mAdapter.notifyItemRemoved(resultCode);
                FileCacheUtil.setMessageListCache(mContext, new Gson().toJson(mList));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Refresh();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onReceiveBriefMessage(BriefMessage event){
        BriefMessage msg = sameMessage(event.getId());
//        List<BriefMessage> old = mList;
        if (msg == null) {
            mList.add(0, event);
        } else {
            int msgCount = msg.getNewMsgCount();
            msg.setNewMsgCount(event.getNewMsgCount() + msgCount);
            mList.remove(msg);
            msg.setType(event.getType());
            msg.setSubTitle(event.getSubTitle());
            msg.setTime(event.getTime());
            mList.add(0, msg);
        }
//        DiffUtil.calculateDiff(new MessageDiffUtil(old, mList), true).dispatchUpdatesTo(mAdapter);
        mAdapter.notifyDataSetChanged();
        FileCacheUtil.setMessageListCache(mContext, new Gson().toJson(mList));
    }
}
