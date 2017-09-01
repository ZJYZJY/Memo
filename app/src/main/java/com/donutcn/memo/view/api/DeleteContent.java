package com.donutcn.memo.view.api;

/**
 * com.donutcn.memo.fragment.api
 * Created by 73958 on 2017/8/21.
 */

public interface DeleteContent {

    /**
     * delete user's content successfully
     *
     * @param position the item position which need to be deleted
     */
    void deleteSuccess(int position);

    /**
     * delete user's content failed
     *
     * @param code error code
     * @param error error message
     */
    void deleteFail(int code, String error);
}
