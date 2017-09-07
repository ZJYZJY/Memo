package com.donutcn.memo.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.donutcn.memo.R;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.entity.User;
import com.donutcn.memo.event.LoginStateEvent;
import com.donutcn.memo.helper.LoginHelper;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.SpfsUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.widgetlib.widget.SwitchView;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalCenterActivity extends AppCompatActivity implements View.OnClickListener, SwitchView.OnStateChangedListener {

    private RelativeLayout mFeedback, mAbout, mLogout;
    private SwitchView mPhotoMark, mNotify;
    private TextView mNickname, mSignature, mVersionNum;
    private ImageView mUserIcon, mSetting;

    private RequestManager glide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        WindowUtils.setStatusBarColor(this, android.R.color.transparent, false);

        initView();
        EventBus.getDefault().register(this);
    }

    public void initView(){
        glide = Glide.with(this);
        mSetting = (ImageView) findViewById(R.id.setting);
        mUserIcon = (ImageView) findViewById(R.id.personal_center_user_icon);
        mNickname = (TextView) findViewById(R.id.personal_center_user_name);
        mSignature = (TextView) findViewById(R.id.personal_center_user_signature);

        mPhotoMark = (SwitchView) findViewById(R.id.photo_switch);
        mNotify = (SwitchView) findViewById(R.id.notification_switch);
        mFeedback = (RelativeLayout) findViewById(R.id.feedback);
        mAbout = (RelativeLayout) findViewById(R.id.about);
        mLogout = (RelativeLayout) findViewById(R.id.log_out);
        mVersionNum = (TextView) findViewById(R.id.version_number);

        mSetting.setOnClickListener(this);
        mFeedback.setOnClickListener(this);
        mAbout.setOnClickListener(this);
        mLogout.setOnClickListener(this);
        findViewById(R.id.personal_center_edit_container).setOnClickListener(this);
        if(SpfsUtils.readBoolean(this, SpfsUtils.SETTING, "showWaterMark", false)){
            mPhotoMark.toggleSwitch(true);
        }
        mPhotoMark.setOnStateChangedListener(this);
        mNotify.setOnStateChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting:
            case R.id.personal_center_edit_container:
                startActivity(new Intent(this, EditInfoPage.class));
                break;
            case R.id.feedback:
                startActivity(new Intent(this, FeedBackActivity.class));
                break;
            case R.id.about:
                Beta.checkUpgrade();
                break;
            case R.id.log_out:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.dialog_tips_title))
                        .setMessage(getString(R.string.dialog_logout))
                        .setPositiveButton(getString(R.string.dialog_pos), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginHelper.logout(PersonalCenterActivity.this);
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_neg), null)
                        .show();
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
            mNotify.toggleSwitch(UserStatus.getCurrentUser().getNotification() == 1);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onBack(View view) {
        finish();
    }

    @Override
    public void toggleToOn(SwitchView view) {
        if (view == mPhotoMark) {
            setPhotoMark(view.isOpened(), view);
        } else if (view == mNotify) {
            setNotification(view.isOpened(), view);
        }
    }

    @Override
    public void toggleToOff(SwitchView view) {
        if (view == mPhotoMark) {
            setPhotoMark(view.isOpened(), view);
        } else if (view == mNotify) {
            setNotification(view.isOpened(), view);
        }
    }

    private void setPhotoMark(final boolean isMark, final SwitchView view) {
        if (isMark) {
            SpfsUtils.write(this, SpfsUtils.SETTING, "showWaterMark", false);
            view.toggleSwitch(false);
        } else {
            SpfsUtils.write(this, SpfsUtils.SETTING, "showWaterMark", true);
            view.toggleSwitch(true);
        }
    }

    private void setNotification(final boolean isNotify, final SwitchView view) {
        HttpUtils.enableNotification(isNotify ? 0 : 1).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body() != null){
                    if(response.body().isOk()){
                        if (isNotify) {
                            view.toggleSwitch(false);
                            ToastUtil.show(PersonalCenterActivity.this, "关闭通知成功");
                        } else {
                            view.toggleSwitch(true);
                            ToastUtil.show(PersonalCenterActivity.this, "启用通知成功");
                        }
                    } else {
                        view.toggleSwitch(isNotify);
                        ToastUtil.show(PersonalCenterActivity.this, response.body().getMessage() + "");
                    }
                } else {
                    view.toggleSwitch(isNotify);
                    ToastUtil.show(PersonalCenterActivity.this, "设置失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                view.toggleSwitch(isNotify);
                ToastUtil.show(PersonalCenterActivity.this, "设置连接失败");
            }
        });
    }
}
