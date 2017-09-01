package com.donutcn.memo.view.fragment.discover;

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
import com.donutcn.memo.helper.LoginHelper;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class DiscoverFragment extends Fragment implements OnTabSelectListener {

    private TabFragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private SlidingTabLayout mTabLayout;
    private ImageView mUserCenter_iv;

    private Context mContext;
    private RequestManager glide;
    private long recommendRefresh = 0;
    private long latestRefresh = 0;
    private long followedRefresh = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        glide = Glide.with(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        mUserCenter_iv = (ImageView) view.findViewById(R.id.user_center);
        mUserCenter_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!LoginHelper.ifRequestLogin(getContext(), "请先登录")){
                startActivity(new Intent(getContext(), PersonalCenterActivity.class));
//                    startActivity(new Intent(getContext(), AuthorPage.class));
                }
            }
        });
        mPagerAdapter = new TabFragmentPagerAdapter(
                getActivity().getSupportFragmentManager(), getContext(), 1);
        mViewPager = (ViewPager) view.findViewById(R.id.dis_viewpager);
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
        recommendRefresh = System.currentTimeMillis();
    }

    @Override
    public void onTabSelect(int position) {
        // if not the recommend page, request to login.
        if(position != 0 && position != 1){
            if(LoginHelper.ifRequestLogin(getContext(), "请先登录")){
                return;
            }
        }
        // refresh strategy.
        switch (position){
            case 0:
                if (recommendRefresh == 0 || System.currentTimeMillis() - recommendRefresh > 3 * 60 * 1000) {
                    EventBus.getDefault().post(new RequestRefreshEvent(2));
                    recommendRefresh = System.currentTimeMillis();
                }
                break;
            case 1:
                if (latestRefresh == 0 || System.currentTimeMillis() - latestRefresh > 3 * 60 * 1000) {
                    EventBus.getDefault().post(new RequestRefreshEvent(3));
                    latestRefresh = System.currentTimeMillis();
                }
                break;
            case 2:
                if (followedRefresh == 0 || System.currentTimeMillis() - followedRefresh > 3 * 60 * 1000) {
                    EventBus.getDefault().post(new RequestRefreshEvent(4));
                    followedRefresh = System.currentTimeMillis();
                }
                break;
        }

        mTabLayout.hideMsg(position);
    }

    @Override
    public void onTabReselect(int position) {
        mTabLayout.hideMsg(position);
        EventBus.getDefault().post(new RequestRefreshEvent(getCurrentPagePosition() + 2));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(sticky = true)
    public void onChangeRedDotEvent(ChangeRedDotEvent event){
        if(event.getDotPosition() >= 2){
            mTabLayout.showMsg(event.getDotPosition() - 2, event.getCount());
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
