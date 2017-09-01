package com.donutcn.memo.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.donutcn.memo.IMService;
import com.donutcn.memo.R;
import com.donutcn.memo.adapter.ViewPagerAdapter;
import com.donutcn.memo.constant.FieldConstant;
import com.donutcn.memo.entity.BriefMessage;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.entity.SyncInfoPackage;
import com.donutcn.memo.event.LoginStateEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.view.fragment.SplashFragment;
import com.donutcn.memo.view.fragment.discover.DiscoverFragment;
import com.donutcn.memo.view.fragment.home.HomeFragment;
import com.donutcn.memo.helper.LoginHelper;
import com.donutcn.memo.interfaces.UploadCallback;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.widgetlib.widget.CheckableImageButton;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private CheckableImageButton mHome, mDiscover, mPublish;
    public SplashFragment splashFragment;
    private HomeFragment mHomeFragment;
    private DiscoverFragment mDiscoverFragment;

    private ProgressBar mMainProgress;
    private PopupWindow mPopupWindow;
    private ImageView mUserIcon;

    private List<File> mIconFile;
    private BriefMessage mMessage;
    private Intent serviceIntent;

    private long mExitTime = 0;
    private int mDefaultItem;
    private static final String HOST = "http://otu6v4c72.bkt.clouddn.com/";
    private static final String strategy = "?imageMogr2/auto-orient/thumbnail/!60p/format/jpg" +
            "/interlace/1/blur/1x0/quality/50|imageslim";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);
        EventBus.getDefault().register(this);

        if (getIntent().getBooleanExtra("unlogin", false)) {
            mDefaultItem = 1;
        } else {
            serviceIntent = new Intent(MainActivity.this, IMService.class);
            boolean completeInfo = getIntent().getBooleanExtra("completeInfo", false);
            startService(serviceIntent);
            if (completeInfo) {
                getWindow().getDecorView().postDelayed(showInfoPopup, 200);
            }
        }

        mIconFile = new ArrayList<>();
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mHome = (CheckableImageButton) findViewById(R.id.main_bottom_home);
        mPublish = (CheckableImageButton) findViewById(R.id.main_bottom_pub);
        mDiscover = (CheckableImageButton) findViewById(R.id.main_bottom_dis);

        mHome.setOnClickListener(this);
        mPublish.setOnClickListener(this);
        mDiscover.setOnClickListener(this);

        initViewPager();

    }

    private void initViewPager() {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        mHomeFragment = new HomeFragment();
        mDiscoverFragment = new DiscoverFragment();
        mAdapter.addFragment(mHomeFragment);
        mAdapter.addFragment(mDiscoverFragment);
        mViewPager.setAdapter(mAdapter);
        if(mDefaultItem != 0){
            mHome.setChecked(false);
            mDiscover.setChecked(true);
            mViewPager.setCurrentItem(mDefaultItem, false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_bottom_home:
                if(mHome.isChecked()){
                    EventBus.getDefault().post(new RequestRefreshEvent(
                            mHomeFragment.getCurrentPagePosition()));
                }else {
                    mHome.setChecked(true);
                    mDiscover.setChecked(false);
                    mViewPager.setCurrentItem(0, false);
                }
                break;
            case R.id.main_bottom_dis:
                if(mDiscover.isChecked()){
                    EventBus.getDefault().post(new RequestRefreshEvent(
                            mDiscoverFragment.getCurrentPagePosition() + 2));
                }else {
                    mHome.setChecked(false);
                    mDiscover.setChecked(true);
                    mViewPager.setCurrentItem(1, false);
                }
                break;
            case R.id.main_bottom_pub:
                if(LoginHelper.ifRequestLogin(this, "请先登录")){
                    return;
                }
                startActivity(new Intent(this, PublishActivity.class));
                break;
        }
    }

    private Runnable showInfoPopup = new Runnable() {
        @Override
        public void run() {
            if(hasWindowFocus()){
                showInfoPopup();
                getWindow().getDecorView().removeCallbacks(this);
            } else {
                getWindow().getDecorView().postDelayed(this, 200);
            }
        }
    };

    public void showInfoPopup(){
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        final View popWindowView = getLayoutInflater().inflate(R.layout.popup_main_complete, null);
        mPopupWindow = new PopupWindow(popWindowView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setAnimationStyle(R.style.AnimPopup);
        mPopupWindow.setClippingEnabled(false);
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        mMainProgress = (ProgressBar) popWindowView.findViewById(R.id.main_progress);
        mUserIcon = (ImageView) popWindowView.findViewById(R.id.main_complete_icon);
        final EditText userName = (EditText) popWindowView.findViewById(R.id.main_complete_name);
        View submit = popWindowView.findViewById(R.id.main_submit);
        String iconUrl = UserStatus.getCurrentUser().getIconUrl();
        String name = UserStatus.getCurrentUser().getName();
        if(iconUrl != null && !iconUrl.equals("")){
            Glide.with(this).load(iconUrl).centerCrop().into(mUserIcon);
        }
        if(name != null && !name.equals("")){
            userName.setText(name);
            userName.setSelection(name.length());
        }
        mUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setPreviewEnabled(true)
                        .start(MainActivity.this);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUserIcon(userName.getText().toString().trim());
                mMainProgress.setVisibility(View.VISIBLE);
            }
        });

        // close popup window
        popWindowView.findViewById(R.id.completing_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

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
                final List<String> finalPhotos = photos;
                Luban.with(this).load(new File(photos.get(0)))
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {}
                            @Override
                            public void onSuccess(File file) {
                                mIconFile = new ArrayList<>();
                                mIconFile.add(file);
                                Glide.with(MainActivity.this).load(finalPhotos.get(0)).into(mUserIcon);
                            }
                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                ToastUtil.show(MainActivity.this, "压缩失败");
                            }
                        }).launch();
            }
        }
    }

    public void uploadUserIcon(final String name){
        HttpUtils.upLoadImages(this, mIconFile, new UploadCallback<String>() {
            @Override
            public void uploadProgress(int progress, int total) {}
            @Override
            public void uploadAll(List<String> keys) {
                final Map<String, String> data = new HashMap<>();
                if(keys != null){
                    data.put(FieldConstant.USER_ICON_URL, HOST + keys.get(0) + strategy);
                }
                if(!name.equals(UserStatus.getCurrentUser().getName())){
                    data.put(FieldConstant.USER_NICKNAME, name);
                }
                modifyInfo(data);
            }
            @Override
            public void uploadFail(String error) {
                ToastUtil.show(MainActivity.this, "上传失败，" + error);
            }
        });
    }

    public void modifyInfo(final Map<String, String> data){
        HttpUtils.modifyUserInfo(data).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body() != null){
                    LogUtil.d(response.body().toString());
                    if(response.body().isOk()){
                        mPopupWindow.dismiss();
                        ToastUtil.show(MainActivity.this, "设置成功");
                        UserStatus.setCurrentUser(data);
                        EventBus.getDefault().postSticky(new LoginStateEvent(LoginStateEvent.SYNC, UserStatus.getCurrentUser()));
                    } else {
                        mMainProgress.setVisibility(View.GONE);
                        ToastUtil.show(MainActivity.this, response.body().getMessage());
                    }
                } else {
                    mMainProgress.setVisibility(View.GONE);
                    ToastUtil.show(MainActivity.this, "设置失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                mPopupWindow.dismiss();
                ToastUtil.show(MainActivity.this, "连接失败，请检查你的网络连接");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(splashFragment == null){
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if(serviceIntent != null){
            stopService(serviceIntent);
        }
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && splashFragment == null) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.show(MainActivity.this, getString(R.string.toast_exit_double_click));
                mExitTime = System.currentTimeMillis();
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setAction("ExitApp");
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * receive the sync info event
     * @param event
     */
    @Subscribe(sticky = true)
    public void onSyncInfo(SyncInfoPackage event) {
        switch (event.getType()){
            case 0:
            case 1:
                UserStatus.getCurrentUser().follow(this, event.getUserId(), event.getType() == 1);
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onLoginStateEvent(LoginStateEvent event) {
        if (event.isLogin()) {
            // register push service
            XGPushManager.registerPush(getApplicationContext(),
                    UserStatus.getCurrentUser().getUserId(), new XGIOperateCallback() {
                @Override
                public void onSuccess(Object o, int i) {
                    LogUtil.d("XGPush", "注册成功，设备token为：" + o);
                }

                @Override
                public void onFail(Object o, int i, String s) {
                    LogUtil.d("XGPush", "注册失败，错误码：" + i + ",错误信息：" + s);
                }
            });
        }
    }
}
