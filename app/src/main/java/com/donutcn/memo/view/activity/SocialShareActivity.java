package com.donutcn.memo.view.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.helper.ShareHelper;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.SpfsUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.widgetlib.widget.SwitchView;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialShareActivity extends AppCompatActivity implements View.OnClickListener {

    private SwitchView mSwith;
    private Button mHomePage;

    private UMWeb mUMWeb;
    private String contentId, contentUrl, title, content, picUrl;
    private int isPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_share);
        WindowUtils.setToolBarTitle(this, R.string.title_activity_share);
        WindowUtils.setToolBarButton(this, R.string.btn_share_homepage);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        isPrivate = getIntent().getIntExtra("isPrivate", 0);
        contentId = getIntent().getStringExtra("contentId");
        contentUrl = getIntent().getStringExtra("contentUrl");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        picUrl = getIntent().getStringExtra("picUrl");
        if(title != null){
            ((TextView)findViewById(R.id.share_title))
                    .setText(getString(R.string.placeholder_share_title, title));
        }
        // remove drafts.
        SpfsUtils.clear(this, SpfsUtils.CACHE);
        initView();
    }

    public void initView() {
        mSwith = (SwitchView) findViewById(R.id.social_content_private);
        mSwith.setOpened(isPrivate == 0);
        mSwith.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                // set public
                setPrivate(view.isOpened(), view);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                // set private
                setPrivate(view.isOpened(), view);
            }
        });
        mHomePage = (Button) findViewById(R.id.toolbar_with_btn);

        findViewById(R.id.share_iv1).setOnClickListener(this);
        findViewById(R.id.share_iv2).setOnClickListener(this);
        findViewById(R.id.share_iv3).setOnClickListener(this);
        findViewById(R.id.share_iv4).setOnClickListener(this);
        findViewById(R.id.share_iv5).setOnClickListener(this);
        findViewById(R.id.share_iv6).setOnClickListener(this);
        findViewById(R.id.share_iv7).setOnClickListener(this);
        findViewById(R.id.share_iv8).setOnClickListener(this);

        mHomePage.setOnClickListener(this);
    }

    public void setPrivate(final boolean isPrivate, final SwitchView view) {
        HttpUtils.setPrivate(contentId, isPrivate ? 1 : 0).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null) {
                    if(response.body().isOk()){
                        if(isPrivate){
                            ToastUtil.show(SocialShareActivity.this, "设为私有");
                            view.toggleSwitch(false);
                        }else {
                            ToastUtil.show(SocialShareActivity.this, "设为公开");
                            view.toggleSwitch(true);
                        }
                    } else {
                        view.toggleSwitch(isPrivate);
                        ToastUtil.show(SocialShareActivity.this, response.body().getMessage());
                    }
                } else {
                    view.toggleSwitch(isPrivate);
                    ToastUtil.show(SocialShareActivity.this, "设置失败");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                view.toggleSwitch(isPrivate);
                ToastUtil.show(SocialShareActivity.this, "设置连接失败");
            }
        });
    }

    @Override
    public void onClick(View v) {
        initWebMedia();
        switch (v.getId()) {
            case R.id.toolbar_with_btn:
                Intent intent0 = new Intent(this, MainActivity.class);
                intent0.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent0);
                break;
            case R.id.share_iv1:
                new ShareHelper(this).shareWechat(mUMWeb);
                break;
            case R.id.share_iv2:
                new ShareHelper(this).shareWechatCirlce(mUMWeb);
                break;
            case R.id.share_iv3:
                new ShareHelper(this).shareQQ(mUMWeb);
                break;
            case R.id.share_iv4:
                new ShareHelper(this).shareQzone(mUMWeb);
                break;
            case R.id.share_iv5:
                new ShareHelper(this).shareWeibo(mUMWeb);
                break;
            case R.id.share_iv6:
                Intent shareIntent = new Intent(SocialShareActivity.this, ShareImageActivity.class);
                shareIntent.putExtra("title", title);
                shareIntent.putExtra("content", content);
                shareIntent.putExtra("url", contentUrl.substring(0, contentUrl.length() - 5));
                startActivity(shareIntent);
                break;
            case R.id.share_iv7:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(contentUrl);
                ToastUtil.show(this, "复制成功");
                break;
            case R.id.share_iv8:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");// setType("audio/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "share");
                intent.putExtra(Intent.EXTRA_TEXT, "此处是要分享的内容");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getTitle()));
                break;
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            ToastUtil.show(SocialShareActivity.this, platform + " 分享成功啦");

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtil.show(SocialShareActivity.this, platform + " 分享失败啦");
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtil.show(SocialShareActivity.this, platform + " 分享取消了");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

    public void initWebMedia() {
        mUMWeb = new UMWeb(contentUrl);
        mUMWeb.setTitle(title);
        if(picUrl != null && !picUrl.equals("")){
            mUMWeb.setThumb(new UMImage(this, picUrl));
        }
        mUMWeb.setDescription(content);
    }

    public void onBack(View view) {
        Intent intent = new Intent(this, PublishActivity.class);
        intent.putExtra("editMode", true);
        intent.putExtra("contentId", contentId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.toolbar_with_btn).performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }
}
