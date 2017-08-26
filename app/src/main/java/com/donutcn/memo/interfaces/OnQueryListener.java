package com.donutcn.memo.interfaces;

import java.util.List;

/**
 * com.donutcn.memo.listener
 * Created by 73958 on 2017/8/3.
 */

public interface OnQueryListener<T> {

    void onQueryProgress(int progress, int total);

    void onQueryComplete(List<T> list);
}
