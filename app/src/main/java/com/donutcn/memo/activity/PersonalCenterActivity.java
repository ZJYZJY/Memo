package com.donutcn.memo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.donutcn.memo.R;
import com.donutcn.memo.entity.User;
import com.donutcn.memo.event.LoginStateEvent;
import com.donutcn.memo.helper.LoginHelper;
import com.donutcn.memo.utils.WindowUtils;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PersonalCenterActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mAuth, mNotify, mFeedback, mAbout, mLogout;
    private TextView mNickname, mSignature, mVersionNum;
    private ImageView mUserIcon;

    private RequestManager glide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        WindowUtils.setStatusBarColor(this, android.R.color.transparent, false);

        initView();
    }

    public void initView(){
        glide = Glide.with(this);
        mUserIcon = (ImageView) findViewById(R.id.personal_center_user_icon);
        mNickname = (TextView) findViewById(R.id.personal_center_user_name);
        mSignature = (TextView) findViewById(R.id.personal_center_user_signature);
        mAuth = (RelativeLayout) findViewById(R.id.authentication);
        mNotify = (RelativeLayout) findViewById(R.id.notification_settings);
        mFeedback = (RelativeLayout) findViewById(R.id.feedback);
        mAbout = (RelativeLayout) findViewById(R.id.about);
        mLogout = (RelativeLayout) findViewById(R.id.log_out);
        mVersionNum = (TextView) findViewById(R.id.version_number);

        mUserIcon.setOnClickListener(this);
        mAuth.setOnClickListener(this);
        mNotify.setOnClickListener(this);
        mFeedback.setOnClickListener(this);
        mAbout.setOnClickListener(this);
        mLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.personal_center_user_icon:
                startActivity(new Intent(this, EditInfoPage.class));
                break;
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
        if(event.isLogin() || event.isSync()){
            User user = event.getUser();
            String iconUrl = user.getIconUrl();
            if(iconUrl != null && !iconUrl.equals("")){
                glide.load(iconUrl).centerCrop().into(mUserIcon);
            }
            mNickname.setText(user.getName() == null ? user.getPhone() : user.getName());
            String signature = user.getSignature();
            if(signature != null && !signature.equals("")){
                mSignature.setText(signature);
            } else {
                mSignature.setText("还没有编辑个人签名");
            }
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
