package com.donutcn.memo.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.HaoYeAdapter;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.type.ItemLayoutType;
import com.donutcn.memo.view.ListViewDecoration;

import java.util.ArrayList;
import java.util.List;

public class AuthorPage extends AppCompatActivity implements OnItemClickListener {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_page);

        initView();
        Refresh();
    }

    public void initView(){
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    public void Refresh(){
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            dataList.add("我是第" + i + "个。");
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new ListViewDecoration(this, R.dimen.item_decoration, 8, 8));
        HaoYeAdapter adapter = new HaoYeAdapter(dataList, ItemLayoutType.NO_IMG);
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);

        ((TextView)findViewById(R.id.author_article_count)).setText(String.valueOf(adapter.getItemCount()));
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "我是第" + position + "个", Toast.LENGTH_SHORT).show();
    }

    public void onBack(View view){
        finish();
    }
}
