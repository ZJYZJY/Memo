package com.donutcn.memo.presenter;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.entity.BriefMessage;
import com.donutcn.memo.entity.MessageItem;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.fragment.api.DeleteContent;
import com.donutcn.memo.fragment.api.FetchContent;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * com.donutcn.memo.presenter
 * Created by 73958 on 2017/8/22.
 */

public class MessagePresenter {

    private FetchContent<MessageItem> fetchContent;
    private DeleteContent deleteContent;

    public MessagePresenter(Context context){
        this.fetchContent = (FetchContent) context;
        this.deleteContent = (DeleteContent) context;
    }

    public void refresh(final List<MessageItem> list){
        fetchContent.loadMoreSuccess(list);
    }

    public void loadMore(List<MessageItem> list){
        fetchContent.loadMoreSuccess(list);
    }

    public void deleteContent(List<MessageItem> list, final int position){
        deleteContent.deleteSuccess(position);
    }
}
