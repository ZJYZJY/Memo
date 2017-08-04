package com.donutcn.memo.activity;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.MemoAdapter;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.type.ItemLayoutType;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.memo.view.ListViewDecoration;

import java.util.ArrayList;

public class AuthorPage extends AppCompatActivity implements OnItemClickListener, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private LinearLayout mFollow, mMessage;
    private TextView mFollowText;

    private ArrayList<BriefContent> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_page);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        WindowUtils.setToolBarTitle(this, "殷大侠");

        initView();
        Refresh();
    }

    public void initView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.author_collapsing_toolbar);
        mFollow = (LinearLayout) findViewById(R.id.author_follow);
        mMessage = (LinearLayout) findViewById(R.id.author_message);
        mFollowText = (TextView) findViewById(R.id.author_follow_text);

        mFollow.setOnClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new ListViewDecoration(this, R.dimen.item_decoration_height, 16, 16));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.author_follow:
                int action;
                if(mFollowText.getText().toString().equals("关注")){
                    action = 0;
                    setFollowed(true);
                }else {
                    action = 1;
                    setFollowed(false);
                }
                // TODO:http request.
                break;
            case R.id.author_message:
                break;
        }
    }

    private void setFollowed(boolean followed){
        if(followed){
            mFollowText.setText("已关注");
            mFollow.setBackgroundResource(R.drawable.radius_btn_disabled);
        }else {
            mFollowText.setText("关注");
            mFollow.setBackgroundResource(R.drawable.selector_radius_blue_btn);
        }
    }

    public void Refresh(){
        MemoAdapter adapter = new MemoAdapter(this, list, ItemLayoutType.TYPE_TAG);
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);

        ((TextView)findViewById(R.id.author_article_count))
                .setText(getString(R.string.placeholder_author_publish_count, adapter.getItemCount()));
    }

    @Override
    public void onItemClick(int position) {
    }

    public void onBack(View view){
        finish();
    }
}
