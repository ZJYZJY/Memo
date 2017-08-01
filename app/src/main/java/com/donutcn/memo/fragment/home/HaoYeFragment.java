package com.donutcn.memo.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.donutcn.memo.activity.ArticlePage;
import com.donutcn.memo.base.BaseScrollFragment;
import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.event.ReceiveNewMessagesEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.type.ItemLayoutType;
import com.donutcn.memo.view.ListViewDecoration;
import com.donutcn.memo.R;
import com.donutcn.memo.adapter.HaoYeAdapter;
import com.donutcn.memo.listener.OnItemClickListener;
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

public class HaoYeFragment extends BaseScrollFragment {

    private SwipeMenuRecyclerView mHaoYe_rv;
    public SmartRefreshLayout mRefreshLayout;

    private HaoYeAdapter mAdapter;
    private ArrayList<BriefContent> list;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_haoye, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mHaoYe_rv = (SwipeMenuRecyclerView) view.findViewById(R.id.recycler_view);
        setRecyclerView(mHaoYe_rv);
        mRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.swipe_layout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setOnLoadmoreListener(mLoadmoreListener);

        mHaoYe_rv.setLayoutManager(new LinearLayoutManager(mContext));
        mHaoYe_rv.addItemDecoration(new ListViewDecoration(mContext,
                R.dimen.item_decoration_height, 8, 8));

        // set up swipe menu.
        mHaoYe_rv.setSwipeMenuCreator(mSwipeMenuCreator);
        mHaoYe_rv.setSwipeMenuItemClickListener(mHaoYeItemClickListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    public void Refresh() {
        mAdapter = new HaoYeAdapter(mContext, list, ItemLayoutType.TYPE_TAG);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mAdapter.setFooterEnable(true);

        mHaoYe_rv.setAdapter(mAdapter);
    }

    private OnRefreshListener mRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(RefreshLayout refreshlayout) {
            refreshlayout.finishRefresh(1000);
        }
    };

    private OnLoadmoreListener mLoadmoreListener = new OnLoadmoreListener() {
        @Override
        public void onLoadmore(RefreshLayout refreshlayout) {
            refreshlayout.finishLoadmore(1000);
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
            EventBus.getDefault().post(new ReceiveNewMessagesEvent(0, position));
            startActivity(new Intent(mContext, ArticlePage.class));
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
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            // close the swipe menu
            closeable.smoothCloseMenu();

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(mContext, "list第" + adapterPosition + "; 右侧菜单第" + menuPosition,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onRequestRefreshEvent(RequestRefreshEvent event){
        if(event.getRefreshPosition() == 0){
            mHaoYe_rv.scrollToPosition(0);
            mRefreshLayout.autoRefresh(0);
        }
    }
}
