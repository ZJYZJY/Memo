package com.donutcn.memo.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.ViewPagerAdapter;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.fragment.SplashFragment;
import com.donutcn.memo.fragment.discover.DiscoverFragment;
import com.donutcn.memo.fragment.home.HomeFragment;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.widgetlib.widget.CheckableImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private CheckableImageButton mHome, mDiscover, mPublish;
    private LinearLayout mMainContainer;
    public SplashFragment splashFragment;
    private HomeFragment mHomeFragment;
    private DiscoverFragment mDiscoverFragment;

    public RequestRefreshEvent mRequestRefreshEvent;

    private long mExitTime = 0;
    private boolean isFirstSet = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        // show splash fragment.
        setSplashFragment();

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mHome = (CheckableImageButton) findViewById(R.id.main_bottom_home);
        mPublish = (CheckableImageButton) findViewById(R.id.main_bottom_pub);
        mDiscover = (CheckableImageButton) findViewById(R.id.main_bottom_dis);

        mHome.setOnClickListener(this);
        mPublish.setOnClickListener(this);
        mDiscover.setOnClickListener(this);

        initViewPager(mViewPager);
    }

    public void setSplashFragment() {
        mMainContainer = (LinearLayout) findViewById(R.id.main_container);
        mMainContainer.setVisibility(View.GONE);
        splashFragment = new SplashFragment();
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.splash_container, splashFragment);
        transaction.commit();
        // set flag full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // TODO: To determine whether or not to log in. if not logged in, start the LoginActivity.

        // delay 3s to remove the splash fragment.
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                showMainPage();
            }
        }, 3000);
    }

    private void showMainPage(){
        if (splashFragment == null)
            return;
        // remove splash fragment.
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(splashFragment);
        transaction.commit();
        splashFragment = null;
        mMainContainer.setVisibility(View.VISIBLE);
        // clear flag full screen.
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initViewPager(ViewPager viewPager) {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        mHomeFragment = new HomeFragment();
        mDiscoverFragment = new DiscoverFragment();
        mAdapter.addFragment(mHomeFragment);
        mAdapter.addFragment(mDiscoverFragment);
        viewPager.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        if(isFirstSet){
            // init RequestRefreshEvent and add observers.
            mRequestRefreshEvent = new RequestRefreshEvent(this);
            isFirstSet = false;
        }
        switch (v.getId()) {
            case R.id.main_bottom_home:
                if(mHome.isChecked()){
                    mRequestRefreshEvent.requestRefresh(mHomeFragment.getCurrentPagePosition());
                }else {
                    mHome.setChecked(true);
                    mDiscover.setChecked(false);
                    mViewPager.setCurrentItem(0, false);
                }
                break;
            case R.id.main_bottom_dis:
                if(mDiscover.isChecked()){
                    mRequestRefreshEvent.requestRefresh(mDiscoverFragment.getCurrentPagePosition() + 2);
                }else {
                    mHome.setChecked(false);
                    mDiscover.setChecked(true);
                    mViewPager.setCurrentItem(1, false);
                }
                break;
            case R.id.main_bottom_pub:
                startActivity(new Intent(this, PublishActivity.class));
                break;
        }
    }

    public void onSkipSplash(View view) {
        showMainPage();
    }

    @Override
    public void onBackPressed() {
        if(splashFragment == null){
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mRequestRefreshEvent = new RequestRefreshEvent(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mRequestRefreshEvent != null)
            mRequestRefreshEvent.deleteObservers();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && splashFragment == null) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(MainActivity.this, getString(R.string.toast_exit_double_click), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
