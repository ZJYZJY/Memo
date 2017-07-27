package com.donutcn.memo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.donutcn.memo.R;
import com.donutcn.memo.utils.WindowUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioButton mToLogin, mToRegister;
    private RadioGroup mRadioGroup;
    private View mWechat, mWithoutLogin, mMsgCode;
    private Button mLogin;
    private EditText mPhoneNum, mPassword;
    private EditText mRegPhone, mRegPassword, mRegCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        WindowUtils.setStatusBarColor(this, R.color.background_blank, true);

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
        mMsgCode = findViewById(R.id.tv_get_msg_code);

        mToLogin.setOnClickListener(this);
        mToRegister.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mWechat.setOnClickListener(this);
        mWithoutLogin.setOnClickListener(this);
    }

    public void attemptToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("showSplash", false);
        startActivity(intent);
        finish();
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
                attemptToLogin();
                break;
            case R.id.login_with_wechat:
                break;
            case R.id.enter_without_login:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("showSplash", false);
                intent.putExtra("defaultItem", 1);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_get_msg_code:
                break;
        }
    }
}
