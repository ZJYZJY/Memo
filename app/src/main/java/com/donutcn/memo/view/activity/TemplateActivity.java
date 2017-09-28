package com.donutcn.memo.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.GridView;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.TemplateListAdapter;
import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.MessageItem;
import com.donutcn.memo.entity.Template;
import com.donutcn.memo.interfaces.OnItemClickListener;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.WindowUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TemplateActivity extends AppCompatActivity implements OnItemClickListener {

    private RecyclerView mRecyclerView;
    private TemplateListAdapter mAdapter;

    private Context mContext;
    private List<Template> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        WindowUtils.setToolBarTitle(this, getString(R.string.title_activity_template));

        mContext = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_template);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mList = new ArrayList<>();

        HttpUtils.getTemplateList().enqueue(new Callback<ArrayResponse<Template>>() {
            @Override
            public void onResponse(Call<ArrayResponse<Template>> call, Response<ArrayResponse<Template>> response) {
                if(response.body() != null){
                    if(response.body().isOk()){
                        mList.addAll(response.body().getData());
                        mAdapter = new TemplateListAdapter(mContext, mList);
                        mAdapter.setOnItemClickListener(TemplateActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        ToastUtil.show(mContext, "获取失败");
                    }
                } else {
                    ToastUtil.show(mContext, "获取失败，服务器内部错误");
                }
            }

            @Override
            public void onFailure(Call<ArrayResponse<Template>> call, Throwable t) {
                t.printStackTrace();
                ToastUtil.show(mContext, "连接失败");
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent();
        intent.putExtra("template", mList.get(position).getContent());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onBack(View view){
        finish();
    }
}
