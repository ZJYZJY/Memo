package com.donutcn.memo.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.donutcn.memo.R;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.widgetlib.widget.SwitchView;
import com.umeng.socialize.ShareAction;
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
    private String contentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_share);
        WindowUtils.setToolBarTitle(this, R.string.title_activity_share);
        WindowUtils.setToolBarButton(this, R.string.btn_share_homepage);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        initView();
        initMedia();
        contentId = getIntent().getStringExtra("contentId");
    }

    public void initView() {
        mSwith = (SwitchView) findViewById(R.id.social_content_private);
        mSwith.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                // not private
                setPrivate(!view.isOpened());
            }

            @Override
            public void toggleToOff(SwitchView view) {
                // set private
                setPrivate(!view.isOpened());
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

    public void setPrivate(final boolean isPrivate) {
        HttpUtils.setPrivate(contentId, isPrivate ? 1 : 0).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body().isOk()) {
                    if(isPrivate){
                        ToastUtil.show(SocialShareActivity.this, "设为私有");
                    }else {
                        ToastUtil.show(SocialShareActivity.this, "设为公开");
                    }
                } else {
                    ToastUtil.show(SocialShareActivity.this, "设置失败");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                ToastUtil.show(SocialShareActivity.this, "设置连接失败");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_with_btn:
                Intent intent0 = new Intent(this, MainActivity.class);
                intent0.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent0);
                break;
            case R.id.share_iv1:
                new ShareAction(SocialShareActivity.this)
                        .withMedia(mUMWeb)
                        .setPlatform(SHARE_MEDIA.WEIXIN)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.share_iv2:
                new ShareAction(SocialShareActivity.this)
                        .withMedia(mUMWeb)
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.share_iv3:
                new ShareAction(SocialShareActivity.this)
                        .withMedia(mUMWeb)
                        .setPlatform(SHARE_MEDIA.QQ)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.share_iv4:
                new ShareAction(SocialShareActivity.this)
                        .withMedia(mUMWeb)
                        .setPlatform(SHARE_MEDIA.QZONE)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.share_iv5:
                new ShareAction(SocialShareActivity.this)
                        .withText("hello")
                        .withMedia(mUMWeb)
                        .setPlatform(SHARE_MEDIA.SINA)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.share_iv6:
                break;
            case R.id.share_iv7:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText("复制成功");
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

    public void initMedia() {
        mUMWeb = new UMWeb("http://bbs.umeng.com/");
        mUMWeb.setTitle("This is web title");
        mUMWeb.setThumb(new UMImage(this, R.drawable.pub_keyboard));
        mUMWeb.setDescription("my description");
    }

    public void onBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }
}
