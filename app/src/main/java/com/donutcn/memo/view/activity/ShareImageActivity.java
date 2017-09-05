package com.donutcn.memo.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.view.ShareImageView;

public class ShareImageActivity extends AppCompatActivity {

    private ShareImageView mShareImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_share_view);

        String content = getIntent().getStringExtra("content");
        String url = getIntent().getStringExtra("url");
        mShareImageView = (ShareImageView) findViewById(R.id.share_container);
        mShareImageView.initView();
        mShareImageView.setContent(content);
        mShareImageView.setQRCode(url);
    }
}
