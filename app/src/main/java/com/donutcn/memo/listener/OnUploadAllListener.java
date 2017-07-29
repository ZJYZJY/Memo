package com.donutcn.memo.listener;

import java.util.List;

/**
 * com.donutcn.memo.listener
 * Created by 73958 on 2017/7/29.
 */

public interface OnUploadAllListener {

    /**
     * after all files are uploaded, this method will be called.
     */
    void uploadAll(List<String> keys);
}