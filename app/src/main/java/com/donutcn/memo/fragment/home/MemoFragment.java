package com.donutcn.memo.fragment.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.donutcn.memo.base.BaseMemoFragment;
import com.donutcn.memo.fragment.api.DeleteContent;
import com.donutcn.memo.fragment.api.FetchContent;
import com.donutcn.memo.presenter.MemoPresenter;
import com.donutcn.memo.activity.ArticlePage;
import com.donutcn.memo.activity.PublishActivity;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.event.ChangeContentEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.helper.ShareHelper;
import com.donutcn.memo.type.ItemLayoutType;
import com.donutcn.memo.utils.FileCacheUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.donutcn.memo.R;
import com.donutcn.memo.adapter.MemoAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.donutcn.memo.utils.FileCacheUtil.CONTENT_LIST_CACHE;

public class MemoFragment extends BaseMemoFragment implements FetchContent<BriefContent>, DeleteContent {

    @Override
    public void initMemoPresenter() {
        mMemoPresenter = new MemoPresenter(this, 0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        mList = new ArrayList<>();
        mAdapter = new MemoAdapter(mContext, mList, ItemLayoutType.TYPE_TAG);
        mAdapter.setOnItemClickListener(this);
        mMemo_rv.setAdapter(mAdapter);
        if(UserStatus.isLogin(mContext)){
            String cache = FileCacheUtil.getCache(mContext, CONTENT_LIST_CACHE, FileCacheUtil.CACHE_SHORT_TIMEOUT);
            if("".equals(cache))
                mMemoPresenter.refresh(mList);
            else{
                List<BriefContent> temp = new Gson().fromJson(cache, new TypeToken<List<BriefContent>>(){}.getType());
                mList.addAll(temp);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void refreshSuccess(List<BriefContent> list) {
        super.refreshSuccess(list);
        FileCacheUtil.setContentCache(mContext, new Gson().toJson(mList));
    }

    @Override
    public void loadMoreSuccess(List<BriefContent> list) {
        super.loadMoreSuccess(list);
        FileCacheUtil.setContentCache(mContext, new Gson().toJson(mList));
    }

    @Override
    public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
        int width = getResources().getDimensionPixelSize(R.dimen.swipe_menu_item_width);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // Add the swipe menu on the right
        {
            SwipeMenuItem editItem = new SwipeMenuItem(mContext)
                    .setBackgroundDrawable(R.drawable.selector_gray)
                    .setText(getResources().getString(R.string.btn_swipe_share))
                    .setTextColor(Color.WHITE)
                    .setTextSize(16)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(editItem);

            SwipeMenuItem shareItem = new SwipeMenuItem(mContext)
                    .setBackgroundDrawable(R.drawable.selector_blue)
                    .setText(getResources().getString(R.string.btn_swipe_edit))
                    .setTextColor(Color.WHITE)
                    .setTextSize(16)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(shareItem);

            SwipeMenuItem delItem = new SwipeMenuItem(mContext)
                    .setBackgroundDrawable(R.drawable.selector_red)
                    .setText(getResources().getString(R.string.btn_swipe_delete))
                    .setTextColor(Color.WHITE)
                    .setTextSize(16)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(delItem);
        }
    }

    @Override
    public void onItemClick(Closeable closeable, final int adapterPosition, int menuPosition, int direction) {
        // close the swipe menu
        closeable.smoothCloseMenu();

        if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
            switch (menuPosition) {
                case 0:
                    new ShareHelper(mContext).openShareBoard(
                            mList.get(adapterPosition).getUrl(),
                            mList.get(adapterPosition).getTitle(),
                            mList.get(adapterPosition).getImage0(),
                            mList.get(adapterPosition).getContent());
                    break;
                case 1:
                    Intent intent = new Intent(mContext, PublishActivity.class);
                    intent.putExtra("editMode", true);
                    intent.putExtra("contentId", mList.get(adapterPosition).getId());
                    startActivity(intent);
                    break;
                case 2:
                    new AlertDialog.Builder(mContext)
                            .setMessage(getString(R.string.dialog_del_publish_content))
                            .setPositiveButton(getString(R.string.dialog_publish_pos), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mMemoPresenter.deleteContent(mList, adapterPosition);
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_publish_neg), null)
                            .setCancelable(true)
                            .show();
                    break;
            }
        }
    }

    @Override
    public void deleteSuccess(int position) {
        EventBus.getDefault().postSticky(new ChangeContentEvent(mList.get(position).getId(), 0));
        mList.remove(position);
        mAdapter.notifyItemRemoved(position);
        ToastUtil.show(mContext, "删除成功");
        FileCacheUtil.setContentCache(mContext, new Gson().toJson(mList));
    }

    @Override
    public void deleteFail(int code, String error) {
        if(code == 401){

        } else {
            ToastUtil.show(mContext, error + "，" + code);
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(mContext, ArticlePage.class);
        intent.putExtra("contentId", mList.get(position).getId());
        intent.putExtra("self", true);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMemoPresenter.refresh(mList);
    }

    @Subscribe
    public void onRequestRefreshEvent(RequestRefreshEvent event){
        if(event.getRefreshPosition() == 0){
            mMemo_rv.scrollToPosition(0);
            mRefreshLayout.autoRefresh(0);
        }
    }
}
