package com.donutcn.memo.view.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.donutcn.memo.R;
import com.donutcn.memo.helper.ShareHelper;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.StringUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.memo.view.ShareImageView;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.os.Environment.getExternalStorageDirectory;

public class ShareImageActivity extends AppCompatActivity {

    private ShareImageView mShareImageView;
    private Bitmap mShareImage;
    private ProgressDialog mDialog;
    private FileOutputStream mFileOutputStream;

    private static final String SHARE_PATH = getExternalStorageDirectory() + "/com.donutcn.memo/shareImage/";
    private String title, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_share_view);
        WindowUtils.setStatusBarColor(this, R.color.background_blank, true);

        mShareImageView = (ShareImageView) findViewById(R.id.share_container);
        mShareImageView.init();
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        String url = getIntent().getStringExtra("url");
        LogUtil.d(content);
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

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                try {
                    mFileOutputStream.flush();
                    mFileOutputStream.close();
                    String picName = msg.getData().getString("picName", "");
                    mDialog.dismiss();
                    ToastUtil.show(ShareImageActivity.this, "图片已保存到" + SHARE_PATH + picName);
                } catch (IOException e) {
                    e.printStackTrace();
                    mDialog.dismiss();
                    ToastUtil.show(ShareImageActivity.this, "图片保存失败");
                }
            }
        }
    };

    public void onSave(View view){
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("正在保存分享图片...");
        mDialog.show();
        final String picName = StringUtil.getFileNameWithDate("IMG_") + ".png";
        mShareImage = mShareImageView.getBitmap();
        File path = new File(SHARE_PATH);
        if(!path.exists()){
            path.mkdirs();
        }
        File file = new File(SHARE_PATH, picName);
        if (file.exists()) {
            file.delete();
        }
        try {
            mFileOutputStream = new FileOutputStream(file);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mShareImage.compress(Bitmap.CompressFormat.PNG, 90, mFileOutputStream);
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("picName", picName);
                    msg.what = 1;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            mDialog.dismiss();
            ToastUtil.show(this, "图片保存失败");
        }
    }

    public void onShare(View view){
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("正在准备分享...");
        mShareImage = mShareImageView.getBitmap();

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
        mShareImageView.destroy();
        if(mShareImage != null && !mShareImage.isRecycled()){
            mShareImage.recycle();
        }
    }
}
