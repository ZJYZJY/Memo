package com.donutcn.memo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.donutcn.memo.R;
import com.donutcn.memo.type.PublishType;
import com.donutcn.memo.utils.WindowUtils;

public class CompletingPage extends AppCompatActivity {

    private PublishType mContentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        mContentType = (PublishType) getIntent().getSerializableExtra("type");

        switch (mContentType){
            case ACTIVITY:
                setContentView(R.layout.completing_page_activity);
                break;
            case VOTE:
                break;
            case RECRUIT:
                setContentView(R.layout.completing_page_recruit);
                break;
            case RESERVE:
                setContentView(R.layout.completing_page_reserve);
                break;
            case SALE:
                setContentView(R.layout.completing_page_sale);
                break;
        }
    }

    public void onFinish(View view){
        Intent intent = new Intent(this, SocialShareActivity.class);
        startActivity(intent);
    }

    public void onBack(View view){
        finish();
    }
}
