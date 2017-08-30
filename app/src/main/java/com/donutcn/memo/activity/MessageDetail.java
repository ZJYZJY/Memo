package com.donutcn.memo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.MessageDetailAdapter;
import com.donutcn.memo.entity.MessageItem;
import com.donutcn.memo.event.ItemActionClickEvent;
import com.donutcn.memo.fragment.api.DeleteContent;
import com.donutcn.memo.fragment.api.FetchContent;
import com.donutcn.memo.interfaces.OnItemClickListener;
import com.donutcn.memo.presenter.MessagePresenter;
import com.donutcn.memo.type.PublishType;
import com.donutcn.memo.utils.CollectionUtil;
import com.donutcn.memo.utils.FileCacheUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.memo.view.ListViewDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.donutcn.memo.utils.FileCacheUtil.MESSAGE_ITEM_CACHE;

public class MessageDetail extends AppCompatActivity implements OnItemClickListener,
        FetchContent<MessageItem>, DeleteContent {

    private SwipeMenuRecyclerView mMsg_rv;
    private SmartRefreshLayout mRefreshLayout;

    private MessagePresenter mMsgPresenter;
    private SwipeMenuAdapter mAdapter;
    private List<MessageItem> mList;
    private PublishType mType;

    public boolean isLoadMore = false;
    public boolean canLoadMore = true;
    public final int PAGE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        EventBus.getDefault().register(this);
        String type = getIntent().getStringExtra("type");
        WindowUtils.setToolBarTitle(this, type);
        mType = PublishType.getType(type);

        String contentId = getIntent().getStringExtra("messageId");
        String title = getIntent().getStringExtra("title");
        String date = getIntent().getStringExtra("date");
        int count = getIntent().getIntExtra("count", 0);
        mMsgPresenter = new MessagePresenter(this, contentId);

        mList = new ArrayList<>();
        mAdapter = new MessageDetailAdapter(this, mList, type, count);
        ((MessageDetailAdapter)mAdapter).setOnItemClickListener(this);
        initView(contentId, title, date, count);
    }

    public void initView(final String contentId, String title, String date, int count){
        ((TextView) findViewById(R.id.detail_content_title)).setText(title);
        ((TextView) findViewById(R.id.detail_content_date)).setText(date);
        findViewById(R.id.detail_content_reference).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageDetail.this, ArticlePage.class);
                intent.putExtra("contentId", contentId);
                startActivity(intent);
            }
        });
        TextView info = (TextView) findViewById(R.id.message_brief_info);
        if(count != 0){
            info.setText(getString(R.string.placeholder_new_reply, count, mType.getReply()));
        } else {
            info.setText(getString(R.string.placeholder_no_new_reply, mType.getReply()));
        }

        mMsg_rv = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.swipe_layout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setOnLoadmoreListener(mLoadmoreListener);

        mMsg_rv.setLayoutManager(new LinearLayoutManager(this));
        mMsg_rv.addItemDecoration(new ListViewDecoration(this, R.dimen.item_decoration_height));
        mMsg_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if(lastVisibleItem + 5 >= mList.size() && mList.size() >= PAGE_SIZE){
                    if(!isLoadMore && canLoadMore){
                        isLoadMore = true;
                        mMsgPresenter.loadMore(mList);
                    }
                }
            }
        });
        String cache = FileCacheUtil.getCache(this, MESSAGE_ITEM_CACHE, FileCacheUtil.CACHE_SHORT_TIMEOUT);
        if("".equals(cache) || count > 0)
            mRefreshLayout.autoRefresh(0);
        else{
            List<MessageItem> temp = new Gson().fromJson(cache, new TypeToken<List<MessageItem>>(){}.getType());
            mList.addAll(temp);
        }
        mMsg_rv.setAdapter(mAdapter);
    }

    private OnRefreshListener mRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(RefreshLayout refreshlayout) {
            mMsgPresenter.refresh(mList);
        }
    };

    private OnLoadmoreListener mLoadmoreListener = new OnLoadmoreListener() {
        @Override
        public void onLoadmore(RefreshLayout refreshlayout) {
            if(mList.size() >= PAGE_SIZE && !isLoadMore && canLoadMore){
                isLoadMore = true;
                mMsgPresenter.loadMore(mList);
            } else if(!isLoadMore) {
                mRefreshLayout.finishLoadmore();
                mRefreshLayout.setLoadmoreFinished(true);
            }
        }
    };

    @Override
    public void onItemClick(int position) {

    }

    public void onBack(View view){
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void refreshSuccess(List<MessageItem> list) {
        mList.addAll(0, list);
        mList = CollectionUtil.removeDuplicateWithOrder(mList);
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.finishRefresh();
//        FileCacheUtil.setMessageItemCache(this, new Gson().toJson(mList));
    }

    @Override
    public void refreshFail(int code, String error) {
        mRefreshLayout.finishRefresh();
        if(code == 401){

        } else if(code == 400){

        } else {
            ToastUtil.show(this, error + "，" + code);
        }
    }

    @Override
    public void loadMoreSuccess(List<MessageItem> list) {
        mList.addAll(mList.size(), list);
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.finishLoadmore();
        isLoadMore = false;
//        FileCacheUtil.setMessageItemCache(this, new Gson().toJson(mList));
    }

    @Override
    public void loadMoreFail(int code, String error) {
        mRefreshLayout.finishLoadmore();
        isLoadMore = false;
        if (code == 401) {

        } else if (code == 400) {
            canLoadMore = false;
            mRefreshLayout.setLoadmoreFinished(true);
        } else {
            ToastUtil.show(this, error + "，" + code);
        }
    }

    @Override
    public void deleteSuccess(int position) {
        mList.remove(position);
        mAdapter.notifyItemRemoved(position);
        ToastUtil.show(this, "删除成功");
//        FileCacheUtil.setMessageItemCache(this, new Gson().toJson(mList));
    }

    @Override
    public void deleteFail(int code, String error) {
        if(code == 401){

        } else {
            ToastUtil.show(this, error + "，" + code);
        }
    }

    @Subscribe
    public void onItemActionClickEvent(ItemActionClickEvent event){
        if(event.type == 0){
            mMsgPresenter.deleteContent(mList, event.position);
        } else if(event.type == 1){
            mMsgPresenter.downloadResume(mList, event.position);
        }
    }
}
