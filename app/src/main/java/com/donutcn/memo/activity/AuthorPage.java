package com.donutcn.memo.activity;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.HaoYeAdapter;
import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.type.ItemLayoutType;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.memo.view.ListViewDecoration;

import java.util.ArrayList;
import java.util.List;

public class AuthorPage extends AppCompatActivity implements OnItemClickListener {

    private RecyclerView mRecyclerView;
    private CollapsingToolbarLayout mCollapsingToolbar;

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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new ListViewDecoration(this, R.dimen.item_decoration_height, 16, 16));
    }

    public void Refresh(){
        HaoYeAdapter adapter = new HaoYeAdapter(this, list, ItemLayoutType.TYPE_TAG);
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);

        ((TextView)findViewById(R.id.author_article_count))
                .setText(getString(R.string.placeholder_author_publish_count, adapter.getItemCount()));
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "我是第" + position + "个", Toast.LENGTH_SHORT).show();
    }

    public void onBack(View view){
        finish();
    }
}
