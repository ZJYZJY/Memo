package com.donutcn.memo.event;

import android.support.v4.app.Fragment;
import android.content.Context;

import com.donutcn.memo.activity.MainActivity;
import com.donutcn.memo.adapter.ViewPagerAdapter;
import com.donutcn.memo.fragment.home.HomeFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * com.donutcn.memo.event
 * Created by 73958 on 2017/7/23.
 */

public class ReceiveNewMessagesEvent extends Observable {

    private final String TAG = "onReceiveNewMessages";

    public ReceiveNewMessagesEvent(Context context, int fragmentIndex){
        Fragment fragment = ((MainActivity)context).getSupportFragmentManager().findFragmentByTag(ViewPagerAdapter.tagList.get(fragmentIndex));
        this.addObserver((Observer) fragment);
    }

    public void onReceiveNewMessages(int msgType, int msgCount){
        Map<String, Integer> map = new HashMap<>();
        map.put("msgType", msgType);
        map.put("msgCount", msgCount);
        setChanged();
        notifyObservers(map);
    }

    public String getTag(){
        return this.TAG;
    }
}
