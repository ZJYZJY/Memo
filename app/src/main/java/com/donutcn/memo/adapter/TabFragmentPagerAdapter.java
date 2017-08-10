package com.donutcn.memo.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.donutcn.memo.fragment.discover.FollowedFragment;
import com.donutcn.memo.fragment.discover.LatestFragment;
import com.donutcn.memo.fragment.discover.FriendsFragment;
import com.donutcn.memo.fragment.discover.RecommendFragment;
import com.donutcn.memo.fragment.home.MemoFragment;
import com.donutcn.memo.fragment.home.MessageFragment;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    private int PAGE_COUNT;
    private String tabTitles0[] = new String[]{"人人记", "消息"};
    private String tabTitles1[] = new String[]{"推荐", "最新", "关注", "好友"};
    private int type;
    private Context context;

    private FragmentManager mFragmentManager;

    public TabFragmentPagerAdapter(FragmentManager fm, Context context, int type) {
        super(fm);
        mFragmentManager = fm;
        this.context = context;
        this.type = type;
        if(type == 0)
            PAGE_COUNT = 2;
        else
            PAGE_COUNT = 4;
    }

    public Fragment getFragment(int position){
        // "+2" is for skip the HomeFragment and DiscoverFragment.
        if(type == 0)
            return mFragmentManager.getFragments().get(position + 2);
        else
            return mFragmentManager.getFragments().get(position + 4);
    }

    @Override
    public Fragment getItem(int position) {
        if (type == 0) {
            if (position == 0) {
                return new MemoFragment();
            } else {
                return new MessageFragment();
            }
        } else {
            if (position == 0) {
                return new RecommendFragment();
            } else if(position == 1){
                return new LatestFragment();
            } else if(position == 2){
                return new FollowedFragment();
            } else {
                return new FriendsFragment();
            }
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (type == 0)
            return tabTitles0[position];
        else
            return tabTitles1[position];
    }
}