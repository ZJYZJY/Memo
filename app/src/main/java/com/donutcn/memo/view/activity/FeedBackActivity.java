package com.donutcn.memo.view.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.donutcn.memo.R;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.WindowUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedBackActivity extends AppCompatActivity {

    private EditText mDetail, mContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        WindowUtils.setToolBarTitle(this, getString(R.string.title_activity_feed_back));

        mDetail = (EditText) findViewById(R.id.feedback_detail);
        mContact = (EditText) findViewById(R.id.feedback_contact);
    }

    public void onSubmit(View view){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("请稍等...");
        String detail = mDetail.getText().toString().trim();
        if(TextUtils.isEmpty(detail)){
            ToastUtil.show(this, "请填写反馈意见");
        }
        String contact = mContact.getText().toString().trim();
        if(TextUtils.isEmpty(contact)){
            ToastUtil.show(this, "请填写联系方式");
        }
        dialog.show();
        HttpUtils.feedback(detail, contact).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                dialog.dismiss();
                if(response.body() != null){
                    if(response.body().isOk()){
                        ToastUtil.show(FeedBackActivity.this, response.body().getMessage());
                        finish();
                    } else {
                        ToastUtil.show(FeedBackActivity.this, response.body().getMessage());
                    }
                } else {
                    ToastUtil.show(FeedBackActivity.this, "反馈失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
                ToastUtil.show(FeedBackActivity.this, "反馈连接失败");
            }
        });
    }
}
