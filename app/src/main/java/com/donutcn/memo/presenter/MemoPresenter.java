package com.donutcn.memo.presenter;

import android.support.v4.app.Fragment;

import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.BriefContent;
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
 * Created by 73958 on 2017/8/21.
 */

public class MemoPresenter {

    private FetchContent fetchContent;
    private DeleteContent deleteContent;
    private int position;

    public MemoPresenter(Fragment fragment, int position){
        this.fetchContent = (FetchContent) fragment;
        if(fragment instanceof DeleteContent){
            this.deleteContent = (DeleteContent) fragment;
        }
        this.position = position;
    }

    public void refresh(final List<BriefContent> list){
        HttpUtils.getContentList(position, "down", list.size() == 0 ? 0 : list.get(0).getTimeStamp())
                .enqueue(new Callback<ArrayResponse<BriefContent>>() {
                    @Override
                    public void onResponse(Call<ArrayResponse<BriefContent>> call,
                                           Response<ArrayResponse<BriefContent>> response) {
                        if(response.body() != null){
                            LogUtil.d("refresh", response.body().toString());
                            if(response.body().isOk()){
                                List<BriefContent> list = new ArrayList<>();
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
                    public void onFailure(Call<ArrayResponse<BriefContent>> call, Throwable t) {
                        t.printStackTrace();
                        fetchContent.refreshFail(408, "连接失败，请检查你的网络连接");
                    }
                });
    }

    public void loadMore(List<BriefContent> list){
        HttpUtils.getContentList(position, "up", list.get(list.size() - 1).getTimeStamp())
                .enqueue(new Callback<ArrayResponse<BriefContent>>() {
                    @Override
                    public void onResponse(Call<ArrayResponse<BriefContent>> call,
                                           Response<ArrayResponse<BriefContent>> response) {
                        if(response.body() != null){
                            LogUtil.d("load", response.body().toString());
                            if(response.body().isOk()){
                                List<BriefContent> list = new ArrayList<>();
                                if(response.body().getData() != null){
                                    list.addAll(response.body().getData());
                                }
                                fetchContent.loadMoreSuccess(list);
                            } else {
                                fetchContent.loadMoreFail(response.body().getCode(),
                                        response.body().getMessage());
                            }
                        } else {
                            fetchContent.loadMoreFail(500, "刷新失败，服务器未知错误");
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayResponse<BriefContent>> call, Throwable t) {
                        t.printStackTrace();
                        fetchContent.loadMoreFail(408, "连接失败，请检查你的网络连接");
                    }
                });
    }

    public void deleteContent(List<BriefContent> list, final int position){
        HttpUtils.deleteContent(list.get(position).getId()).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                LogUtil.d("delete", response.body().getMessage());
                if(response.body() != null){
                    if(response.body().isOk()){
                        deleteContent.deleteSuccess(position);
                    } else {
                        deleteContent.deleteFail(response.body().getCode(),
                                response.body().getMessage());
                    }
                }else {
                    deleteContent.deleteFail(500, "删除失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                deleteContent.deleteFail(508, "连接失败，请检查你的网络连接");
            }
        });
    }
}
