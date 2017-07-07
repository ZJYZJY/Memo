package com.donutcn.memo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.donutcn.memo.R;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.widgetlib.SwitchView;

public class SocialShareActivity extends AppCompatActivity {

    private SwitchView mSwith;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_share);
        WindowUtils.setToolBarTitle(this, R.string.title_activity_share);
        WindowUtils.setToolBarButton(this, R.string.btn_share_homepage);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary);

        mSwith = (SwitchView) findViewById(R.id.social_content_private);
    }

    public void onBack(View view){
        finish();
    }
}
