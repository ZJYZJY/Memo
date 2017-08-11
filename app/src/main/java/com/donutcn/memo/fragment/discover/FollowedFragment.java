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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowedFragment extends BaseMemoFragment {

    @Override
    public void Refresh() {
        HttpUtils.getContentList(3, "down", mList.size() == 0 ? 0 : mList.get(0).getTimeStamp())
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
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                        mRefreshLayout.finishRefresh();
                    }

                    @Override
                    public void onFailure(Call<ArrayResponse<BriefContent>> call, Throwable t) {
                        t.printStackTrace();
                        ToastUtil.show(getContext(), "关注连接失败");
                        mRefreshLayout.finishRefresh();
                    }
                });
    }

    @Override
    public void LoadMore(){
        HttpUtils.getContentList(3, "up", mList.get(mList.size() - 1).getTimeStamp())
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
                                ToastUtil.show(getContext(), "已经到底部了");
                                mRefreshLayout.finishLoadmore();
                                mRefreshLayout.setLoadmoreFinished(true);
                            }
                        }
                        mRefreshLayout.finishLoadmore();
                    }

                    @Override
                    public void onFailure(Call<ArrayResponse<BriefContent>> call, Throwable t) {
                        t.printStackTrace();
                        ToastUtil.show(getContext(), "关注连接失败");
                        mRefreshLayout.finishLoadmore();
                    }
                });
    }

    @Subscribe
    public void onRequestRefreshEvent(RequestRefreshEvent event){
        if(event.getRefreshPosition() == 4){
            mMemo_rv.scrollToPosition(0);
            mRefreshLayout.autoRefresh(0);
        }
    }

    @Subscribe(sticky = true)
    public void onChangeContentEvent(ChangeContentEvent event) {
        if (event.getType() == 0) {
            String contentId = event.getId();
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getId().equals(contentId)) {
                    mList.remove(i);
                    mAdapter.notifyItemRemoved(i);
                    break;
                }
            }
        } else if (event.getType() == 1) {
            String userId = event.getId();
            List<BriefContent> toBeRemoved = new ArrayList<>();
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getUserId().equals(userId)) {
                    toBeRemoved.add(mList.get(i));
                }
            }
            mList.removeAll(toBeRemoved);
            mAdapter.notifyDataSetChanged();
        } else if (event.getType() == 2) {
            mList.clear();
            mRefreshLayout.autoRefresh(0);
        }
    }
}
