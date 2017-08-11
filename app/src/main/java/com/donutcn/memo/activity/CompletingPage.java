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
import com.donutcn.memo.event.FinishEditVoteItemsEvent;
import com.donutcn.memo.type.PublishType;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.widgetlib.widget.CheckBox;
import com.donutcn.widgetlib.widget.SwitchView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private List<String> mVoteItems;
    private String mContentId;
    private String field1Str, field2Str, field3Str;
    private String mContentStr, mTitleStr;
    private HashMap mExtraInfo;

    private boolean mEditMode = false;
    private boolean needToApply = false;
    private int needExtra1 = 0;
    private int needExtra2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        EventBus.getDefault().register(this);
        mContext = this;
        mContentId = getIntent().getStringExtra("contentId");
        if(mContentId != null){
            mEditMode = true;
            mExtraInfo = (HashMap) getIntent().getSerializableExtra("extraInfo");
        }
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

        if(mEditMode){
            name.setChecked(mExtraInfo.get("is_name").equals("1"));
            phone.setChecked(mExtraInfo.get("is_tel_number").equals("1"));
            extra1.setChecked(mExtraInfo.get("extra1").equals("1"));
            extra2.setChecked(mExtraInfo.get("extra2").equals("1"));
            apply.setOpened(mExtraInfo.get("is_sign_up").equals("1"));
            field1.setText((String) mExtraInfo.get("field1"));
            field2.setText((String) mExtraInfo.get("field2"));
        }
    }

    public void initVoteView() {
        List<String> items = new ArrayList<>();
        if(mEditMode){
            items = (List<String>) mExtraInfo.get("data");
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.addItemDecoration(new ListViewDecoration(this, R.dimen.dp_line, 0, 0));
        mVoteItemAdapter = new VoteItemAdapter(this, items);
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

        if(mEditMode){
            name.setChecked(mExtraInfo.get("is_name").equals("1"));
            phone.setChecked(mExtraInfo.get("is_tel_number").equals("1"));
            extra1.setChecked(mExtraInfo.get("extra1").equals("1"));
            extra2.setChecked(mExtraInfo.get("extra2").equals("1"));
            apply.setOpened(mExtraInfo.get("is_sign_up").equals("1"));
            field1.setText((String) mExtraInfo.get("field1"));
            field2.setText((String) mExtraInfo.get("field2"));
            field3.setText((String) mExtraInfo.get("field3"));
        }
    }

    public void initSaleView() {
        field1 = (EditText) findViewById(R.id.comment_field1);
        field2 = (EditText) findViewById(R.id.comment_field2);
        field3 = (EditText) findViewById(R.id.comment_field3);

        if(mEditMode){
            field1.setText((String) mExtraInfo.get("field1"));
            field2.setText((String) mExtraInfo.get("field2"));
            field3.setText((String) mExtraInfo.get("field3"));
        }
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
//                mVoteItems = mVoteItemAdapter.getTextArray();
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
        HttpUtils.publishContent(mContentId, mTitleStr, mContentType.toString(), mContentStr)
                .enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null) {
                    if(response.body().isOk()){
                        mContentId = response.body().getField("article_id");
                        Intent mShareIntent = new Intent(mContext, SocialShareActivity.class);
                        mShareIntent.putExtra("contentId", mContentId);
                        mShareIntent.putExtra("contentUrl", String.valueOf(response.body().getField("url")));
                        mShareIntent.putExtra("title", (String) response.body().getField("title"));
                        mShareIntent.putExtra("content", (String) response.body().getField("content"));
                        mShareIntent.putExtra("picUrl", (String) response.body().getField("picurl"));
                        mShareIntent.putExtra("isPrivate", Integer.valueOf((String) response.body().getField("is_private")));
                        completeInfo(mShareIntent);
                    }
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

    private void completeInfo(final Intent intent) {
        HttpUtils.completeInfo(mContentId, mContentType, field1Str, field2Str, field3Str, needToApply,
                needExtra1, needExtra2, mVoteItems, mEditMode).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null) {
                    if(response.body().isOk()){
                        ToastUtil.show(mContext, "发布成功");
                        startActivity(intent);
                        finish();
                    }
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

    @Subscribe
    public void onFinishEditVoteItemsEvent(FinishEditVoteItemsEvent event){
        mVoteItems = event.getVoteItem();
        onFinish(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onBack(View view) {
        finish();
    }
}
