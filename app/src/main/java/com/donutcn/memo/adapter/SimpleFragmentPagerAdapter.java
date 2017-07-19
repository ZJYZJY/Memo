package com.donutcn.memo.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.donutcn.memo.fragment.discover.FriendsFragment;
import com.donutcn.memo.fragment.discover.RecommendFragment;
import com.donutcn.memo.fragment.home.HaoYeFragment;
import com.donutcn.memo.fragment.home.MessageFragment;
import com.donutcn.memo.listener.OnReceiveNewMessagesListener;

/**
 * com.hdu.waibaotest
 * Created by 73958 on 2017/7/4.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles0[] = new String[]{"好页", "消息"};
    private String tabTitles1[] = new String[]{"推荐", "好友圈"};
    private int type;
    private Context context;
    private OnReceiveNewMessagesListener mMsgListener;

    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context, int type) {
        super(fm);
        this.context = context;
        this.type = type;
    }

    public void setOnReceiveNewMessages(OnReceiveNewMessagesListener listener) {
        this.mMsgListener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        if (type == 0) {
            if (position == 0) {
                HaoYeFragment fragment = new HaoYeFragment();
                fragment.setOnReceiveNewMessagesListener(mMsgListener);
                return fragment;
            } else {
                MessageFragment fragment = new MessageFragment();
                fragment.setOnReceiveNewMessagesListener(mMsgListener);
                return fragment;
            }
        } else {
            if (position == 0) {
                RecommendFragment fragment = new RecommendFragment();
                fragment.setOnReceiveNewMessagesListener(mMsgListener);
                return fragment;
            } else {
                FriendsFragment fragment = new FriendsFragment();
                fragment.setOnReceiveNewMessagesListener(mMsgListener);
                return fragment;
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