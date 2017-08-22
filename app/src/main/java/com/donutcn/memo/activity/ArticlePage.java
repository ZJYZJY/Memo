package com.donutcn.memo.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.donutcn.memo.R;
import com.donutcn.memo.constant.FieldConfig;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.event.ChangeContentEvent;
import com.donutcn.memo.helper.RouterHelper;
import com.donutcn.memo.type.PublishType;
import com.donutcn.memo.utils.DensityUtils;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.donutcn.memo.utils.WindowUtils;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticlePage extends AppCompatActivity implements View.OnClickListener {

    private BottomSheetBehavior behavior;
    private BottomSheetDialog dialog;
    private Button mInteractive;
    private TextView mName, mUpvote, mComment, mWant;
    private ScrollView mScrollView;
    private ImageView mUserIcon;
    private EditText mRealName, mPhone, mWeChat, mEmail, mReply;
    private TextView mResume;

    private PublishType mType = PublishType.ARTICLE;
    private RequestManager glide;
    private Context mContext;

    private String mContentId, mContentUrl, mNameStr, mIconUrl, mUserId;
    private int mUpvoteCount, mCommentCount;
    // comment height
    private int commentHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_page);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        mContext = this;
        glide = Glide.with(this);
        String action = getIntent().getAction();
        if(action != null && action.equals(Intent.ACTION_VIEW)){
            String data = getIntent().getDataString();
            mContentId = data.substring(data.lastIndexOf("/") + 1);
        } else {
            mContentId = getIntent().getStringExtra("contentId");
        }
        HttpUtils.verifyContentById(mContentId).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body() != null){
                    if(response.body().isOk()){
                        mUserId = response.body().getField(FieldConfig.USER_ID);
                        mContentUrl = response.body().getField(FieldConfig.CONTENT_URL);
                        mNameStr = response.body().getField(FieldConfig.USER_NICKNAME);
                        mIconUrl = response.body().getField(FieldConfig.USER_ICON_URL);
                        String type = response.body().getField(FieldConfig.CONTENT_TYPE);
                        mType = PublishType.getType(type);
                        mUpvoteCount = Integer.valueOf((String) response.body().getField(FieldConfig.CONTENT_UP_VOTE_COUNT));
                        mCommentCount = Integer.valueOf((String) response.body().getField(FieldConfig.CONTENT_COMM_COUNT));
                        initView();
                        showContent();
                    } else if(response.body().notFound()){
                        ToastUtil.show(ArticlePage.this, "该文章不存在，或已经被删除");
                        EventBus.getDefault().postSticky(new ChangeContentEvent(mContentId, 0));
                        finish();
                    } else {
                        ToastUtil.show(ArticlePage.this, "连接失败，服务器未知错误");
                    }
                } else {
                    ToastUtil.show(ArticlePage.this, "连接失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                ToastUtil.show(ArticlePage.this, "连接失败，请检查你的网络");
            }
        });


    }

    public void initView() {
        boolean self = getIntent().getBooleanExtra("self", false);
        if (self) {
            mIconUrl = UserStatus.getCurrentUser().getIconUrl();
            mNameStr = UserStatus.getCurrentUser().getName();
        }

        mName = (TextView) findViewById(R.id.article_author_name);
        mUserIcon = (ImageView) findViewById(R.id.article_author_icon);

        // bottom bar
        mWant = (TextView) findViewById(R.id.interactive_bottom_want);
        mUpvote = (TextView) findViewById(R.id.interactive_upvote_count);
        mComment = (TextView) findViewById(R.id.interactive_comment_count);
        mInteractive = (Button) findViewById(R.id.interactive);
        mName.setText(mNameStr);
        mUpvote.setText(String.valueOf(mUpvoteCount));
        mComment.setText(String.valueOf(mCommentCount));

        // bottom dialog
        mRealName = (EditText) findViewById(R.id.interactive_name);
        mPhone = (EditText) findViewById(R.id.interactive_phone);
        mWeChat = (EditText) findViewById(R.id.interactive_we_chat);
        mEmail = (EditText) findViewById(R.id.interactive_email);
        mResume = (TextView) findViewById(R.id.interactive_resume);
        mReply = (EditText) findViewById(R.id.et_interactive_reply);

        findViewById(R.id.interactive_bottom_publish).setOnClickListener(this);
        findViewById(R.id.interactive_bottom_upvote).setOnClickListener(this);
        findViewById(R.id.interactive_bottom_comment).setOnClickListener(this);
        findViewById(R.id.author_info_container).setOnClickListener(this);

        // set user icon.
        if (mIconUrl != null && !mIconUrl.equals("")) {
            glide.load(mIconUrl).centerCrop().into(mUserIcon);
        } else {
            mUserIcon.setImageResource(R.mipmap.user_default_icon);
        }
    }

    public static void synCookies(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.setCookie(url, HttpUtils.cookieHeader());//cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();
    }

    private void showContent(){
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.web_progress);
        WebView webView = (WebView) findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUserAgent(settings.getUserAgentString() + "app/Memo");
        LogUtil.d(settings.getUserAgentString());
        // 设置加载进来的页面自适应手机屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
//        settings.setJavaScriptEnabled(true);

