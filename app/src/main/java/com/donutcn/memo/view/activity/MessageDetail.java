package com.donutcn.memo.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.utils.ExcelUtil;
import com.donutcn.memo.R;
import com.donutcn.memo.adapter.MessageDetailAdapter;
import com.donutcn.memo.entity.MessageItem;
import com.donutcn.memo.event.ItemActionClickEvent;
import com.donutcn.memo.interfaces.OnWriteExcelListener;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.view.api.DeleteContent;
import com.donutcn.memo.view.api.FetchContent;
import com.donutcn.memo.interfaces.OnItemClickListener;
import com.donutcn.memo.presenter.MessagePresenter;
import com.donutcn.memo.type.PublishType;
import com.donutcn.memo.utils.CollectionUtil;
import com.donutcn.memo.utils.FileCacheUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.WindowUtils;
import com.donutcn.memo.view.ListViewDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.donutcn.memo.utils.FileCacheUtil.MESSAGE_ITEM_CACHE;

public class MessageDetail extends AppCompatActivity implements OnItemClickListener,
        FetchContent<MessageItem>, DeleteContent {

    private SwipeMenuRecyclerView mMsg_rv;
    private SmartRefreshLayout mRefreshLayout;

    private MessagePresenter mMsgPresenter;
    private SwipeMenuAdapter mAdapter;
    private List<MessageItem> mList;
    private List<MessageItem> mExportList;
    private PublishType mType;

    private ExcelUtil mExcel;
    private String mContentId;
    private String mDate;
    public boolean isLoadMore = false;
    public boolean canLoadMore = true;

    public final int PAGE_SIZE = 10;
    private final String[] EXCEL_LABELS = {"用户昵称", "姓名", "手机", "微信", "简历", "邮箱", "时间"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        WindowUtils.setStatusBarColor(this, R.color.colorPrimary, true);

        EventBus.getDefault().register(this);
        String type = getIntent().getStringExtra("type");
        WindowUtils.setToolBarTitle(this, type);
        mType = PublishType.getType(type);

        mContentId = getIntent().getStringExtra("messageId");
        String title = getIntent().getStringExtra("title");
        mDate = getIntent().getStringExtra("date");
        int count = getIntent().getIntExtra("count", 0);
        mMsgPresenter = new MessagePresenter(this, mContentId);

        mList = new ArrayList<>();
        mAdapter = new MessageDetailAdapter(this, mList, type, count);
        ((MessageDetailAdapter)mAdapter).setOnItemClickListener(this);
        initView(mContentId, title, mDate, count);
    }

    public void initView(final String contentId, String title, String date, int count){
        if(mType == PublishType.ARTICLE
                || mType == PublishType.ALBUM
                || mType == PublishType.QA
                || mType == PublishType.VOTE){
            findViewById(R.id.toolbar_with_btn).setVisibility(View.GONE);
        }
        ((TextView) findViewById(R.id.detail_content_title)).setText(title);
        ((TextView) findViewById(R.id.detail_content_date)).setText(date);
        findViewById(R.id.detail_content_reference).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageDetail.this, ArticlePage.class);
                intent.putExtra("contentId", contentId);
                startActivity(intent);
            }
        });
        TextView info = (TextView) findViewById(R.id.message_brief_info);
        if(count != 0){
            info.setText(getString(R.string.placeholder_new_reply, count, mType.getReply()));
        } else {
            info.setText(getString(R.string.placeholder_no_new_reply, mType.getReply()));
        }

        mMsg_rv = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.swipe_layout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setOnLoadmoreListener(mLoadmoreListener);

        mMsg_rv.setLayoutManager(new LinearLayoutManager(this));
        mMsg_rv.addItemDecoration(new ListViewDecoration(this, R.dimen.item_decoration_height));
        mMsg_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if(lastVisibleItem + 5 >= mList.size() && mList.size() >= PAGE_SIZE){
                    if(!isLoadMore && canLoadMore){
                        isLoadMore = true;
                        mMsgPresenter.loadMore(mList);
                    }
                }
            }
        });
        String cache = FileCacheUtil.getCache(this, MESSAGE_ITEM_CACHE, FileCacheUtil.CACHE_SHORT_TIMEOUT);
        if("".equals(cache) || count > 0)
            mRefreshLayout.autoRefresh(0);
        else{
            List<MessageItem> temp = new Gson().fromJson(cache, new TypeToken<List<MessageItem>>(){}.getType());
            mList.addAll(temp);
        }
        mMsg_rv.setAdapter(mAdapter);
    }

    private OnRefreshListener mRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(RefreshLayout refreshlayout) {
            mMsgPresenter.refresh(mList);
        }
    };

    private OnLoadmoreListener mLoadmoreListener = new OnLoadmoreListener() {
        @Override
        public void onLoadmore(RefreshLayout refreshlayout) {
            if(mList.size() >= PAGE_SIZE && !isLoadMore && canLoadMore){
                isLoadMore = true;
                mMsgPresenter.loadMore(mList);
            } else if(!isLoadMore) {
                mRefreshLayout.finishLoadmore();
                mRefreshLayout.setLoadmoreFinished(true);
            }
        }
    };

    @Override
    public void onItemClick(int position) {

    }

    public void onBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onExport(View view) {
        String[] date = mDate.split(" ");
        String[] formedDate = date[0].split("-");
        final String name = formedDate[0] + "年" + formedDate[1] + "月" + formedDate[2] + "日人人记"
                + mType.toString() + "汇总";
        HttpUtils.getExportList(mContentId).enqueue(new Callback<ArrayResponse<MessageItem>>() {
            @Override
            public void onResponse(Call<ArrayResponse<MessageItem>> call,
                                   Response<ArrayResponse<MessageItem>> response) {
                if(response.body() != null){
                    if(response.body().isOk()){
                        mExportList = response.body().getData();
                        if(mExportList != null && mExportList.size() > 0){
                            try {
                                exportReply(mExportList, name, mType.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtil.show(MessageDetail.this, "导出失败，没有数据");
                        }
                    } else {
                        ToastUtil.show(MessageDetail.this, response.body().getMessage());
                    }
                } else {
                    ToastUtil.show(MessageDetail.this, "导出失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<ArrayResponse<MessageItem>> call, Throwable t) {
                t.printStackTrace();
                ToastUtil.show(MessageDetail.this, "导出失败，连接失败");
            }
        });
    }

    public void exportReply(List<MessageItem> data, String fileName, String sheetName) throws Exception {
        mExcel = ExcelUtil.with(this)
                .setData(data)
                .setFileName(fileName)
                .setSheetName(sheetName)
                .setLabels(EXCEL_LABELS)
                .setOnWriteExcelListener(new OnWriteExcelListener() {
                    @Override
                    public void onWriteExcel(WritableSheet sheet, List<MessageItem> list) throws WriteException {
                        for (int i = 0; i < list.size(); i++) {
                            MessageItem item = list.get(i);
                            sheet.addCell(new Label(0, i + 1, item.getName()));
                            sheet.addCell(new Label(1, i + 1, item.getRealName()));
                            sheet.addCell(new Label(2, i + 1, item.getPhone()));
                            sheet.addCell(new Label(3, i + 1, item.getWeChat()));
                            sheet.addCell(new Label(4, i + 1, item.getResume()));
                            sheet.addCell(new Label(5, i + 1, item.getEmail()));
                            sheet.addCell(new Label(6, i + 1, item.getTime()));
                            ToastUtil.show(MessageDetail.this, "成功导出到" + ExcelUtil.PATH);
                        }
                    }
                })
                .build();
    }

    @Override
    public void refreshSuccess(List<MessageItem> list) {
        mList.addAll(0, list);
        mList = CollectionUtil.removeDuplicateWithOrder(mList);
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.finishRefresh();
//        FileCacheUtil.setMessageItemCache(this, new Gson().toJson(mList));
    }

    @Override
    public void refreshFail(int code, String error) {
        mRefreshLayout.finishRefresh();
        if(code == 401){

        } else if(code == 400){

        } else {
            ToastUtil.show(this, error + "，" + code);
        }
    }

    @Override
    public void loadMoreSuccess(List<MessageItem> list) {
        mList.addAll(mList.size(), list);
        mAdapter.notifyDataSetChanged();
        mRefreshLayout.finishLoadmore();
        isLoadMore = false;
//        FileCacheUtil.setMessageItemCache(this, new Gson().toJson(mList));
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
            ToastUtil.show(this, error + "，" + code);
        }
    }

    @Override
    public void deleteSuccess(int position) {
        mList.remove(position);
        mAdapter.notifyItemRemoved(position);
        ToastUtil.show(this, "删除成功");
//        FileCacheUtil.setMessageItemCache(this, new Gson().toJson(mList));
    }

    @Override
    public void deleteFail(int code, String error) {
        if(code == 401){

        } else {
            ToastUtil.show(this, error + "，" + code);
        }
    }

    @Subscribe
    public void onItemActionClickEvent(ItemActionClickEvent event){
        if(event.type == 0){
            mMsgPresenter.deleteContent(mList, event.position);
        } else if(event.type == 1){
            mMsgPresenter.downloadResume(mList, event.position);
        }
    }
}
