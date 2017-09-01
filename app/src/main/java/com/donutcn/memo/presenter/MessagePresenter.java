package com.donutcn.memo.presenter;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;

import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.MessageItem;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.view.api.DeleteContent;
import com.donutcn.memo.view.api.FetchContent;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.ToastUtil;

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
    private Context mContext;
    private String contentId;

    public MessagePresenter(Context context, String contentId){
        this.fetchContent = (FetchContent) context;
        this.deleteContent = (DeleteContent) context;
        this.mContext = context;
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
        String replyId;
        boolean isMessage = list.get(position).isMessage();
        if(isMessage)
            replyId = list.get(position).getMessageId();
        else
            replyId = list.get(position).getCommentId();

        HttpUtils.deleteReply(contentId, replyId, isMessage ? "message_id" : "comment_id")
                .enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body() != null){
                    LogUtil.d("msg_del", response.body().toString());
                    if(response.body().isOk()){
                        deleteContent.deleteSuccess(position);
                    } else {
                        deleteContent.deleteFail(response.body().getCode(),
                                response.body().getMessage());
                    }
                } else {
                    deleteContent.deleteFail(500, "删除失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                deleteContent.deleteFail(408, "连接失败，请检查你的网络连接");
            }
        });
    }

    public void downloadResume(List<MessageItem> list, int position){
        MessageItem messageItem = list.get(position);
        String[] parts = messageItem.getResume().split("\\.");
        String postfix = parts[parts.length -1];
        //创建下载任务,downloadUrl就是下载链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(messageItem.getResume()));
        request.setDestinationInExternalPublicDir("/com.donutcn.memo/resume", messageItem.getRealName() + "的简历." + postfix);
        // set cookies for download request
        request.addRequestHeader("Cookie", HttpUtils.cookieHeader());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);

        final DownloadManager downloadManager= (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载任务加入下载队列
        final long taskId = downloadManager.enqueue(request);
        ToastUtil.show(mContext, "开始下载...");
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(taskId);//筛选下载任务，传入任务ID，可变参数
                Cursor c = downloadManager.query(query);
                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    switch (status) {
                        case DownloadManager.STATUS_PAUSED:
                            LogUtil.i(">>>下载暂停");
                        case DownloadManager.STATUS_PENDING:
                            LogUtil.i(">>>下载延迟");
                        case DownloadManager.STATUS_RUNNING:
                            LogUtil.i(">>>正在下载");
                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            LogUtil.i(">>>下载完成");
                            ToastUtil.show(mContext, "下载完成");
                            break;
                        case DownloadManager.STATUS_FAILED:
                            LogUtil.e(">>>下载失败");
                            ToastUtil.show(mContext, "下载失败");
                            break;
                    }
                }
            }
        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}
