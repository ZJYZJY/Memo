package com.donutcn.memo.event;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.donutcn.memo.activity.MainActivity;
import com.donutcn.memo.adapter.ViewPagerAdapter;
import com.donutcn.memo.fragment.discover.DiscoverFragment;
import com.donutcn.memo.fragment.home.HomeFragment;

import java.util.Observable;
import java.util.Observer;

/**
 * com.donutcn.memo.event
 * Created by 73958 on 2017/7/24.
 */

public class RequestRefreshEvent extends Observable {

    public RequestRefreshEvent(Context context){
        Fragment fragment0 = ((MainActivity)context)
                .getSupportFragmentManager().findFragmentByTag(ViewPagerAdapter.tagList.get(0));
        addObserver((Observer) ((HomeFragment)fragment0).getPageFragment(0));
        addObserver((Observer) ((HomeFragment)fragment0).getPageFragment(1));
        Fragment fragment1 = ((MainActivity)context)
                .getSupportFragmentManager().findFragmentByTag(ViewPagerAdapter.tagList.get(1));
        addObserver((Observer) ((DiscoverFragment)fragment1).getPageFragment(0));
        addObserver((Observer) ((DiscoverFragment)fragment1).getPageFragment(1));
    }

    public void requestRefresh(int fragmentPosition){
        setChanged();
        notifyObservers(fragmentPosition);
    }
}
