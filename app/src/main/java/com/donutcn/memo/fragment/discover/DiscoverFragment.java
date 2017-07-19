package com.donutcn.memo.fragment.discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.donutcn.memo.R;
import com.donutcn.memo.activity.AuthorPage;
import com.donutcn.memo.adapter.SimpleFragmentPagerAdapter;
import com.donutcn.memo.listener.OnReceiveNewMessagesListener;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

public class DiscoverFragment extends Fragment implements OnTabSelectListener, OnReceiveNewMessagesListener {

    private SimpleFragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private SlidingTabLayout mTabLayout;
    private ImageView mUserCenter_iv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        mUserCenter_iv = (ImageView) view.findViewById(R.id.user_center);
        mUserCenter_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getContext(), PersonalCenterActivity.class));
                startActivity(new Intent(getContext(), AuthorPage.class));
            }
        });
        mPagerAdapter = new SimpleFragmentPagerAdapter(getActivity().getSupportFragmentManager(), getContext(), 1);
        mPagerAdapter.setOnReceiveNewMessages(this);
        mViewPager = (ViewPager) view.findViewById(R.id.dis_viewpager);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mTabLayout.setViewPager(mViewPager);
        mTabLayout.setOnTabSelectListener(this);
        mTabLayout.showMsg(1, 3);
        mTabLayout.showDot(0);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Refresh();
    }

    @Override
    public void onReceiveNewMessage(int msgCount, int msgType) {
        switch (msgType) {
            case 2:
                mTabLayout.showMsg(0, msgCount);
                break;
            case 3:
                mTabLayout.showMsg(1, msgCount);
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabSelect(int position) {
        mTabLayout.hideMsg(position);
    }

    @Override
    public void onTabReselect(int position) {
        mTabLayout.hideMsg(position);
    }

    public void update() {
        Refresh();
    }

    public void Refresh() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Refresh();
    }
}