//        settings.setSupportZoom(false);
//        settings.setBuiltInZoomControls(false);
//        settings.setDisplayZoomControls(false);
        synCookies(mContext, mContentUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if(url.startsWith(RouterHelper.getApiUri().toString())){
//                    if(RouterHelper.confirmRequest(uri, "follow")){
//                        UserStatus.getCurrentUser().follow(mContext, mUserId, true);
//                    }
                    return false;
                } else if (url.startsWith(RouterHelper.appScheme())) {
                    if (RouterHelper.confirmIntent(uri, "content")) {
                        RouterHelper.openPageWithUri(mContext, uri, ArticlePage.class);
                    } else if (RouterHelper.confirmIntent(uri, "author")) {
                        RouterHelper.openPageWithUri(mContext, uri, AuthorPage.class);
                    } else if(RouterHelper.confirmIntent(uri, "publish")){
                        RouterHelper.openPageWithUri(mContext, uri, PublishActivity.class);
                    }
                    return true;
                }
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView webView, int i) {
                if(i == 100){
                    progressBar.setVisibility(View.GONE);
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(i);
                }
            }
        });
        webView.loadUrl(mContentUrl);

        switch (mType) {
            case ARTICLE:
                mWant.setText(getString(R.string.interactive_want_article));
                mInteractive.setText(getString(R.string.interactive_comment));
                // open the bottom sheet dialog.
                mInteractive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBSDialog(R.layout.bottom_dialog_reply);
                    }
                });
                break;
            case ALBUM:
                mWant.setText(getString(R.string.interactive_want_album));
                mInteractive.setText(getString(R.string.interactive_comment));
                mInteractive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBSDialog(R.layout.bottom_dialog_reply);
                    }
                });
                break;
            case ACTIVITY:
                mWant.setText(getString(R.string.interactive_want_activity));
                mInteractive.setText(getString(R.string.interactive_enroll));
                mInteractive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBSDialog(R.layout.bottom_dialog_info);
                    }
                });
                break;
            case VOTE:
                mWant.setText(getString(R.string.interactive_want_vote));
                mInteractive.setText(getString(R.string.interactive_vote));
                break;
            case RECRUIT:
                mWant.setText(getString(R.string.interactive_want_recruit));
                mInteractive.setText(getString(R.string.interactive_resume));
                break;
            case QA:
                mWant.setText(getString(R.string.interactive_want_answer));
                mInteractive.setText(getString(R.string.interactive_answer));
                mInteractive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBSDialog(R.layout.bottom_dialog_reply);
                    }
                });
                break;
            case RESERVE:
                mWant.setText(getString(R.string.interactive_want_reserve));
                mInteractive.setText(getString(R.string.interactive_reserve));
                break;
            case SALE:
                mWant.setText(getString(R.string.interactive_want_sale));
                mInteractive.setText(getString(R.string.interactive_buy));
                break;
        }
    }

    /**
     * create a bottom sheet dialog.
     */
    private void showBSDialog(@LayoutRes int layout) {
        dialog = new BottomSheetDialog(this);
        final View view = LayoutInflater.from(this).inflate(layout, null);
        dialog.setContentView(view);
        View parent = (View) view.getParent();
        behavior = BottomSheetBehavior.from(parent);
        behavior.setPeekHeight(DensityUtils.dp2px(this, 512));

        if (layout == R.layout.bottom_dialog_info) {
            parent.findViewById(R.id.interactive_info_submit).setOnClickListener(this);
            TextView action = (TextView) parent.findViewById(R.id.interactive_type);
            action.setText(getString(R.string.interactive_enroll));
        } else if (layout == R.layout.bottom_dialog_reply) {
            Button action = (Button) parent.findViewById(R.id.interactive_reply_submit);
            action.setOnClickListener(this);
            if (mType == PublishType.QA) {
                action.setText(getString(R.string.btn_dialog_answer));
            } else if (mType == PublishType.ARTICLE || mType == PublishType.ALBUM) {
                // set the submit button text.
                action.setText(getString(R.string.btn_dialog_submit));
            }
        }

        dialog.show();
    }

    public void onUploadResume(View view){
        // Todo:upload resume.
    }

    public void onMoreOption(View view) {

    }

    public void onCloseDialog(View view) {
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void onBack(View view) {
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // get the comment layout position.
//        commentHeight = findViewById(R.id.article_content).getTop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.interactive_bottom_publish:
                Intent intent = new Intent(this, PublishActivity.class);
                intent.putExtra("type", mType);
                startActivity(intent);
                break;
            case R.id.interactive_bottom_upvote:

                break;
            case R.id.interactive_bottom_comment:
//                mScrollView.smoothScrollTo(0, commentHeight);
                break;
            // top user icon.
            case R.id.author_info_container:
                if(mUserId != null && !mUserId.equals("")){
                    if(UserStatus.isLogin(this)){
                        if(!mUserId.equals(UserStatus.getCurrentUser().getUserId())){
                            Intent intent1 = new Intent(this, AuthorPage.class);
                            intent1.putExtra("userId", mUserId);
                            startActivity(intent1);
                        } else {
                            // Todo : open my own info page.
                        }
                    } else {
                        Intent intent1 = new Intent(this, AuthorPage.class);
                        intent1.putExtra("userId", mUserId);
                        startActivity(intent1);
                    }
                }
                break;
            case R.id.interactive_reply_submit://submit reply
                break;
            case R.id.interactive_info_submit://submit info
                break;
        }
    }
}
