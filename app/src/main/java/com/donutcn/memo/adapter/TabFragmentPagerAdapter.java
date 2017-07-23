package com.donutcn.memo.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.donutcn.memo.fragment.discover.FriendsFragment;
import com.donutcn.memo.fragment.discover.RecommendFragment;
import com.donutcn.memo.fragment.home.HaoYeFragment;
import com.donutcn.memo.fragment.home.MessageFragment;

/**
 * com.hdu.waibaotest
 * Created by 73958 on 2017/7/4.
 */

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles0[] = new String[]{"好页", "消息"};
    private String tabTitles1[] = new String[]{"推荐", "好友圈"};
    private int type;
    private Context context;

    public TabFragmentPagerAdapter(FragmentManager fm, Context context, int type) {
        super(fm);
        this.context = context;
        this.type = type;
    }

    @Override
    public Fragment getItem(int position) {
        if (type == 0) {
            if (position == 0) {
                return new HaoYeFragment();
            } else {
                return new MessageFragment();
            }
        } else {
            if (position == 0) {
                return new RecommendFragment();
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