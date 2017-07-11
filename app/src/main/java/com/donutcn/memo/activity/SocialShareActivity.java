package com.donutcn.memo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.donutcn.memo.R;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.widgetlib.SwitchView;

public class SocialShareActivity extends AppCompatActivity implements View.OnClickListener {

    private SwitchView mSwith;
    private Button mHomePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_share);
        WindowUtils.setToolBarTitle(this, R.string.title_activity_share);
        WindowUtils.setToolBarButton(this, R.string.btn_share_homepage);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        initView();
    }

    public void initView(){
        mSwith = (SwitchView) findViewById(R.id.social_content_private);
        mHomePage = (Button) findViewById(R.id.toolbar_with_btn);

        mHomePage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_with_btn:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }

    public void onBack(View view){
        finish();
    }
}
