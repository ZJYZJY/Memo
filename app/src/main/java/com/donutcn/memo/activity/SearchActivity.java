package com.donutcn.memo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.SearchListAdapter;
import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.memo.view.ListViewDecoration;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements OnItemClickListener {

    private RecyclerView mRecyclerView;
    private EditText mSearch_et;
    private SearchListAdapter mAdapter;

    private ArrayList<BriefContent> mList;
    private String mKeyWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSearch_et = (EditText) findViewById(R.id.toolbar_search);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new ListViewDecoration(this, R.dimen.item_decoration_height, 8, 8));
        mList = new ArrayList<>();
        mAdapter = new SearchListAdapter(SearchActivity.this, mList);
        mAdapter.setOnItemClickListener(SearchActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        mSearch_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    mKeyWords = mSearch_et.getText().toString().trim();
                    WindowUtils.toggleKeyboard(SearchActivity.this, mSearch_et, false);
                    startSearch();
                    return true;
                }
                return false;
            }
        });
    }

    public void startSearch(){
        HttpUtils.searchContent(mKeyWords).enqueue(new Callback<ArrayResponse<BriefContent>>() {
            @Override
            public void onResponse(Call<ArrayResponse<BriefContent>> call, Response<ArrayResponse<BriefContent>> response) {
                if(response.body() != null && response.body().isOk()){
                    mList.clear();
                    mList.addAll(response.body().getData());
                    mAdapter.setKeyword(mKeyWords);
                    mAdapter.notifyDataSetChanged();
                }else {
                    ToastUtil.show(SearchActivity.this, "没有找到结果");
                }
            }

            @Override
            public void onFailure(Call<ArrayResponse<BriefContent>> call, Throwable t) {
                t.printStackTrace();
                ToastUtil.show(SearchActivity.this, "搜索连接失败");
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ArticlePage.class);
        intent.putExtra("userId", mList.get(position).getUserId());
        intent.putExtra("url", mList.get(position).getUrl());
        intent.putExtra("name", mList.get(position).getName());
        intent.putExtra("userIcon", mList.get(position).getUserIcon());
        intent.putExtra("type", mList.get(position).getType());
        intent.putExtra("upvote", mList.get(position).getUpVote());
        intent.putExtra("comment", mList.get(position).getComment());
        startActivity(intent);
    }

    public void onBack(View view){
        finish();
    }

    public void onCancel(View view){
        mSearch_et.getText().clear();
        mList.clear();
        mAdapter.notifyDataSetChanged();
        WindowUtils.toggleKeyboard(this, mSearch_et, true);
    }
}
