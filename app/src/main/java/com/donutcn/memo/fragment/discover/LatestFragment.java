package com.donutcn.memo.fragment.discover;

import com.donutcn.memo.base.BaseMemoFragment;
import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.event.ChangeContentEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.utils.CollectionUtil;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.ToastUtil;

import org.greenrobot.eventbus.Subscribe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LatestFragment extends BaseMemoFragment {

    @Override
    public void Refresh() {
        HttpUtils.getContentList(2, "down", mList.size() == 0 ? 0 : mList.get(0).getTimeStamp())
                .enqueue(new Callback<ArrayResponse<BriefContent>>() {
                    @Override
                    public void onResponse(Call<ArrayResponse<BriefContent>> call,
                                           Response<ArrayResponse<BriefContent>> response) {
                        if(response.body() != null){
                            LogUtil.d("refresh", response.body().toString());
                            if(response.body().isOk()){
                                mList.addAll(0, response.body().getData());
                                mList = CollectionUtil.removeDuplicateWithOrder(mList);
                                mAdapter.setDataSet(mList);
//                                mAdapter.notifyDataSetChanged();
                                mMemo_rv.setAdapter(mAdapter);
                            }
                        }
                        mRefreshLayout.finishRefresh();
                    }

                    @Override
                    public void onFailure(Call<ArrayResponse<BriefContent>> call, Throwable t) {
                        t.printStackTrace();
                        ToastUtil.show(getContext(), "最新连接失败");
                        mRefreshLayout.finishRefresh();
                    }
                });
    }

    @Override
    public void LoadMore(){
        HttpUtils.getContentList(2, "up", mList.get(mList.size() - 1).getTimeStamp())
                .enqueue(new Callback<ArrayResponse<BriefContent>>() {
                    @Override
                    public void onResponse(Call<ArrayResponse<BriefContent>> call,
                                           Response<ArrayResponse<BriefContent>> response) {
                        if(response.body() != null){
                            LogUtil.d("load", response.body().toString());
                            if(response.body().isOk()){
                                mList.addAll(mList.size(), response.body().getData());
                                mAdapter.setDataSet(mList);
                                mAdapter.notifyDataSetChanged();
                                mRefreshLayout.finishLoadmore();
                            }else if(response.body().unAuthorized()){

                            } else if(response.body().isFail()) {
//                                ToastUtil.show(getContext(), "已经到底部了");
                                canLoadMore = false;
                                mRefreshLayout.finishLoadmore();
                                mRefreshLayout.setLoadmoreFinished(true);
                            }
                        }
                        isLoadMore = false;
                        mRefreshLayout.finishLoadmore();
                    }

                    @Override
                    public void onFailure(Call<ArrayResponse<BriefContent>> call, Throwable t) {
                        t.printStackTrace();
                        ToastUtil.show(getContext(), "最新连接失败");
                        isLoadMore = false;
                        mRefreshLayout.finishLoadmore();
                    }
                });
    }

    @Subscribe
    public void onRequestRefreshEvent(RequestRefreshEvent event){
        if(event.getRefreshPosition() == 3){
            mMemo_rv.scrollToPosition(0);
            mRefreshLayout.autoRefresh(0);
        }
    }

    @Subscribe(sticky = true)
    public void onChangeContentEvent(ChangeContentEvent event){
        if(event.getType() == 0){
            String contentId = event.getId();
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getId().equals(contentId)) {
                    mList.remove(i);
                    mAdapter.notifyItemRemoved(i);
                    break;
                }
            }
        }
    }
}
