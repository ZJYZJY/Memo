package com.donutcn.memo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.donutcn.memo.R;
import com.donutcn.memo.entity.User;
import com.donutcn.memo.event.LoginStateEvent;
import com.donutcn.memo.helper.LoginHelper;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.widgetlib.widget.CircleImageView;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PersonalCenterActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mAuth, mNotify, mFeedback, mAbout, mSignout;
    private TextView mNickname, mVersionNum;
    private ImageView mUserIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        WindowUtils.setStatusBarColor(this, android.R.color.transparent, false);

        initView();
    }

    public void initView(){
        mUserIcon = (ImageView) findViewById(R.id.personal_center_user_icon);
        mNickname = (TextView) findViewById(R.id.personal_center_user_name);
        mAuth = (RelativeLayout) findViewById(R.id.authentication);
        mNotify = (RelativeLayout) findViewById(R.id.notification_settings);
        mFeedback = (RelativeLayout) findViewById(R.id.feedback);
        mAbout = (RelativeLayout) findViewById(R.id.about);
        mSignout = (RelativeLayout) findViewById(R.id.log_out);
        mVersionNum = (TextView) findViewById(R.id.version_number);

        mAuth.setOnClickListener(this);
        mNotify.setOnClickListener(this);
        mFeedback.setOnClickListener(this);
        mAbout.setOnClickListener(this);
        mSignout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.authentication:
                startActivity(new Intent(this, TipOffActivity.class));
                break;
            case R.id.notification_settings:
                break;
            case R.id.feedback:
                break;
            case R.id.about:
                Beta.checkUpgrade();
                break;
            case R.id.log_out:
                LoginHelper.logout(getApplicationContext());
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onLoginStateEvent(LoginStateEvent event){
        if(event.isLogin()){
            User user = event.getUser();
            String iconUrl = user.getIconUrl();
            if(iconUrl != null && !iconUrl.equals("")){
                Glide.with(this).load(iconUrl).centerCrop().into(mUserIcon);
            }
            mNickname.setText(user.getName() == null ? user.getPhone() : user.getName());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onBack(View view){
        finish();
    }
}
