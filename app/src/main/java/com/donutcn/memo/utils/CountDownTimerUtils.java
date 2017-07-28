package com.donutcn.memo.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.donutcn.memo.R;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/28.
 */

public class CountDownTimerUtils extends CountDownTimer {
    private TextView mTextView;

    /**
     * @param textView          The TextView
     *
     *
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receiver
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimerUtils(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setClickable(false);
        mTextView.setText(millisUntilFinished / 1000 + "秒后可重新发送");
        mTextView.setBackgroundResource(R.drawable.disabled_gray_btn_bg);
    }

    @Override
    public void onFinish() {
        mTextView.setText("重新获取验证码");
        mTextView.setClickable(true);
        mTextView.setBackgroundResource(R.drawable.selector_radius_blue_btn);
    }
}