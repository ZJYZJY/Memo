package com.donutcn.memo.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.donutcn.memo.R;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.helper.LoginHelper;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.WindowUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TipOffActivity extends AppCompatActivity {

    private EditText mDetail;
    private RadioGroup mType;

    private String mContentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_off);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        WindowUtils.setToolBarTitle(this, R.string.title_activity_tip_off);

        mDetail = (EditText) findViewById(R.id.tip_off_detail);
        mType = (RadioGroup) findViewById(R.id.tip_off_type);

        String action = getIntent().getAction();
        if(action != null && action.equals(Intent.ACTION_VIEW)){
            String data = getIntent().getDataString();
            mContentId = data.substring(data.lastIndexOf("/") + 1);
        } else {
            mContentId = getIntent().getStringExtra("contentId");
        }

    }

    public void onSubmit(View view){
        String mDetailStr = mDetail.getText().toString().trim();
        if("".equals(mDetailStr)){
            ToastUtil.show(this, "请填写详细信息");
            return;
        }
        int type = 0;
        int id = mType.getCheckedRadioButtonId();
        if(id == -1){
            ToastUtil.show(this, "请选择举报原因");
            return;
        } else {
            switch (id){
                case R.id.tip_off_1:
                    type = -1;
                    break;
                case R.id.tip_off_2:
                    type = -2;
                    break;
                case R.id.tip_off_3:
                    type = -3;
                    break;
                case R.id.tip_off_4:
                    type = -4;
                    break;
                case R.id.tip_off_5:
                    type = -5;
                    break;
            }
        }
        HttpUtils.tipOff(mContentId, type, mDetailStr).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body() != null){
                    if(response.body().isOk()){
                        ToastUtil.show(TipOffActivity.this, "举报成功");
                        finish();
                    } else if (response.body().unAuthorized()){
                        ToastUtil.show(TipOffActivity.this, "登录授权过期，请重新登录");
                        LoginHelper.logout(TipOffActivity.this);
                    } else {
                        ToastUtil.show(TipOffActivity.this, response.body().getMessage());
                    }
                } else {
                    ToastUtil.show(TipOffActivity.this, "举报失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                ToastUtil.show(TipOffActivity.this, "举报连接失败");
            }
        });
    }

    public void onBack(View view){
        finish();
    }
}
