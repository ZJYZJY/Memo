package com.donutcn.memo.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.SimpleFragmentPagerAdapter;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

public class HomeFragment extends Fragment implements OnTabSelectListener {

    private SimpleFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private SlidingTabLayout tabLayout;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        pagerAdapter = new SimpleFragmentPagerAdapter(getActivity().getSupportFragmentManager(), getContext(), 0);
//        toolbar = (Toolbar) view.findViewById(R.id.home_toolbar);
        viewPager = (ViewPager) view.findViewById(R.id.home_viewpager);
        viewPager.setAdapter(pagerAdapter);
        tabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setViewPager(viewPager);
        tabLayout.setOnTabSelectListener(this);
        tabLayout.showMsg(0, 2);
        tabLayout.showDot(1);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Refresh();
    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }

    public void update(){
        Refresh();
    }

    public void Refresh(){

    }

    @Override
    public void onResume() {
        super.onResume();
        Refresh();
    }
}
