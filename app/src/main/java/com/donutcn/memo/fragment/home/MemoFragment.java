package com.donutcn.memo.fragment.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donutcn.memo.activity.ArticlePage;
import com.donutcn.memo.activity.PublishActivity;
import com.donutcn.memo.base.BaseScrollFragment;
import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.entity.SimpleResponse;
import com.donutcn.memo.event.ReceiveNewMessagesEvent;
import com.donutcn.memo.event.ChangeContentEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.helper.ShareHelper;
import com.donutcn.memo.type.ItemLayoutType;
import com.donutcn.memo.utils.CollectionUtil;
import com.donutcn.memo.utils.FileCacheUtil;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.donutcn.memo.view.ListViewDecoration;
import com.donutcn.memo.R;
import com.donutcn.memo.adapter.MemoAdapter;
import com.donutcn.memo.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemoFragment extends BaseScrollFragment {

    private SwipeMenuRecyclerView mMemo_rv;
    public SmartRefreshLayout mRefreshLayout;

    private MemoAdapter mAdapter;
    private List<BriefContent> mList;
    private Context mContext;
    private boolean isLoadMore = false;
    private boolean canLoadMore = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_memo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mMemo_rv = (SwipeMenuRecyclerView) view.findViewById(R.id.recycler_view);
        setRecyclerView(mMemo_rv);
        mRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.swipe_layout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setOnLoadmoreListener(mLoadmoreListener);
//        mRefreshLayout.setEnableLoadmore(true);

        mMemo_rv.setLayoutManager(new LinearLayoutManager(mContext));
        mMemo_rv.addItemDecoration(new ListViewDecoration(mContext, R.dimen.item_decoration_height));
        // preload data.
        mMemo_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if(lastVisibleItem + 5 >= mList.size() && mList.size() > 0){
                    if(!isLoadMore && canLoadMore){
                        isLoadMore = true;
                        LoadMore();
                    }
                }
            }
        });

        // set up swipe menu.
        mMemo_rv.setSwipeMenuCreator(mSwipeMenuCreator);
        mMemo_rv.setSwipeMenuItemClickListener(mHaoYeItemClickListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mList = new ArrayList<>();
        mAdapter = new MemoAdapter(mContext, mList, ItemLayoutType.TYPE_TAG);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mMemo_rv.setAdapter(mAdapter);
        if(UserStatus.isLogin(mContext)){
            String cache = FileCacheUtil.getCache(mContext, "docs_cache.txt", FileCacheUtil.CACHE_LONG_TIMEOUT);
            if(cache.equals(""))
                Refresh();
            else{
                mList = new Gson().fromJson(cache, new TypeToken<List<BriefContent>>(){}.getType());
                mAdapter.setDataSet(mList);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void Refresh() {
        HttpUtils.getContentList(0, "down", mList.size() == 0 ? 0 : mList.get(0).getTimeStamp())
                .enqueue(new Callback<ArrayResponse<BriefContent>>() {
            @Override
            public void onResponse(Call<ArrayResponse<BriefContent>> call, Response<ArrayResponse<BriefContent>> response) {
                if(response.body() != null){
                    LogUtil.d("refresh", response.body().toString());
                    if(response.body().isOk()){
                        mList.addAll(0, response.body().getData());
                        mList = CollectionUtil.removeDuplicateWithOrder(mList);
                        mAdapter.setDataSet(mList);
//                        mAdapter.notifyDataSetChanged();
                        mMemo_rv.setAdapter(mAdapter);
                        FileCacheUtil.setCache(mContext, new Gson().toJson(mList));
                    }
                }
                mRefreshLayout.finishRefresh();
            }

            @Override
            public void onFailure(Call<ArrayResponse<BriefContent>> call, Throwable t) {
                t.printStackTrace();
                ToastUtil.show(mContext, "连接失败");
                mRefreshLayout.finishRefresh();
            }
        });
    }

    public void LoadMore() {
        HttpUtils.getContentList(0, "up", mList.get(mList.size() - 1).getTimeStamp())
                .enqueue(new Callback<ArrayResponse<BriefContent>>() {
            @Override
            public void onResponse(Call<ArrayResponse<BriefContent>> call, Response<ArrayResponse<BriefContent>> response) {
                if(response.body() != null){
                    LogUtil.d("load", response.body().toString());
                    if(response.body().isOk()){
                        mList.addAll(mList.size(), response.body().getData());
                        mAdapter.setDataSet(mList);
                        mAdapter.notifyDataSetChanged();
                        mRefreshLayout.finishLoadmore();
                        FileCacheUtil.setCache(mContext, new Gson().toJson(mList));
                    } else if(response.body().unAuthorized()){

                    } else if(response.body().isFail()) {
//                        ToastUtil.show(mContext, "已经到底部了");
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
                ToastUtil.show(mContext, "连接失败");
                isLoadMore = false;
                mRefreshLayout.finishLoadmore();
            }
        });
    }

    private OnRefreshListener mRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(RefreshLayout refreshlayout) {
            Refresh();
        }
    };

    private OnLoadmoreListener mLoadmoreListener = new OnLoadmoreListener() {
        @Override
        public void onLoadmore(RefreshLayout refreshlayout) {
            if(mList.size() > 0 && !isLoadMore && canLoadMore){
                isLoadMore = true;
                LoadMore();
            }
        }
    };

    /**
     * Menu creator. Call when creates the menu.
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
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
    };

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            EventBus.getDefault().postSticky(new ReceiveNewMessagesEvent(0, position));
            Intent intent = new Intent(mContext, ArticlePage.class);
            intent.putExtra("contentId", mList.get(position).getId());
            intent.putExtra("self", true);
            startActivity(intent);
        }
    };

    /**
     * Menu onClickListener
     */
    private OnSwipeMenuItemClickListener mHaoYeItemClickListener
            = new OnSwipeMenuItemClickListener() {
        /**
         * @param closeable       Used for close the menu
         * @param adapterPosition position of recyclerView item
         * @param menuPosition    position of swipe menu item
         * @param direction       left swipe menu,value：{@link SwipeMenuRecyclerView#LEFT_DIRECTION}，
         *                        right swipe menu,value：{@link SwipeMenuRecyclerView#RIGHT_DIRECTION}.
         */
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
                                        deleteContent(adapterPosition);
                                    }
                                })
                                .setNegativeButton(getString(R.string.dialog_publish_neg), null)
                                .setCancelable(true)
                                .show();
                        break;
                }
            }
//            SpfsUtils.write(mContext, SpfsUtils.CACHE, "publishType", mSelectedType);
//            SpfsUtils.write(mContext, SpfsUtils.CACHE, "publishTitle", mTitleStr);
//            SpfsUtils.write(mContext, SpfsUtils.CACHE, "publishContent", mContentStr);
        }
    };

    private void deleteContent(final int position){
        HttpUtils.deleteContent(mList.get(position).getId()).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                LogUtil.d("delete", response.body().getMessage());
                if(response.body() != null){
                    if(response.body().isOk()){
                        EventBus.getDefault().postSticky(new ChangeContentEvent(mList.get(position).getId(), 0));
                        mList.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        ToastUtil.show(mContext, "删除成功");
                        FileCacheUtil.setCache(mContext, new Gson().toJson(mList));
                    }
                }else {
                    ToastUtil.show(mContext, "删除失败");
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                t.printStackTrace();
                ToastUtil.show(mContext, "删除连接失败");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Refresh();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onRequestRefreshEvent(RequestRefreshEvent event){
        if(event.getRefreshPosition() == 0){
            mMemo_rv.scrollToPosition(0);
            mRefreshLayout.autoRefresh(0);
        }
    }
}
