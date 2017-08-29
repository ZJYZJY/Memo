package com.donutcn.memo.activity;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.donutcn.memo.R;
import com.donutcn.memo.constant.FieldConstant;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.fragment.SplashFragment;
import com.donutcn.memo.helper.LoginHelper;
import com.donutcn.memo.utils.CountDownTimerUtils;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.StringUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.donutcn.memo.utils.WindowUtils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioButton mToLogin, mToRegister;
    private RadioGroup mRadioGroup;
    private View mWechat, mWithoutLogin;
    private TextView mMsgCode, mForget;
    private Button mLogin;
    private EditText mPhoneNum, mPassword;
    private EditText mRegPhone, mRegPassword, mRegCode;
    private RelativeLayout mLoginContainer;
    private SplashFragment splashFragment;
    private ProgressDialog mDialog;

    private CountDownTimerUtils authCodeTimer;
    private Handler mSplashHandler = new Handler();

    private boolean loginState;
    private boolean completeInfo = false;
    private final int ACTION_REGISTER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        WindowUtils.setStatusBarColor(this, R.color.background_blank, true);
        if(getIntent().getBooleanExtra("showSplash", true)){
            // show splash fragment.
            showSplashFragment();
        }
        initView();
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("正在登录中...");
    }

    public void initView() {
        mToLogin = (RadioButton) findViewById(R.id.switch_to_login);
        mToRegister = (RadioButton) findViewById(R.id.switch_to_register);
        mRadioGroup = (RadioGroup) findViewById(R.id.login_or_register);
        mPhoneNum = (EditText) findViewById(R.id.login_phone);
        mPassword = (EditText) findViewById(R.id.login_password);
        mRegPhone = (EditText) findViewById(R.id.reg_phone);
        mRegCode = (EditText) findViewById(R.id.reg_message_code);
        mRegPassword = (EditText) findViewById(R.id.reg_password);
        mLogin = (Button) findViewById(R.id.login_btn);
        mWechat = findViewById(R.id.login_with_wechat);
        mWithoutLogin = findViewById(R.id.enter_without_login);
        mMsgCode = (TextView) findViewById(R.id.tv_get_msg_code);
        mForget = (TextView) findViewById(R.id.forget_password);
        authCodeTimer = new CountDownTimerUtils(mMsgCode, 60*1000, 1000);

        mToLogin.setOnClickListener(this);
        mToRegister.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mWechat.setOnClickListener(this);
        mWithoutLogin.setOnClickListener(this);
        mMsgCode.setOnClickListener(this);
        mForget.setOnClickListener(this);
    }

    private Runnable showMainPage = new Runnable(){
        @Override
        public void run() {
            if(loginState){
                HttpUtils.autoLogin().enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                        if (response.body() != null) {
                            LogUtil.d("sync", response.body().toString());
                            if(response.body().isOk()){
                                LinkedHashMap data = response.body().getData();
                                LoginHelper.autoLogin(LoginActivity.this, data);
                                // if login with phone number.
                                if(LoginHelper.loginType(LoginActivity.this) == UserStatus.PHONE_LOGIN){
                                    if("".equals(data.get(FieldConstant.USER_ICON_URL))
                                            || "".equals(data.get(FieldConstant.USER_NICKNAME))){
                                        completeInfo = true;
                                    }
                                }
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("completeInfo", completeInfo);
                                startActivity(intent);
                            } else if (response.body().unAuthorized()) {
                                // check if the cookie is out of date.
                                LogUtil.e("unAuthorized", response.body().toString());
                                ToastUtil.show(LoginActivity.this, "登录授权过期，请重新登录");
                                LoginHelper.logout(LoginActivity.this);
                            } else {
                                ToastUtil.show(LoginActivity.this, response.body().getMessage());
                                removeSplashFragment();
                            }
                        } else {
                            ToastUtil.show(LoginActivity.this, "连接失败，服务器未知错误");
                            removeSplashFragment();
                        }
                    }

                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        t.printStackTrace();
                        removeSplashFragment();
                        ToastUtil.show(LoginActivity.this, "连接失败，请检查你的网络连接");
                    }
                });
            }else {
                removeSplashFragment();
            }
        }
    };

    public void showSplashFragment() {
        mLoginContainer = (RelativeLayout) findViewById(R.id.login_container);
        findViewById(R.id.splash_container).setVisibility(View.VISIBLE);
        mLoginContainer.setVisibility(View.GONE);
        splashFragment = new SplashFragment();
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.splash_container, splashFragment);
        transaction.commit();
        // set flag full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // check the login state.
        loginState = UserStatus.isLogin(getApplicationContext());
        // delay 3s to remove the splash fragment.
        mSplashHandler.postDelayed(showMainPage, 3000);
    }

    private void removeSplashFragment(){
        if (splashFragment == null)
            return;
        // remove splash fragment.
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(splashFragment);
        transaction.commit();
        splashFragment = null;
        mLoginContainer.setVisibility(View.VISIBLE);
        findViewById(R.id.splash_container).setVisibility(View.GONE);
        // clear flag full screen.
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void attemptToLogin() {
        final String username = mPhoneNum.getText().toString();
        final String password = mPassword.getText().toString();
        if(TextUtils.isEmpty(username)){
            ToastUtil.show(this, "手机号不能为空");
            return;
        }else if(TextUtils.isEmpty(password)){
            ToastUtil.show(this, "密码不能为空");
            return;
        }
        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);
        mDialog.show();
        HttpUtils.login(UserStatus.PHONE_LOGIN, data).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                mDialog.cancel();
                if (response.body() != null) {
                    if (response.body().isOk()) {
                        ToastUtil.show(LoginActivity.this, "登录成功");
                        Map<String, String> data = response.body().getData();
                        data.put("token", password);
                        LoginHelper.login(getApplicationContext(),
                                UserStatus.PHONE_LOGIN, data);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        ToastUtil.show(LoginActivity.this, "登录失败，" + response.body().getMessage());
                    }
                } else {
                    ToastUtil.show(LoginActivity.this, "登录失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                mDialog.cancel();
                ToastUtil.show(LoginActivity.this, "登录连接失败");
                t.printStackTrace();
            }
        });
    }

    public void attemptToRegister(){
        final String phoneNumber = mRegPhone.getText().toString();
        String authCode = mRegCode.getText().toString();
        final String password = mRegPassword.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtil.show(this, "手机号不能为空");
            return;
        } else if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtil.show(this, "验证码不能为空");
            return;
        } else if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtil.show(this, "密码不能为空");
            return;
        }
        mDialog.show();
        HttpUtils.modifyUser(phoneNumber, authCode, password, ACTION_REGISTER).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                mDialog.cancel();
                if(response.body() != null){
                    if(response.body().isOk()){
                        ToastUtil.show(LoginActivity.this, "注册成功");
                        Map<String, String> data = response.body().getData();
                        data.put("token", password);
                        LoginHelper.login(getApplicationContext(),
                                UserStatus.PHONE_LOGIN, data);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else {
                        ToastUtil.show(LoginActivity.this, "注册失败，" + response.body().getMessage());
                    }
                } else {
                    ToastUtil.show(LoginActivity.this, "注册失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                mDialog.cancel();
                ToastUtil.show(LoginActivity.this, "注册连接失败");
                t.printStackTrace();
            }
        });
    }

    public void requestForAuthCode(){
        String phoneNumber = mRegPhone.getText().toString();
        if(TextUtils.isEmpty(phoneNumber)){
            ToastUtil.show(this, "请输入手机号码");
            return;
        }
        mMsgCode.setClickable(false);
        mMsgCode.setBackgroundResource(R.drawable.disabled_gray_btn_bg);
        HttpUtils.getVerifiedCode(phoneNumber, "register").enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body() != null){
                    if(response.body().isOk()){
                        authCodeTimer.start();
                        ToastUtil.show(LoginActivity.this, "验证码发送成功");
                    } else {
                        mMsgCode.setClickable(true);
                        mMsgCode.setBackgroundResource(R.drawable.selector_radius_blue_btn);
                        ToastUtil.show(LoginActivity.this, "验证码发送失败，" + response.body().getMessage());
                    }
                } else {
                    ToastUtil.show(LoginActivity.this, "登录失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                mMsgCode.setClickable(true);
                mMsgCode.setBackgroundResource(R.drawable.selector_radius_blue_btn);
                ToastUtil.show(LoginActivity.this, "验证码发送连接失败");
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_to_login:
                mToLogin.setTextColor(getResources().getColor(R.color.colorAccent));
                mToRegister.setTextColor(getResources().getColor(R.color.textPrimaryDark));
                findViewById(R.id.login_et).setVisibility(View.VISIBLE);
                findViewById(R.id.register_et).setVisibility(View.GONE);
                mLogin.setText(getString(R.string.btn_login));
                break;
            case R.id.switch_to_register:
                mToRegister.setTextColor(getResources().getColor(R.color.colorAccent));
                mToLogin.setTextColor(getResources().getColor(R.color.textPrimaryDark));
                findViewById(R.id.register_et).setVisibility(View.VISIBLE);
                findViewById(R.id.login_et).setVisibility(View.GONE);
                mLogin.setText(getString(R.string.btn_register));
                break;
            case R.id.login_btn:
                WindowUtils.toggleKeyboard(this, false);
                if(mToLogin.isChecked())
                    attemptToLogin();
                else
                    attemptToRegister();
                break;
            case R.id.login_with_wechat:
                mDialog.show();
                UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, umAuthListener);
                break;
            case R.id.enter_without_login:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("showSplash", false);
                intent.putExtra("unlogin", true);
                startActivity(intent);
                break;
            case R.id.tv_get_msg_code:
                requestForAuthCode();
                break;
            case R.id.forget_password:
                startActivity(new Intent(this, ResetPasswordPage.class));
                break;
        }
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //授权开始的回调
        }
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            HttpUtils.login(UserStatus.WECHAT_LOGIN, data).enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    mDialog.cancel();
                    if(response.body() != null && response.body().isOk()){
                        LogUtil.d("wechat_login", response.body().toString());
                        Map<String, String> data = response.body().getData();
                        data.put("token", StringUtil.getMD5(data.get("openid")));
                        LoginHelper.login(getApplicationContext(),
                                UserStatus.WECHAT_LOGIN, data);
                        ToastUtil.show(LoginActivity.this, "登录成功");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        ToastUtil.show(LoginActivity.this, "登录失败");
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    mDialog.cancel();
                    t.printStackTrace();
                    ToastUtil.show(LoginActivity.this, "连接失败，请检查你的网络");
                }
            });
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            mDialog.cancel();
            t.printStackTrace();
            Toast.makeText( getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            mDialog.cancel();
            Toast.makeText( getApplicationContext(), "授权取消", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

    public void onSkipSplash(View view) {
        mSplashHandler.removeCallbacks(showMainPage);
        getWindow().getDecorView().post(showMainPage);
    }

    @Override
    protected void onDestroy() {
        mSplashHandler.removeCallbacks(showMainPage);
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if("ExitApp".equals(intent.getAction())){
            finish();
        }
        mPhoneNum.setText("");
        mPassword.setText("");
        mRegPhone.setText("");
        mRegPassword.setText("");
        mRegCode.setText("");
        if(getIntent().getBooleanExtra("showSplash", false)){
            // show splash fragment.
            showSplashFragment();
        }else {
            removeSplashFragment();
        }
    }
}
