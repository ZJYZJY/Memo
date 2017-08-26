package com.donutcn.memo.interfaces;

import java.util.List;

/**
 * com.donutcn.memo.listener
 * Created by 73958 on 2017/7/29.
 */

public interface UploadCallback<T> {

    /**
     * after single step was finished, this method will be called.
     */
    void uploadProgress(int progress, int total);

    /**
     * after all files were uploaded, this method will be called.
     */
    void uploadAll(List<T> keys);

    /**
     * upload files fail.
     */
    void uploadFail(String error);
}
