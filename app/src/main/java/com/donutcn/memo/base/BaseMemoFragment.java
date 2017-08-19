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
import com.donutcn.memo.activity.ArticlePage;
import com.donutcn.memo.adapter.MemoAdapter;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.helper.ShareHelper;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.type.ItemLayoutType;
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

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMemoFragment extends BaseScrollFragment {

    public SwipeMenuRecyclerView mMemo_rv;
    public SmartRefreshLayout mRefreshLayout;

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
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setOnLoadmoreListener(mLoadmoreListener);

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
                        LoadMore();
                    }
                }
            }
        });

        // set up swipe menu.
        mMemo_rv.setSwipeMenuCreator(mSwipeMenuCreator);
        mMemo_rv.setSwipeMenuItemClickListener(mMemoItemClickListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        mList = new ArrayList<>();
        mAdapter = new MemoAdapter(mContext, mList, ItemLayoutType.AVATAR_IMG);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mMemo_rv.setAdapter(mAdapter);
    }

    public abstract void Refresh();

    public abstract void LoadMore();

    public OnRefreshListener mRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(RefreshLayout refreshlayout) {
            Refresh();
        }
    };

    public OnLoadmoreListener mLoadmoreListener = new OnLoadmoreListener() {
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
    public SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
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
    };

    public OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Intent intent = new Intent(getContext(), ArticlePage.class);
            intent.putExtra("contentId", mList.get(position).getId());
            intent.putExtra("userId", mList.get(position).getUserId());
            intent.putExtra("url", mList.get(position).getUrl());
            intent.putExtra("name", mList.get(position).getName());
            intent.putExtra("userIcon", mList.get(position).getUserIcon());
            intent.putExtra("type", mList.get(position).getType());
            intent.putExtra("upvote", mList.get(position).getUpVote());
            intent.putExtra("comment", mList.get(position).getComment());
            startActivity(intent);
        }
    };

    /**
     * Menu onClickListener
     */
    public OnSwipeMenuItemClickListener mMemoItemClickListener
            = new OnSwipeMenuItemClickListener() {
        /**
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
    };

    @Override
    public void onResume() {
        super.onResume();
//        Refresh();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
