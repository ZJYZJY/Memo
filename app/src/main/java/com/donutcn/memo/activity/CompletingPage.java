package com.donutcn.memo.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.VoteItemAdapter;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.type.PublishType;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.widgetlib.widget.CheckBox;
import com.donutcn.widgetlib.widget.SwitchView;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletingPage extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private VoteItemAdapter mVoteItemAdapter;
    private EditText field1, field2, field3;
    private CheckBox name, phone, extra1, extra2;
    private SwitchView apply;

    private Context mContext;
    private PublishType mContentType;
    private ArrayList<String> voteItems;
    private String contentId;
    private String field1Str, field2Str, field3Str;
    private String mContentStr, mTitleStr;

    private boolean needToApply = false;
    private int needExtra1 = 0;
    private int needExtra2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        mContext = this;
        mTitleStr = getIntent().getStringExtra("title");
        mContentStr = getIntent().getStringExtra("content");
        mContentType = (PublishType) getIntent().getSerializableExtra("type");
        switch (mContentType) {
            case ACTIVITY:
                setContentView(R.layout.completing_page_activity);
                initActivityOrReserve();
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
                initActivityOrReserve();
                break;
            case SALE:
                setContentView(R.layout.completing_page_sale);
                initSaleView();
                break;
        }
    }

    public void initActivityOrReserve() {
        name = (CheckBox) findViewById(R.id.need_name);
        phone = (CheckBox) findViewById(R.id.need_phone);
        extra1 = (CheckBox) findViewById(R.id.need_extra1);
        extra2 = (CheckBox) findViewById(R.id.need_extra2);
        apply = (SwitchView) findViewById(R.id.need_apply);
        field1 = (EditText) findViewById(R.id.comment_field1);
        field2 = (EditText) findViewById(R.id.comment_field2);
    }

    public void initVoteView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.addItemDecoration(new ListViewDecoration(this, R.dimen.dp_line, 0, 0));
        mVoteItemAdapter = new VoteItemAdapter(this);
        mRecyclerView.setAdapter(mVoteItemAdapter);
    }

    public void initRecruitView() {
        name = (CheckBox) findViewById(R.id.need_name);
        phone = (CheckBox) findViewById(R.id.need_phone);
        extra1 = (CheckBox) findViewById(R.id.need_extra1);
        extra2 = (CheckBox) findViewById(R.id.need_extra2);
        apply = (SwitchView) findViewById(R.id.need_apply);
        field1 = (EditText) findViewById(R.id.comment_field1);
        field2 = (EditText) findViewById(R.id.comment_field2);
        field3 = (EditText) findViewById(R.id.comment_field3);
    }

    public void initSaleView() {
        field1 = (EditText) findViewById(R.id.comment_field1);
        field2 = (EditText) findViewById(R.id.comment_field2);
        field3 = (EditText) findViewById(R.id.comment_field3);
    }

    public void onFinish(View view) {
        switch (mContentType) {
            case ACTIVITY:
            case RESERVE:
                needToApply = apply.isOpened();
                needExtra1 = extra1.isChecked() ? 1 : 0;
                needExtra2 = extra2.isChecked() ? 1 : 0;
                field1Str = field1.getText().toString().trim();
                field2Str = field2.getText().toString().trim();
                if (TextUtils.isEmpty(field1Str) || TextUtils.isEmpty(field2Str)) {
                    ToastUtil.show(mContext, "请填写完整信息");
                    return;
                }
                break;
            case VOTE:
                voteItems = mVoteItemAdapter.getTextArray();
                break;
            case RECRUIT:
                needToApply = apply.isOpened();
                needExtra1 = extra1.isChecked() ? 1 : 0;
                needExtra2 = extra2.isChecked() ? 1 : 0;
                field1Str = field1.getText().toString().trim();
                field2Str = field2.getText().toString().trim();
                field3Str = field3.getText().toString().trim();
                if (TextUtils.isEmpty(field1Str) || TextUtils.isEmpty(field2Str)
                        || TextUtils.isEmpty(field3Str)) {
                    ToastUtil.show(mContext, "请填写完整信息");
                    return;
                }
                break;
            case SALE:
                field1Str = field1.getText().toString().trim();
                field2Str = field2.getText().toString().trim();
                field3Str = field3.getText().toString().trim();
                if (TextUtils.isEmpty(field1Str) || TextUtils.isEmpty(field2Str)
                        || TextUtils.isEmpty(field3Str)) {
                    ToastUtil.show(mContext, "请填写完整信息");
                    return;
                }
                break;
        }
        HttpUtils.publishContent(mTitleStr, mContentType.toString(), mContentStr)
                .enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body().isOk()) {
                    contentId = String.valueOf(response.body().getField("article_id"));
                    completeInfo();
                } else {
                    ToastUtil.show(mContext, "发布失败");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                ToastUtil.show(mContext, "发布连接失败");
            }
        });
    }

    private void completeInfo() {
        HttpUtils.completeInfo(contentId, mContentType, field1Str, field2Str, field3Str, needToApply,
                needExtra1, needExtra2, voteItems).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body().isOk()) {
                    ToastUtil.show(mContext, "发布成功");
                    Intent intent = new Intent(mContext, SocialShareActivity.class);
                    intent.putExtra("contentId", contentId);
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtil.show(mContext, "发布信息完善失败");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                ToastUtil.show(mContext, "完善连接失败");
            }
        });
    }

    public void onBack(View view) {
        finish();
    }
}
