package com.donutcn.memo.view.api;

import com.donutcn.memo.entity.BriefContent;

import java.util.List;

/**
 * com.donutcn.memo.fragment.api
 * Created by 73958 on 2017/8/21.
 */

public interface FetchContent<T> {

    /**
     * refresh content successfully
     *
     * @param list current list of {@link BriefContent}
     */
    void refreshSuccess(List<T> list);

    /**
     * refresh content failed
     *
     * @param code error code
     * @param error error message
     */
    void refreshFail(int code, String error);

    /**
     * load content successfully
     *
     * @param list current list of {@link BriefContent}
     */
    void loadMoreSuccess(List<T> list);

    /**
     * load content failed
     *
     * @param code error code
     * @param error error message
     */
    void loadMoreFail(int code, String error);
}
