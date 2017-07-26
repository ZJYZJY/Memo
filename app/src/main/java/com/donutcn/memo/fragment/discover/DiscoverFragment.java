package com.donutcn.memo.fragment.discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.donutcn.memo.R;
import com.donutcn.memo.activity.AuthorPage;
import com.donutcn.memo.adapter.TabFragmentPagerAdapter;
import com.donutcn.memo.event.ReceiveNewMessagesEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class DiscoverFragment extends Fragment implements OnTabSelectListener {

    private TabFragmentPagerAdapter mPagerAdapter;
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
        mPagerAdapter = new TabFragmentPagerAdapter(
                getActivity().getSupportFragmentManager(), getContext(), 1);
        mViewPager = (ViewPager) view.findViewById(R.id.dis_viewpager);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mTabLayout.setViewPager(mViewPager);
        mTabLayout.setOnTabSelectListener(this);
        mTabLayout.showMsg(1, 3);
        mTabLayout.showDot(0);
        return view;
    }

    public int getCurrentPagePosition(){
        return mTabLayout.getCurrentTab();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        Refresh();
    }

    @Override
    public void onTabSelect(int position) {
        mTabLayout.hideMsg(position);
    }

    @Override
    public void onTabReselect(int position) {
        mTabLayout.hideMsg(position);
        EventBus.getDefault().post(new RequestRefreshEvent(getCurrentPagePosition() + 2));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onReceiveNewMessagesEvent(ReceiveNewMessagesEvent event){
        if(event.getMessagePos() >= 2){
            mTabLayout.showMsg(event.getMessagePos() - 2, event.getMessageCount());
        }
    }
}
