package com.donutcn.memo.view.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.donutcn.memo.R;
import com.donutcn.memo.helper.ShareHelper;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.view.ShareImageView;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class ShareImageActivity extends AppCompatActivity {

    private ShareImageView mShareImageView;
    private Bitmap mShareImage;
    private ProgressDialog mDialog;

    private boolean hasSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_share_view);

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        String url = getIntent().getStringExtra("url");
        mShareImageView = (ShareImageView) findViewById(R.id.share_container);
        mShareImageView.initView();
        mShareImageView.setContent("<h2>" + title + "</h2>" + content);
        mShareImageView.setQRCode(url);

        mShareImageView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress == 100){
                    findViewById(R.id.share_type).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * save bitmap to sdcard
     * @param b bitmap
     * @return file path
     */
    public String saveImage(Bitmap b) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在生成分享图片...");
        dialog.show();
        mShareImage = mShareImageView.getBitmap();
        dialog.dismiss();
        if(hasSaved){
            return null;
        }
        hasSaved = true;
        return MediaStore.Images.Media.insertImage(this.getContentResolver(), b, null, null);
    }

    public void onShare(View view){
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("正在准备分享...");
        saveImage(mShareImage);

        new ShareHelper(this).openShareBoard(mShareImage, new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                mDialog.show();
            }

            @Override
            public void onResult(SHARE_MEDIA platform) {
                ToastUtil.show(ShareImageActivity.this, platform + " 分享成功啦");
                if(!mShareImage.isRecycled()) {
                    mShareImage.recycle();
                }
                mDialog.dismiss();
                finish();
            }

            @Override
            public void onError(SHARE_MEDIA platform, Throwable t) {
                ToastUtil.show(ShareImageActivity.this, platform + " 分享失败啦");
                if(!mShareImage.isRecycled()) {
                    mShareImage.recycle();
                }
                mDialog.dismiss();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                ToastUtil.show(ShareImageActivity.this, platform + " 分享取消了");
                if(!mShareImage.isRecycled()) {
                    mShareImage.recycle();
                }
                mDialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mShareImage != null && !mShareImage.isRecycled()){
            mShareImage.recycle();
        }
    }
}
