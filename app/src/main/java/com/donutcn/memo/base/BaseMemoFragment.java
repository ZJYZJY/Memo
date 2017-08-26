package com.donutcn.memo.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donutcn.memo.R;
import com.donutcn.memo.fragment.api.FetchContent;
import com.donutcn.memo.presenter.MemoPresenter;
import com.donutcn.memo.utils.CollectionUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.activity.ArticlePage;
import com.donutcn.memo.adapter.MemoAdapter;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.helper.ShareHelper;
import com.donutcn.memo.interfaces.OnItemClickListener;
import com.donutcn.memo.view.ListViewDecoration;
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

import java.util.List;

public abstract class BaseMemoFragment extends BaseScrollFragment implements FetchContent<BriefContent>,
        SwipeMenuCreator, OnSwipeMenuItemClickListener, OnItemClickListener,
        OnLoadmoreListener, OnRefreshListener {

    public SwipeMenuRecyclerView mMemo_rv;
    public SmartRefreshLayout mRefreshLayout;

    public MemoPresenter mMemoPresenter;
    public MemoAdapter mAdapter;
    public List<BriefContent> mList;
    public Context mContext;
    public boolean isLoadMore = false;
    public boolean canLoadMore = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadmoreListener(this);

        initMemoPresenter();
        mMemo_rv.setLayoutManager(new LinearLayoutManager(mContext));
        mMemo_rv.addItemDecoration(new ListViewDecoration(getContext(), R.dimen.item_decoration_height));
        mMemo_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if(lastVisibleItem + 5 >= mList.size() && mList.size() > 0){
                    if(!isLoadMore && canLoadMore){
                        isLoadMore = true;
                        mMemoPresenter.loadMore(mList);
                    }
                }
            }
        });

        // set up swipe menu.
        mMemo_rv.setSwipeMenuCreator(this);
        mMemo_rv.setSwipeMenuItemClickListener(this);
    }

    public abstract void initMemoPresenter();

    @Override
    public void refreshSuccess(List<BriefContent> list) {
//        List<BriefContent> oldList = mList;
        mList.addAll(0, list);
        mList = CollectionUtil.removeDuplicateWithOrder(mList);
//        DiffUtil.calculateDiff(new MemoDiffUtil(oldList, mList), true).dispatchUpdatesTo(mAdapter);
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.finishRefresh();
    }

    @Override
    public void refreshFail(int code, String error) {
        mRefreshLayout.finishRefresh();
        if(code == 401){

        } else if(code == 400){

        } else {
            ToastUtil.show(mContext, error + "，" + code);
        }
    }

    @Override
    public void loadMoreSuccess(List<BriefContent> list) {
//        List<BriefContent> oldList = mList;
        mList.addAll(mList.size(), list);
//        DiffUtil.calculateDiff(new MemoDiffUtil(oldList, mList), true).dispatchUpdatesTo(mAdapter);
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.finishLoadmore();
        isLoadMore = false;
    }

    @Override
    public void loadMoreFail(int code, String error) {
        mRefreshLayout.finishLoadmore();
        isLoadMore = false;
        if (code == 401) {

        } else if (code == 400) {
            canLoadMore = false;
            mRefreshLayout.setLoadmoreFinished(true);
        } else {
            ToastUtil.show(mContext, error + "，" + code);
        }
    }

    /**
     * Menu creator. Call when creates the menu.
     */
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
        }
    }

    /**
     * Menu onClickListener
     *
     * @param closeable       Used for close the menu
     * @param adapterPosition position of recyclerView item
     * @param menuPosition    position of swipe menu item
     * @param direction       left swipe menu,value：{@link SwipeMenuRecyclerView#LEFT_DIRECTION}，
     *                        right swipe menu,value：{@link SwipeMenuRecyclerView#RIGHT_DIRECTION}.
     */
    @Override
    public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
        // close the swipe menu
        closeable.smoothCloseMenu();

        if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
            if(menuPosition == 0) {
                new ShareHelper(mContext).openShareBoard(
                        mList.get(adapterPosition).getUrl(),
                        mList.get(adapterPosition).getTitle(),
                        mList.get(adapterPosition).getImage0(),
                        mList.get(adapterPosition).getContent());
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), ArticlePage.class);
        intent.putExtra("contentId", mList.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void setRecyclerView(RecyclerView recyclerView) {
        super.setRecyclerView(recyclerView);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        mMemoPresenter.refresh(mList);
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        if(mList.size() > 0 && !isLoadMore && canLoadMore){
            isLoadMore = true;
            mMemoPresenter.loadMore(mList);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
