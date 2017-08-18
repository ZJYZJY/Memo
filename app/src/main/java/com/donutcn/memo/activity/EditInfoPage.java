package com.donutcn.memo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.donutcn.memo.R;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.event.LoginStateEvent;
import com.donutcn.memo.listener.UploadCallback;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.donutcn.memo.utils.WindowUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class EditInfoPage extends AppCompatActivity implements View.OnClickListener {

    private ImageView mUserIcon;
    private TextView mNickname, mGender, mSignature;

    private List<File> mIconFile;
    private RequestManager glide;

    private static final int MODIFY_NAME = 0;
    private static final int MODIFY_GENDER = 1;
    private static final int MODIFY_AVATAR = 2;
    private static final int MODIFY_SIGNATURE = 3;

    private final String[] mGenderType = {"男", "女"};
    private static final String HOST = "http://otu6v4c72.bkt.clouddn.com/";
    private static final String strategy = "?imageMogr2/auto-orient/thumbnail/!60p/format/jpg" +
            "/interlace/1/blur/1x0/quality/50|imageslim";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info_page);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        WindowUtils.setToolBarTitle(this, R.string.title_activity_edit_info);

        initView();
    }

    public void initView(){
        glide = Glide.with(this);
        mUserIcon = (ImageView) findViewById(R.id.edit_info_icon);
        mNickname = (TextView) findViewById(R.id.edit_info_nickname);
        mGender = (TextView) findViewById(R.id.edit_info_gender);
        mSignature = (TextView) findViewById(R.id.edit_info_signature);

        String iconUrl = UserStatus.getCurrentUser().getIconUrl();
        if(iconUrl != null){
            glide.load(iconUrl).centerCrop().into(mUserIcon);
        }
        mNickname.setText(UserStatus.getCurrentUser().getName());
        String gender = UserStatus.getCurrentUser().getGender();
        if(gender != null){
            mGender.setText(gender);
        }
        String signature = UserStatus.getCurrentUser().getSignature();
        if(signature != null){
            mSignature.setText(signature);
        }


        findViewById(R.id.edit_info_icon_container).setOnClickListener(this);
        findViewById(R.id.edit_info_nickname_container).setOnClickListener(this);
        findViewById(R.id.edit_info_gender_container).setOnClickListener(this);
        findViewById(R.id.edit_info_signature_container).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_info_icon_container:
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setPreviewEnabled(true)
                        .start(this);
                break;
            case R.id.edit_info_nickname_container:
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_text, null);
                final EditText nickname = (EditText) view.findViewById(R.id.edit_user_info_et);
                String name = UserStatus.getCurrentUser().getName();
                nickname.setText(name);
                nickname.setSelection(name.length());
                new AlertDialog.Builder(this)
                        .setTitle("给自己起个昵称吧")
                        .setView(view)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, String> data = new HashMap<>();
                                data.put("name", nickname.getText().toString());
                                modifyInfo(data, MODIFY_NAME);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.edit_info_signature_container:
                View view1 = LayoutInflater.from(this).inflate(R.layout.dialog_edit_text, null);
                final EditText signature = (EditText) view1.findViewById(R.id.edit_user_info_et);
                String sign = UserStatus.getCurrentUser().getSignature();
                if(sign != null){
                    signature.setText(sign);
                    signature.setSelection(sign.length());
                }
                new AlertDialog.Builder(this)
                        .setTitle("个性签名")
                        .setView(view1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, String> data = new HashMap<>();
                                data.put("self_introduction", signature.getText().toString());
                                modifyInfo(data, MODIFY_SIGNATURE);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.edit_info_gender_container:
                new AlertDialog.Builder(this)
                        .setItems(mGenderType, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, String> data = new HashMap<>();
                                data.put("sex", mGenderType[which]);
                                modifyInfo(data, MODIFY_GENDER);
                            }
                        })
                        .show();
                break;
        }
    }

    public void modifyInfo(final Map<String, String> data, final int type){
        HttpUtils.modifyUserInfo(data).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body() != null){
                    LogUtil.d(response.body().toString());
                    if(response.body().isOk()){
                        switch (type){
                            case MODIFY_NAME:
                                mNickname.setText(data.get("name"));
                                break;
                            case MODIFY_GENDER:
                                mGender.setText(data.get("sex"));
                                break;
                            case MODIFY_AVATAR:
                                Glide.with(EditInfoPage.this).load(mIconFile.get(0)).into(mUserIcon);
                                break;
                            case MODIFY_SIGNATURE:
                                mSignature.setText(data.get("self_introduction"));
                                break;
                        }
                        ToastUtil.show(EditInfoPage.this, "设置成功");
                        UserStatus.setCurrentUser(data);
                        EventBus.getDefault().postSticky(new LoginStateEvent(LoginStateEvent.SYNC, UserStatus.getCurrentUser()));
                    } else {
                        ToastUtil.show(EditInfoPage.this, "设置失败");
                    }
                } else {
                    ToastUtil.show(EditInfoPage.this, "设置失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                ToastUtil.show(EditInfoPage.this, "连接失败，请检查你的网络连接");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                for(String s : photos){
                    LogUtil.d("select_photo", s);
                }
            }

            if (photos != null) {
                // compress the image.
                Luban.with(this).load(new File(photos.get(0)))
                        .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {}
                    @Override
                    public void onSuccess(File file) {
                        mIconFile = new ArrayList<>();
                        mIconFile.add(file);
                        uploadUserIcon();
                    }
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtil.show(EditInfoPage.this, "压缩失败");
                    }
                }).launch();
            }
        }
    }

    public void uploadUserIcon(){
        LogUtil.d("upload_icon");
        HttpUtils.upLoadImages(this, mIconFile, new UploadCallback<String>() {
            @Override
            public void uploadProgress(int progress, int total) {}
            @Override
            public void uploadAll(List<String> keys) {
                final Map<String, String> data = new HashMap<>();
                data.put("head_portrait", HOST + keys.get(0) + strategy);
                modifyInfo(data, MODIFY_AVATAR);
            }
            @Override
            public void uploadFail(String error) {
                ToastUtil.show(EditInfoPage.this, "上传失败，" + error);
            }
        });
    }

    public void onBack(View view){
        finish();
    }
}
