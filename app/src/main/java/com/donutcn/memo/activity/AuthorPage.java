package com.donutcn.memo.activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.donutcn.memo.R;
import com.donutcn.memo.adapter.MemoAdapter;
import com.donutcn.memo.constant.FieldConfig;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.event.ChangeContentEvent;
import com.donutcn.memo.helper.LoginHelper;
import com.donutcn.memo.interfaces.OnItemClickListener;
import com.donutcn.memo.type.ItemLayoutType;
import com.donutcn.memo.utils.CollectionUtil;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.memo.view.ListViewDecoration;
import com.google.gson.internal.LinkedTreeMap;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorPage extends AppCompatActivity implements OnItemClickListener, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private LinearLayout mFollow, mMessage;
    private TextView mFollowText, mAuthorName, mAuthorSign;
    private ImageView mUserIcon;

    private RequestManager glide;
    private List<BriefContent> mList;
    private LinkedTreeMap mUserInfo;
    private String mUserId, mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_page);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        initView();
        String action = getIntent().getAction();
        if(action != null && action.equals(Intent.ACTION_VIEW)){
            String data = getIntent().getDataString();
            mUserId = data.substring(data.lastIndexOf("/") + 1);
        } else {
            mUserId = getIntent().getStringExtra("userId");
        }
        if(UserStatus.getCurrentUser().getFollowedUser().contains(mUserId)){
            // if already followed.
            setFollowed(true);
        }

        HttpUtils.getUserById(mUserId).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body() != null){
                    if(response.body().isOk()){
                        mUserInfo = (LinkedTreeMap) response.body().getData().get("user_info");
                        List<LinkedTreeMap> data = (List<LinkedTreeMap>) response.body().getData().get("article");
                        loadData(data);
                    } else if(response.body().unAuthorized()){
                        ToastUtil.show(AuthorPage.this, "登录授权过期，请重新登录");
                        LoginHelper.logout(AuthorPage.this);
                    }
                } else {
                    ToastUtil.show(AuthorPage.this, "连接失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                ToastUtil.show(AuthorPage.this, "连接失败，请检查你的网络连接");
            }
        });
    }

    public void initView(){
        glide = Glide.with(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.author_collapsing_toolbar);
        mUserIcon = (ImageView) findViewById(R.id.author_icon);
        mAuthorName = (TextView) findViewById(R.id.author_name);
        mAuthorSign = (TextView) findViewById(R.id.author_signature);
        mFollow = (LinearLayout) findViewById(R.id.author_follow);
        mMessage = (LinearLayout) findViewById(R.id.author_message);
        mFollowText = (TextView) findViewById(R.id.author_follow_text);

        mFollow.setOnClickListener(this);
        mMessage.setOnClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new ListViewDecoration(this, R.dimen.item_decoration_height, 16, 16));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.author_follow:
                if(LoginHelper.ifRequestLogin(this, "请先登录")){
                    return;
                }
                int action;
                if(mFollowText.getText().toString().equals("关注")){
                    action = 1;
                    setFollowed(true);
                }else {
                    action = 0;
                    setFollowed(false);
                }
                final int copyAction = action;
                HttpUtils.follow(mUserId, action).enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                        if(response.body() != null){
                            LogUtil.d("follow", response.body().toString());
                            if(response.body().isOk()){
                                ToastUtil.show(AuthorPage.this, copyAction == 1 ? "关注成功" : "取消关注成功");
                                UserStatus.getCurrentUser().follow(AuthorPage.this, mUserId, copyAction == 1);
                                EventBus.getDefault().postSticky(new ChangeContentEvent(mUserId, copyAction == 1 ? 2 : 1));
                            } else if (response.body().unAuthorized()) {
                                ToastUtil.show(AuthorPage.this, "登录授权过期，请重新登录");
                                LoginHelper.logout(AuthorPage.this);
                            } else if(response.body().isFail()){
                                setFollowed(copyAction != 1);
                                ToastUtil.show(AuthorPage.this, copyAction == 1 ? "关注" : "取消关注失败");
                            }
                        } else {
                            setFollowed(copyAction != 1);
                            ToastUtil.show(AuthorPage.this, "连接失败，服务器未知错误");
                        }
                    }
                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        t.printStackTrace();
                        setFollowed(copyAction != 1);
                        ToastUtil.show(AuthorPage.this, "连接失败");
                    }
                });
                break;
            case R.id.author_message:
                if(LoginHelper.ifRequestLogin(this, "请先登录")){
                    return;
                }
                Intent intent = new Intent(AuthorPage.this, ChatActivity.class);
                intent.putExtra("username", mUserName);
                intent.putExtra("name", (String) mUserInfo.get(FieldConfig.USER_NICKNAME));
                intent.putExtra("avatar", (String) mUserInfo.get(FieldConfig.USER_ICON_URL));
                startActivity(intent);
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

    public void loadData(List<LinkedTreeMap> data){
        WindowUtils.setToolBarTitle(this, (String) mUserInfo.get(FieldConfig.USER_NICKNAME));
        mAuthorName.setText((String) mUserInfo.get(FieldConfig.USER_NICKNAME));
        String signature = (String) mUserInfo.get(FieldConfig.USER_SIGNATURE);
        mAuthorSign.setText(signature == null ? getString(R.string.author_page_not_signature) : signature);
        glide.load((String) mUserInfo.get(FieldConfig.USER_ICON_URL)).centerCrop().into(mUserIcon);
        mUserName = (String) mUserInfo.get(FieldConfig.USER_NAME);

        try {
            mList = CollectionUtil.covertLinkedTreeMap(data, BriefContent.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MemoAdapter adapter = new MemoAdapter(this, mList, ItemLayoutType.TYPE_TAG);
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);

        ((TextView)findViewById(R.id.author_article_count))
                .setText(getString(R.string.placeholder_author_publish_count, adapter.getItemCount()));
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ArticlePage.class);
        intent.putExtra("contentId", mList.get(position).getId());
        startActivity(intent);
    }

    public void onBack(View view){
        finish();
    }
}
