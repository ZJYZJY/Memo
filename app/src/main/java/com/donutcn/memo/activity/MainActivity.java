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
import com.donutcn.memo.fragment.SplashFragment;
import com.donutcn.memo.fragment.discover.DiscoverFragment;
import com.donutcn.memo.fragment.home.HomeFragment;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.widgetlib.widget.CheckableImageButton;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private CheckableImageButton mHome, mDiscover, mPublish;
    private LinearLayout mMainContainer;
    public SplashFragment splashFragment;

    private long mExitTime = 0;

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

        mViewPager.addOnPageChangeListener(this);
//        mViewPager.setOffscreenPageLimit(2);
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        mAdapter.addFragment(new HomeFragment());
        mAdapter.addFragment(new DiscoverFragment());

        viewPager.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_bottom_home:
                mHome.setChecked(true);
                mDiscover.setChecked(false);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.main_bottom_dis:
                mHome.setChecked(false);
                mDiscover.setChecked(true);
                mViewPager.setCurrentItem(1, false);
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mViewPager.setCurrentItem(position);
        mAdapter.update(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
