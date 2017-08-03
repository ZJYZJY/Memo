package com.donutcn.memo.listener;

import java.util.List;

/**
 * com.donutcn.memo.listener
 * Created by 73958 on 2017/7/29.
 */

public interface UploadCallback {

    /**
     * after single file was uploaded, this method will be called.
     */
    void uploadSingle(String key);

    /**
     * after all files were uploaded, this method will be called.
     */
    void uploadAll(List<String> keys);

    /**
     * upload files fail.
     */
    void uploadFail(String error);
}
