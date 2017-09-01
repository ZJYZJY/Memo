package com.donutcn.memo.view.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.donutcn.memo.R;
import com.donutcn.memo.view.activity.PersonalCenterActivity;
import com.donutcn.memo.adapter.TabFragmentPagerAdapter;
import com.donutcn.memo.event.LoginStateEvent;
import com.donutcn.memo.event.ChangeRedDotEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HomeFragment extends Fragment implements OnTabSelectListener {

    private TabFragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private SlidingTabLayout mTabLayout;
    private ImageView mUserCenter_iv;

    private Context mContext;
    private RequestManager glide;
    private int memoCount = 0;
    private int msgCount = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        glide = Glide.with(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mUserCenter_iv = (ImageView) view.findViewById(R.id.user_center);
        mUserCenter_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PersonalCenterActivity.class));
            }
        });

        mPagerAdapter = new TabFragmentPagerAdapter(
                getActivity().getSupportFragmentManager(), getContext(), 0);
        mViewPager = (ViewPager) view.findViewById(R.id.home_viewpager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mTabLayout.setViewPager(mViewPager);
        mTabLayout.setOnTabSelectListener(this);
        return view;
    }

    public int getCurrentPagePosition(){
        return mTabLayout.getCurrentTab();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onTabSelect(int position) {
        mTabLayout.hideMsg(position);
    }

    @Override
    public void onTabReselect(int position) {
        if(position == 0){
            memoCount = 0;
        } else {
            msgCount = 0;
        }
        mTabLayout.hideMsg(position);
        EventBus.getDefault().post(new RequestRefreshEvent(getCurrentPagePosition()));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onChangeRedDotEvent(ChangeRedDotEvent event){
        if(event.getDotPosition() == 0){
            memoCount = memoCount + event.getCount();
            mTabLayout.showMsg(event.getDotPosition(), memoCount);
        } else if(event.getDotPosition() == 1){
            if(event.getCount() == -1){
                mTabLayout.hideMsg(1);
            } else {
                msgCount =  msgCount + event.getCount();
                mTabLayout.showMsg(event.getDotPosition(), msgCount);
            }
        }
    }

    @Subscribe(sticky = true)
    public void onLoginStateEvent(LoginStateEvent event){
        if(event.isLogin() || event.isSync()){
            String iconUrl = event.getUser().getIconUrl();
            if(iconUrl == null || iconUrl.equals("")){
                mUserCenter_iv.setImageResource(R.mipmap.user_default_icon);
            }else {
                glide.load(iconUrl).centerCrop().into(mUserCenter_iv);
            }
        } else {
            mUserCenter_iv.setImageResource(R.drawable.mine);
        }
    }
}
