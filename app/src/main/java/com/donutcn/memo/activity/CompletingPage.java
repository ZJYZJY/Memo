package com.donutcn.memo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.VoteItemAdapter;
import com.donutcn.memo.type.PublishType;
import com.donutcn.memo.utils.WindowUtils;

public class CompletingPage extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private VoteItemAdapter mVoteItemAdapter;

    private PublishType mContentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        mContentType = (PublishType) getIntent().getSerializableExtra("type");
        switch (mContentType) {
            case ACTIVITY:
                setContentView(R.layout.completing_page_activity);
                initActivityView();
                break;
            case VOTE:
                setContentView(R.layout.completing_page_vote);
                initVoteView();
                break;
            case RECRUIT:
                setContentView(R.layout.completing_page_recruit);
                initRecruitView();
                break;
            case RESERVE:
                setContentView(R.layout.completing_page_reserve);
                initReserveView();
                break;
            case SALE:
                setContentView(R.layout.completing_page_sale);
                initSaleView();
                break;
        }
    }

    public void initActivityView(){

    }

    public void initVoteView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.addItemDecoration(new ListViewDecoration(this, R.dimen.dp_line, 0, 0));
        mVoteItemAdapter = new VoteItemAdapter(this);
        mVoteItemAdapter.setMaxVoteSelection(5);
        mRecyclerView.setAdapter(mVoteItemAdapter);
    }

    public void initRecruitView(){

    }

    public void initReserveView(){

    }

    public void initSaleView(){

    }

    public void onFinish(View view) {
        Intent intent = new Intent(this, SocialShareActivity.class);
        startActivity(intent);
    }

    public void onBack(View view) {
        finish();
    }
}
