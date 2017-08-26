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
import com.donutcn.memo.fragment.api.DeleteContent;
import com.donutcn.memo.fragment.api.FetchContent;
import com.donutcn.memo.interfaces.OnItemClickListener;
import com.donutcn.memo.presenter.MessagePresenter;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.memo.view.ListViewDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageDetail extends AppCompatActivity implements OnItemClickListener, FetchContent<MessageItem>, DeleteContent {

    private SwipeMenuRecyclerView mMsg_rv;
    private SmartRefreshLayout mRefreshLayout;

    private MessagePresenter mMsgPresenter;
    private SwipeMenuAdapter mAdapter;
    private List<MessageItem> mList;

    public boolean isLoadMore = false;
    public boolean canLoadMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        String type = getIntent().getStringExtra("type");
        WindowUtils.setToolBarTitle(this, "招聘");

        mMsgPresenter = new MessagePresenter(this);
        String contetnId = getIntent().getStringExtra("messageId");
        // Todo : http request, get the message list.
        String title = getIntent().getStringExtra("title");
        String date = getIntent().getStringExtra("date");
        initView("360智能硬件招聘开始了", "2017-08-05");

        mList = new ArrayList<>();
        mList.add(new MessageItem());
        mList.add(new MessageItem());
        mList.add(new MessageItem());
        mList.add(new MessageItem());
        mList.add(new MessageItem());
        mAdapter = new MessageDetailAdapter(this, mList, type);
        ((MessageDetailAdapter)mAdapter).setOnItemClickListener(this);
        mMsg_rv.setAdapter(mAdapter);
    }

    public void initView(String title, String date){
        ((TextView) findViewById(R.id.detail_content_title)).setText(title);
        ((TextView) findViewById(R.id.detail_content_date)).setText(date);
        findViewById(R.id.detail_content_reference).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageDetail.this, ArticlePage.class);
                // Todo:pass the content id to ArticlePage.
                intent.putExtra("contentId", "id");
                startActivity(intent);
            }
        });

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
                if(lastVisibleItem + 5 >= mList.size() && mList.size() > 0){
                    if(!isLoadMore && canLoadMore){
                        isLoadMore = true;
                        mMsgPresenter.loadMore(mList);
                    }
                }
            }
        });
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
            if(mList.size() > 0 && !isLoadMore && canLoadMore){
                isLoadMore = true;
                mMsgPresenter.loadMore(mList);
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
    public void refreshSuccess(List<MessageItem> list) {
        mRefreshLayout.finishRefresh();
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
        mRefreshLayout.finishLoadmore();
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

    }

    @Override
    public void deleteFail(int code, String error) {

    }
}
