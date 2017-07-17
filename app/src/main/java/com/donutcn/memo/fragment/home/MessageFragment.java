package com.donutcn.memo.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.donutcn.memo.R;
import com.donutcn.memo.activity.InteractivePage;
import com.donutcn.memo.adapter.HaoYeAdapter;
import com.donutcn.memo.adapter.MessageAdapter;
import com.donutcn.memo.adapter.SimpleFragmentPagerAdapter;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.type.ItemLayoutType;
import com.donutcn.memo.view.ListViewDecoration;
import com.flyco.tablayout.SlidingTabLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context mContext;

    private SwipeMenuRecyclerView mMessage_rv;

    private SwipeRefreshLayout mRefreshLayout;

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
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mMessage_rv = (SwipeMenuRecyclerView) view.findViewById(R.id.recycler_view);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mRefreshLayout.setOnRefreshListener(this);

        mMessage_rv.setLayoutManager(new LinearLayoutManager(mContext));
        mMessage_rv.addItemDecoration(new ListViewDecoration(mContext,
                R.dimen.item_decoration, 0, 0));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Refresh();
    }

    public void update(){
        Refresh();
    }

    public void Refresh(){
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            dataList.add("我是第" + i + "个。");
        }
        MessageAdapter adapter = new MessageAdapter(dataList, ItemLayoutType.TYPE_TAG);
        adapter.setOnItemClickListener(mOnItemClickListener);

        mMessage_rv.setAdapter(adapter);

        mRefreshLayout.setRefreshing(false);
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Toast.makeText(mContext, "我是第" + position + "条。", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRefresh() {
        Refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        Refresh();
    }
}
