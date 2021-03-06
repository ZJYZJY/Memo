package com.donutcn.memo.view.fragment.discover;

import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.donutcn.memo.App;
import com.donutcn.memo.R;
import com.donutcn.memo.adapter.MemoAdapter;
import com.donutcn.memo.base.BaseMemoFragment;
import com.donutcn.memo.presenter.MemoPresenter;
import com.donutcn.memo.type.ItemLayoutType;
import com.donutcn.memo.view.activity.ArticlePage;
import com.donutcn.memo.view.activity.AuthorPage;
import com.donutcn.memo.entity.ArrayResponse;
import com.donutcn.memo.entity.Contact;
import com.donutcn.memo.ContactDao;
import com.donutcn.memo.DaoSession;
import com.donutcn.memo.event.RequestRefreshEvent;
import com.donutcn.memo.interfaces.OnItemClickListener;
import com.donutcn.memo.interfaces.OnQueryListener;
import com.donutcn.memo.interfaces.UploadCallback;
import com.donutcn.memo.utils.HttpUtils;
import com.donutcn.memo.utils.PermissionCheck;
import com.donutcn.memo.utils.SpfsUtils;
import com.donutcn.memo.utils.StringUtil;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.donutcn.memo.view.fragment.home.MemoFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsFragment extends BaseMemoFragment {

    private View mContainer, mNoMatch;

    private static List<Contact> mTempList;
    private static List<Contact> mContactsList;
    private ContactDao mContactDao;
    private boolean hasMatched;

    @Override
    public void initMemoPresenter() {
        mMemoPresenter = new MemoPresenter(this, 4);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaoSession daoSession = ((App)mContext.getApplicationContext()).getDaoSession();
        mContactDao = daoSession.getContactDao();
        hasMatched = SpfsUtils.readBoolean(mContext, SpfsUtils.USER, "match_contacts", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.start_match).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMatch();
            }
        });
        mContainer = view.findViewById(R.id.unmatch_container);
        mNoMatch = view.findViewById(R.id.no_matched_contact);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        mContactsList = new ArrayList<>();
        mList = new ArrayList<>();
        // check whether had been matched contact list.
        if(hasMatched){
            mContactsList = mContactDao
                    .queryBuilder()
                    .orderAsc(ContactDao.Properties.SortKey)
                    .build().list();
            mAdapter = new MemoAdapter(mContext, mList, ItemLayoutType.AVATAR_IMG);
            mAdapter.setOnItemClickListener(mOnItemClickListener);
            mMemo_rv.setAdapter(mAdapter);
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
    }

    public void startMatch(){
        if(PermissionCheck.checkContactsPermission(this)){
            final ProgressDialog dialog = new ProgressDialog(mContext);
            dialog.setTitle("正在匹配联系人");
//            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            getContactsAsync(new UploadCallback<Contact>() {
                @Override
                public void uploadProgress(int progress, int total) {}
                @Override
                public void uploadAll(List<Contact> list) {
                    mAdapter = new MemoAdapter(mContext, mList, ItemLayoutType.AVATAR_IMG);
                    mAdapter.setOnItemClickListener(mOnItemClickListener);
                    mMemo_rv.setAdapter(mAdapter);
                    if(mContactsList.size() > 0){
                        mRefreshLayout.setVisibility(View.VISIBLE);
                        mNoMatch.setVisibility(View.GONE);
                        mContainer.setVisibility(View.GONE);
                    }else {
                        mRefreshLayout.setVisibility(View.GONE);
                        mNoMatch.setVisibility(View.VISIBLE);
                        mContainer.setVisibility(View.GONE);
                    }
                    dialog.dismiss();
                    mRefreshLayout.autoRefresh(0);
                    mMemoPresenter.refresh(mList);
                    SpfsUtils.write(mContext, SpfsUtils.USER, "match_contacts", true);
                    ToastUtil.show(mContext, "匹配完成");
                    // insert matched contact into database.
                    for(Contact contact : mContactsList) {
                        Contact findContact = mContactDao
                                .queryBuilder()
                                .where(ContactDao.Properties.UserId.eq(contact.getUserId()))
                                .build().unique();
                        if(findContact != null){
                            mContactDao.deleteByKey(findContact.getUserId());
                        }
                        mContactDao.insert(contact);
                    }
                }
                @Override
                public void uploadFail(String error) {
                    dialog.dismiss();
                    ToastUtil.show(mContext, error);
                }
            });
        }else {
            ToastUtil.show(mContext, "未授权读取联系人");
        }
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Intent intent = new Intent(mContext, ArticlePage.class);
            intent.putExtra("contentId", mList.get(position).getId());
            startActivity(intent);
        }
    };

    @Subscribe
    public void onRequestRefreshEvent(RequestRefreshEvent event){
        if(event.getRefreshPosition() == 5 && hasMatched){
            mMemo_rv.scrollToPosition(0);
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

    private void getContactsAsync(final UploadCallback<Contact> listener) {
        ContactsQueryHandler qh = new ContactsQueryHandler(getContext().getContentResolver(),
                new OnQueryListener<Contact>() {
                    @Override
                    public void onQueryProgress(int progress, int total) {}
                    @Override
                    public void onQueryComplete(List<Contact> list) {
                        uploadSignatureCode(list, listener);
                    }
                });
        // query field
        String[] projection = {ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY};
        qh.startQuery(0, null, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null,
                "sort_key COLLATE LOCALIZED asc");
    }

    // upload signature code for matching friends.
    private void uploadSignatureCode(final List<Contact> list, final UploadCallback<Contact> listener) {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String code = StringUtil.getMD5(list.get(i).getPhoneNum());
            codes.add(code);
        }
        HttpUtils.matchContacts(codes).enqueue(new Callback<ArrayResponse<Contact>>() {
            @Override
            public void onResponse(Call<ArrayResponse<Contact>> call,
                                   Response<ArrayResponse<Contact>> response) {
                if (response.body() != null) {
                    if (response.body().isOk()) {
                        mContactsList.addAll(response.body().getData());
                        listener.uploadAll(mContactsList);
                    } else {
                        listener.uploadFail("匹配失败，" + response.body().getMessage());
                    }
                } else {
                    Log.e("match_code", "匹配失败");
                    listener.uploadFail("匹配失败，服务器未知错误");
                }
            }

            @Override
            public void onFailure(Call<ArrayResponse<Contact>> call, Throwable t) {
                t.printStackTrace();
                listener.uploadFail("连接失败，请检查你的网络连接");
            }
        });
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
                Map<String, Contact> contactIdMap = new HashMap<>();
                mTempList = new ArrayList<>();
                cursor.moveToFirst();
                int size = cursor.getCount();
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    number = number.replace(" ", "");
                    String sortKey = cursor.getString(3);
                    int contactId = cursor.getInt(4);
                    String lookUpKey = cursor.getString(5);

                    String username = UserStatus.getCurrentUser().getUsername();
                    // remove duplicate numbers and your own number.
                    if (contactIdMap.containsKey(number)
                            || (username != null && username.equals(number))) {
                        // do nothing
                    } else {
                        // create a contact object.
                        Contact contact = new Contact();
                        contact.setDisplayName(name);
                        contact.setPhoneNum(number);
                        contact.setSortKey(sortKey);
//                        contact.setUserId(contactId);
                        contact.setLookUpKey(lookUpKey);
                        mTempList.add(contact);
                        contactIdMap.put(number, contact);
                    }
                    listener.onQueryProgress(i / size * 100, size);
                }
                cursor.close();
                listener.onQueryComplete(mTempList);
            }
            super.onQueryComplete(token, cookie, cursor);
        }
    }
}
