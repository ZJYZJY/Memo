package com.donutcn.memo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.MessageDetailAdapter;
import com.donutcn.memo.entity.MessageItem;
import com.donutcn.memo.listener.OnItemClickListener;
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

public class MessageDetail extends AppCompatActivity implements OnItemClickListener {

    private SwipeMenuRecyclerView mMsg_rv;
    private SmartRefreshLayout mRefreshLayout;

    private SwipeMenuAdapter mAdapter;
    private List<MessageItem> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        String type = getIntent().getStringExtra("type");
        WindowUtils.setToolBarTitle(this, "招聘");

        String contetnId = getIntent().getStringExtra("contentId");
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
        mAdapter = new MessageDetailAdapter(this, mList);
        ((MessageDetailAdapter)mAdapter).setOnItemClickListener(this);
        mMsg_rv.setAdapter(mAdapter);
    }

    public void initView(String title, String date){
        ((TextView) findViewById(R.id.detail_content_title)).setText(title);
        ((TextView) findViewById(R.id.detail_content_date)).setText(date);
        findViewById(R.id.detail_content_reference).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mMsg_rv = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.swipe_layout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setOnLoadmoreListener(mLoadmoreListener);

        mMsg_rv.setLayoutManager(new LinearLayoutManager(this));
        mMsg_rv.addItemDecoration(new ListViewDecoration(this, R.dimen.item_decoration_height));
    }

    public void Refresh(){
        mRefreshLayout.finishRefresh(1000);
    }

    public void LoadMore(){
        mRefreshLayout.finishLoadmore(1000);
    }

    private OnRefreshListener mRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(RefreshLayout refreshlayout) {
            Refresh();
        }
    };

    private OnLoadmoreListener mLoadmoreListener = new OnLoadmoreListener() {
        @Override
        public void onLoadmore(RefreshLayout refreshlayout) {
            if(mList.size() > 0){
                LoadMore();
            }
        }
    };

    @Override
    public void onItemClick(int position) {

    }

    public void onBack(View view){
        finish();
    }
}
