package com.donutcn.memo.presenter;

import android.content.Context;

import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.MessageItem;
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
    private String contentId;

    public MessagePresenter(Context context, String contentId){
        this.fetchContent = (FetchContent) context;
        this.deleteContent = (DeleteContent) context;
        this.contentId = contentId;
    }

    public void refresh(final List<MessageItem> list){
        HttpUtils.getReplyById(contentId, "down", list.size() == 0 ? 0 : list.get(0).getTimeStamp())
                .enqueue(new Callback<ArrayResponse<MessageItem>>() {
                    @Override
                    public void onResponse(Call<ArrayResponse<MessageItem>> call,
                                           Response<ArrayResponse<MessageItem>> response) {
                        if(response.body() != null){
                            LogUtil.d("msg_refresh", response.body().toString());
                            if(response.body().isOk()){
                                List<MessageItem> list = new ArrayList<>();
                                if(response.body().getData() != null){
                                    list.addAll(response.body().getData());
                                }
                                fetchContent.refreshSuccess(list);
                            } else {
                                fetchContent.refreshFail(response.body().getCode(),
                                        response.body().getMessage());
                            }
                        } else {
                            fetchContent.refreshFail(500, "刷新失败，服务器未知错误");
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayResponse<MessageItem>> call, Throwable t) {
                        t.printStackTrace();
                        fetchContent.refreshFail(408, "连接失败，请检查你的网络连接");
                    }
                });
    }

    public void loadMore(List<MessageItem> list){
        HttpUtils.getReplyById(contentId, "up", list.get(list.size() - 1).getTimeStamp())
                .enqueue(new Callback<ArrayResponse<MessageItem>>() {
                    @Override
                    public void onResponse(Call<ArrayResponse<MessageItem>> call,
                                           Response<ArrayResponse<MessageItem>> response) {
                        if(response.body() != null){
                            LogUtil.d("msg_load", response.body().toString());
                            if(response.body().isOk()){
                                List<MessageItem> list = new ArrayList<>();
                                if(response.body().getData() != null){
                                    list.addAll(response.body().getData());
                                }
                                fetchContent.refreshSuccess(list);
                            } else {
                                fetchContent.refreshFail(response.body().getCode(),
                                        response.body().getMessage());
                            }
                        } else {
                            fetchContent.refreshFail(500, "加载失败，服务器未知错误");
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayResponse<MessageItem>> call, Throwable t) {
                        t.printStackTrace();
                        fetchContent.refreshFail(408, "连接失败，请检查你的网络连接");
                    }
                });
    }

    public void deleteContent(List<MessageItem> list, final int position){
        deleteContent.deleteSuccess(position);
    }
}
