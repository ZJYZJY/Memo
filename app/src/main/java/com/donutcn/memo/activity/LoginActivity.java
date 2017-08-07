package com.donutcn.memo.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.donutcn.memo.R;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.fragment.SplashFragment;
import com.donutcn.memo.utils.CountDownTimerUtils;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.SpfsUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.donutcn.memo.utils.WindowUtils;

import java.io.IOException;

import okhttp3.ResponseBody;
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

    private CountDownTimerUtils authCodeTimer;
    private Handler mSplashHandler = new Handler();

    private boolean loginState;
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
                String phoneNumber = SpfsUtils.readString(
                        getApplicationContext(), SpfsUtils.USER, "phoneNumber", "");
                UserStatus.login(getApplicationContext(), phoneNumber);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }else {
                Intent intent = getIntent();
                String scheme = intent.getScheme();
                Uri uri = intent.getData();
                System.out.println("scheme:"+scheme);
                if (uri != null) {
                    String host = uri.getHost();
                    String dataString = intent.getDataString();
                    String id = uri.getQueryParameter("d");
                    String path = uri.getPath();
                    String path1 = uri.getEncodedPath();
                    String queryString = uri.getQuery();
                    System.out.println("host:"+host);
                    System.out.println("dataString:"+dataString);
                    System.out.println("id:"+id);
                    System.out.println("path:"+path);
                    System.out.println("path1:"+path1);
                    System.out.println("queryString:"+queryString);
                }
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
        String password = mPassword.getText().toString();
        if(TextUtils.isEmpty(username)){
            ToastUtil.show(this, "手机号不能为空");
            return;
        }else if(TextUtils.isEmpty(password)){
            ToastUtil.show(this, "密码不能为空");
            return;
        }
        HttpUtils.login(username, password).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.body() != null && response.body().isOk()) {
                    ToastUtil.show(LoginActivity.this, "登录成功");
                    UserStatus.login(getApplicationContext(), username);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    ToastUtil.show(LoginActivity.this, "登录失败，" + response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                ToastUtil.show(LoginActivity.this, "登录连接失败");
                t.printStackTrace();
            }
        });
    }

    public void attemptToRegister(){
        final String phoneNumber = mRegPhone.getText().toString();
        String authCode = mRegCode.getText().toString();
        String password = mRegPassword.getText().toString();
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
        HttpUtils.modifyUser(phoneNumber, authCode, password, ACTION_REGISTER).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body() != null && response.body().isOk()){
                    ToastUtil.show(LoginActivity.this, "注册成功");
                    UserStatus.login(getApplicationContext(), phoneNumber);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else {
                    ToastUtil.show(LoginActivity.this, "注册失败，" + response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
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
                if(response.body() != null && response.body().isOk()){
                    authCodeTimer.start();
                    ToastUtil.show(LoginActivity.this, "验证码发送成功");
                }else {
                    mMsgCode.setClickable(true);
                    mMsgCode.setBackgroundResource(R.drawable.selector_radius_blue_btn);
                    ToastUtil.show(LoginActivity.this, "验证码发送失败，" + response.body().getMessage());
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
                mToLogin.setTextColor(getResources().getColor(R.color.text_blue));
                mToRegister.setTextColor(getResources().getColor(R.color.textPrimaryDark));
                findViewById(R.id.login_et).setVisibility(View.VISIBLE);
                findViewById(R.id.register_et).setVisibility(View.GONE);
                break;
            case R.id.switch_to_register:
                mToRegister.setTextColor(getResources().getColor(R.color.text_blue));
                mToLogin.setTextColor(getResources().getColor(R.color.textPrimaryDark));
                findViewById(R.id.register_et).setVisibility(View.VISIBLE);
                findViewById(R.id.login_et).setVisibility(View.GONE);
                break;
            case R.id.login_btn:
                WindowUtils.toggleKeyboard(this, v, false);
                if(mToLogin.isChecked())
                    attemptToLogin();
                else
                    attemptToRegister();
                break;
            case R.id.login_with_wechat:
                // 查看登录状态 debug
                HttpUtils.test().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            Log.e("test", response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                break;
            case R.id.enter_without_login:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("showSplash", false);
                intent.putExtra("defaultItem", 1);
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

    public void onSkipSplash(View view) {
        mSplashHandler.removeCallbacks(showMainPage);
        getWindow().getDecorView().post(showMainPage);
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
