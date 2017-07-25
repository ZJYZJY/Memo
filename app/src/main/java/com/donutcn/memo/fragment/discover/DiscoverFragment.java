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
import com.donutcn.memo.activity.MainActivity;
import com.donutcn.memo.adapter.TabFragmentPagerAdapter;
import com.donutcn.memo.event.ReceiveNewMessagesEvent;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class DiscoverFragment extends Fragment implements OnTabSelectListener, Observer {

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

    public Fragment getPageFragment(int position){
        return mPagerAdapter.getFragment(position);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Refresh();
    }

    @Override
    public void onTabSelect(int position) {
        mTabLayout.hideMsg(position);
    }

    @Override
    public void onTabReselect(int position) {
        mTabLayout.hideMsg(position);
        // TODO: click top button before bottom button will crash.
        ((MainActivity)getContext()).mRequestRefreshEvent
                .requestRefresh(getCurrentPagePosition() + 2);
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
    public void update(Observable o, Object arg) {
        if (o instanceof ReceiveNewMessagesEvent) {
            mTabLayout.showMsg(((Map<String, Integer>)arg).get("msgType")
                    , ((Map<String, Integer>)arg).get("msgCount"));
        }
    }
}
