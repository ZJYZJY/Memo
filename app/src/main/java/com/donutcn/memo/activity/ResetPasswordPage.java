package com.donutcn.memo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.helper.LoginHelper;
import com.donutcn.memo.utils.CountDownTimerUtils;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.donutcn.memo.utils.WindowUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordPage extends AppCompatActivity implements View.OnClickListener {

    private EditText mPhone, mMsgCode, mPassword;
    private TextView mGetAuthCode;
    private Button mReset;

    private CountDownTimerUtils authCodeTimer;
    private final int ACTION_MODIFY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_page);
        WindowUtils.setToolBarTitle(this, R.string.title_activity_reset_password);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        initView();

    }

    public void initView(){
        mPhone = (EditText) findViewById(R.id.reset_phone);
        mMsgCode = (EditText) findViewById(R.id.reset_message_code);
        mPassword = (EditText) findViewById(R.id.reset_password);
        mGetAuthCode = (TextView) findViewById(R.id.tv_get_msg_code);
        mReset = (Button) findViewById(R.id.reset_btn);
        authCodeTimer = new CountDownTimerUtils(mMsgCode, 60*1000, 1000);

        mGetAuthCode.setOnClickListener(this);
        mReset.setOnClickListener(this);
    }

    public void requestForAuthCode(){
        String phoneNumber = mPhone.getText().toString();
        if(TextUtils.isEmpty(phoneNumber)){
            ToastUtil.show(this, "请输入手机号码");
            return;
        } else if(!isValidPhoneNumber(phoneNumber)){
            ToastUtil.show(this, "请检查手机号是否正确");
            return;
        }
        mGetAuthCode.setClickable(false);
        mGetAuthCode.setBackgroundResource(R.drawable.disabled_gray_btn_bg);
        HttpUtils.getVerifiedCode(phoneNumber, "edituser").enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body() != null){
                    if(response.body().isOk()){
                        authCodeTimer.start();
                        ToastUtil.show(ResetPasswordPage.this, "验证码发送成功");
                    } else {
                        mGetAuthCode.setClickable(true);
                        mGetAuthCode.setBackgroundResource(R.drawable.selector_radius_blue_btn);
                        ToastUtil.show(ResetPasswordPage.this, response.body().getMessage());
                    }
                } else {
                    ToastUtil.show(ResetPasswordPage.this, "验证码发送失败 ，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                mGetAuthCode.setClickable(true);
                mGetAuthCode.setBackgroundResource(R.drawable.selector_radius_blue_btn);
                ToastUtil.show(ResetPasswordPage.this, "获取验证码连接失败");
                t.printStackTrace();
            }
        });
    }

    public void attemptToRegister(){
        final String phoneNumber = mPhone.getText().toString();
        String authCode = mMsgCode.getText().toString();
        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtil.show(this, "手机号不能为空");
            return;
        } else if (TextUtils.isEmpty(authCode)) {
            ToastUtil.show(this, "验证码不能为空");
            return;
        } else if (TextUtils.isEmpty(password)) {
            ToastUtil.show(this, "密码不能为空");
            return;
        } else if(!isValidPassword(password)){
            ToastUtil.show(this, "密码不能少于6位");
            return;
        }
        HttpUtils.modifyUser(phoneNumber, authCode, password, ACTION_MODIFY).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body() != null){
                    if(response.body().isOk()){
                        ToastUtil.show(ResetPasswordPage.this, "重置密码成功");
                        LoginHelper.login(getApplicationContext(),
                                UserStatus.PHONE_LOGIN, response.body().getData());
                        Intent intent = new Intent(ResetPasswordPage.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else {
                        ToastUtil.show(ResetPasswordPage.this, "重置密码失败，" + response.body().getMessage());
                    }
                } else {
                    ToastUtil.show(ResetPasswordPage.this, "登录失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                ToastUtil.show(ResetPasswordPage.this, "重置连接失败");
                t.printStackTrace();
            }
        });
    }

    private boolean isValidPhoneNumber(String number){
        return number.length() == 11;
    }

    private boolean isValidPassword(String password){
        return password.length() >= 6;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_get_msg_code:
                requestForAuthCode();
                break;
            case R.id.reset_btn:
                attemptToRegister();
                break;
        }
    }

    public void onBack(View view){
        finish();
    }
}
