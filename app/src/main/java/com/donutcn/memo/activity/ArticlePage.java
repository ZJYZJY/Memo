package com.donutcn.memo.activity;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.donutcn.memo.R;
import com.donutcn.memo.X5testActivity;
import com.donutcn.memo.entity.ContentResponse;
import com.donutcn.memo.type.PublishType;
import com.donutcn.memo.utils.DensityUtils;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.WindowUtils;
import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticlePage extends AppCompatActivity implements View.OnClickListener {

    private BottomSheetBehavior behavior;
    private BottomSheetDialog dialog;
    private Button mInteractive;
    private TextView mTitle, mDate, mAuthor, mReadCount, mContent, mWant;
    private ScrollView mScrollView;

    private PublishType mType = PublishType.ARTICLE;

    private String mTitleStr, mAuthorStr, mDateStr, mReadCountStr, mContentStr;
    private String mContentId;
    // comment height
    private int commentHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_page);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        initView();
        mContentId = getIntent().getStringExtra("contentId");
        if(mContent != null){
            HttpUtils.getContent(mContentId).enqueue(new Callback<ContentResponse>() {
                @Override
                public void onResponse(Call<ContentResponse> call, Response<ContentResponse> response) {
                    ContentResponse res = response.body();
                    if(res.isOk()){
                        mTitleStr = res.getTitle();
                        mAuthorStr = res.getAuthor();
                        mDateStr = res.getDate();
                        mReadCountStr = res.getReadCount();
                        mContentStr = res.getContentStr();
                        mType = res.getType();
                        showContent();
                    }else {
                        Toast.makeText(ArticlePage.this, "该文章不存在", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ContentResponse> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(ArticlePage.this, "连接失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showContent(){
        mTitle.setText(mTitleStr);
        mDate.setText(mDateStr);
        mAuthor.setText(mAuthorStr);
        mReadCount.setText(getString(R.string.placeholder_article_read_count, mReadCountStr));
        RichText.fromHtml(mContentStr)
                .bind(this)
                .autoFix(true)
                .cache(CacheType.ALL)
                .scaleType(ImageHolder.ScaleType.FIT_AUTO)
                .into(mContent);
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

    public void initView() {
        mTitle = (TextView) findViewById(R.id.article_title);
        mDate = (TextView) findViewById(R.id.article_date);
        mAuthor = (TextView) findViewById(R.id.article_author);
        mReadCount = (TextView) findViewById(R.id.article_read_count);
        mContent = (TextView) findViewById(R.id.article_content);
        mScrollView = (ScrollView) findViewById(R.id.article_scroll);

        mInteractive = (Button) findViewById(R.id.interactive);
        mWant = (TextView) findViewById(R.id.interactive_bottom_want);

        findViewById(R.id.interactive_bottom_publish).setOnClickListener(this);
        findViewById(R.id.interactive_bottom_upvote).setOnClickListener(this);
        findViewById(R.id.interactive_bottom_comment).setOnClickListener(this);
    }

    public void onMore(View view) {

    }

    public void onCloseDialog(View view) {
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void onBack(View view) {
        finish();
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
            TextView action = (TextView) parent.findViewById(R.id.interactive_type);
            action.setText(getString(R.string.interactive_enroll));
        } else if (layout == R.layout.bottom_dialog_reply) {
            TextView action = (TextView) parent.findViewById(R.id.interactive_reply_submit);
            if (mType == PublishType.QA) {
                action.setText(getString(R.string.btn_dialog_answer));
            } else if (mType == PublishType.ARTICLE || mType == PublishType.ALBUM) {
                // set the submit button text.
                action.setText(getString(R.string.btn_dialog_submit));
            }
        }

        dialog.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // get the comment layout position.
        commentHeight = findViewById(R.id.article_content).getTop();
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
                startActivity(new Intent(this, X5testActivity.class));
                break;
            case R.id.interactive_bottom_comment:
                mScrollView.smoothScrollTo(0, commentHeight);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RichText.clear(this);
    }
}
