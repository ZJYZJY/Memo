package com.donutcn.memo.fragment.discover;

import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donutcn.memo.R;
import com.donutcn.memo.adapter.FriendListAdapter;
import com.donutcn.memo.base.BaseScrollFragment;
import com.donutcn.memo.entity.BriefContent;
import com.donutcn.memo.entity.Contact;
import com.donutcn.memo.event.ReceiveNewMessagesEvent;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.listener.OnItemClickListener;
import com.donutcn.memo.listener.OnQueryListener;
import com.donutcn.memo.utils.PermissionCheck;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.view.ListViewDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsFragment extends BaseScrollFragment {

    private SwipeMenuRecyclerView mHaoYe_rv;
    private SmartRefreshLayout mRefreshLayout;
    private View mContainer, mNoMatch;

    private FriendListAdapter mAdapter;
    private ArrayList<BriefContent> mList;
    private static ArrayList<Contact> mContactsList;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.start_match).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMatch(v);
            }
        });
        mContainer = view.findViewById(R.id.unmatch_container);
        mNoMatch = view.findViewById(R.id.no_matched_contact);

        mHaoYe_rv = (SwipeMenuRecyclerView) view.findViewById(R.id.recycler_view);
        setRecyclerView(mHaoYe_rv);
        mRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.swipe_layout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setOnLoadmoreListener(mLoadmoreListener);

        mHaoYe_rv.setLayoutManager(new LinearLayoutManager(mContext));
        mHaoYe_rv.addItemDecoration(new ListViewDecoration(getContext(), R.dimen.item_decoration_height));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Refresh();
    }

    public void Refresh() {
        mAdapter = new FriendListAdapter(mContext, mContactsList);
        mAdapter.setOnItemClickListener(mOnItemClickListener);

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

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            EventBus.getDefault().post(new ReceiveNewMessagesEvent(3, position));
        }
    };

    public void startMatch(View view){
//        PermissionCheck check = new PermissionCheck(mContext);
//        if(check.checkContactsPermission()){
//            getContactsAsync();
//        }else {
//            ToastUtil.show(mContext, "未授权读取联系人");
//        }
        if(PermissionCheck.checkContactsPermission(this)){
            getContactsAsync();
        }else {
            ToastUtil.show(mContext, "未授权读取联系人");
        }
    }

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
        if(event.getRefreshPosition() == 3){
            mHaoYe_rv.scrollToPosition(0);
            mRefreshLayout.autoRefresh(0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionCheck.PERMISSION_READ_CONTACTS) {

        }
    }

    private void getContactsAsync() {
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setTitle("正在匹配联系人");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ContactsQueryHandler qh = new ContactsQueryHandler(getContext().getContentResolver(),
                new OnQueryListener<Contact>() {
                    @Override
                    public void onQueryProgress(int progress, int total) {
                        dialog.setProgress(progress);
                    }

                    @Override
                    public void onQueryComplete(List<Contact> list) {
                        dialog.dismiss();
                        ToastUtil.show(mContext, "匹配完成");
                        mAdapter = new FriendListAdapter(mContext, mContactsList);
                        mAdapter.setOnItemClickListener(mOnItemClickListener);
                        mHaoYe_rv.setAdapter(mAdapter);
                        if(mContactsList.size() > 0){
                            mRefreshLayout.setVisibility(View.VISIBLE);
                            mNoMatch.setVisibility(View.GONE);
                            mContainer.setVisibility(View.GONE);
                        }else {
                            mRefreshLayout.setVisibility(View.GONE);
                            mNoMatch.setVisibility(View.VISIBLE);
                            mContainer.setVisibility(View.GONE);
                        }
                    }
                });
        // query field
        String[] projection = {ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY};
        qh.startQuery(0, null, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null,
                "sort_key COLLATE LOCALIZED asc");
    }

    static class ContactsQueryHandler extends AsyncQueryHandler {

        private OnQueryListener<Contact> listener;

        public ContactsQueryHandler(ContentResolver cr, OnQueryListener<Contact> listener) {
            super(cr);
            this.listener = listener;
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                Map<Integer, Contact> contactIdMap = new HashMap<>();
                mContactsList = new ArrayList<>();
                cursor.moveToFirst();
                int size = cursor.getCount();
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    int Id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    number = number.replace(" ", "");
                    String sortKey = cursor.getString(3);
                    int contactId = cursor.getInt(4);
                    Long photoId = cursor.getLong(5);
                    String lookUpKey = cursor.getString(6);

                    if (contactIdMap.containsKey(contactId)) {
                        // do nothing
                    } else {
                        // create a contact object.
                        Contact contact = new Contact();
                        contact.setDesplayName(name);
                        contact.setPhoneNum(number);
                        contact.setSortKey(sortKey);
                        contact.setContactId(contactId);
                        contact.setPhotoId(photoId);
                        contact.setLookUpKey(lookUpKey);
                        mContactsList.add(contact);

                        contactIdMap.put(contactId, contact);
                    }
                    listener.onQueryProgress(i / size * 100, size);
                }
                cursor.close();
                listener.onQueryComplete(mContactsList);
            }
            super.onQueryComplete(token, cookie, cursor);
        }
    }
}
