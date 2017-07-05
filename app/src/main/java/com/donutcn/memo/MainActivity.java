package com.donutcn.memo;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.donutcn.memo.activity.PublishActivity;
import com.donutcn.memo.adapter.ViewPagerAdapter;
import com.donutcn.memo.fragment.discover.DiscoverFragment;
import com.donutcn.memo.fragment.home.HomeFragment;
import com.donutcn.widgetlib.CheckableImageButton;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private CheckableImageButton home, discover, publish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        home = (CheckableImageButton) findViewById(R.id.main_bottom_home);
        publish = (CheckableImageButton) findViewById(R.id.main_bottom_pub);
        discover = (CheckableImageButton) findViewById(R.id.main_bottom_dis);

        viewPager.addOnPageChangeListener(this);
//        viewPager.setOffscreenPageLimit(2);
        home.setOnClickListener(this);
        publish.setOnClickListener(this);
        discover.setOnClickListener(this);

        initViewPager(viewPager);
    }

    private void initViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new DiscoverFragment());

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_bottom_home:
                home.setChecked(true);
                discover.setChecked(false);
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.main_bottom_dis:
                home.setChecked(false);
                discover.setChecked(true);
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.main_bottom_pub:
                startActivity(new Intent(this, PublishActivity.class));
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        viewPager.setCurrentItem(position);
        adapter.update(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
