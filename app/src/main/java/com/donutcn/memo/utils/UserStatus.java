package com.donutcn.memo.utils;

import android.content.Context;

import com.donutcn.memo.App;
import com.donutcn.memo.constant.FieldConstant;
import com.donutcn.memo.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/28.
 */

public class UserStatus {

    public static int PHONE_LOGIN = 1000;
    public static int WECHAT_LOGIN = 2000;

    private static User USER = new User();

    private static int loginType;

    public static void setCurrentUser(Map<String, String> data) {
        if (USER == null)
            USER = new User();
        USER.setUserId(data.get(FieldConstant.USER_ID));
        USER.setUnionId(data.get(FieldConstant.USER_UNION_ID));
        USER.setName(data.get(FieldConstant.USER_NICKNAME));
        USER.setGender(data.get(FieldConstant.USER_GENDER));
        USER.setIconUrl(data.get(FieldConstant.USER_ICON_URL));
        USER.setPhone(data.get(FieldConstant.USER_PHONE));
        USER.setUsername(data.get(FieldConstant.USER_NAME));
        USER.setSignature(data.get(FieldConstant.USER_SIGNATURE));
        USER.setEmail(data.get(FieldConstant.USER_EMAIL));
        String enable = data.get(FieldConstant.USER_NOTIFICATION);
        if(enable != null){
            USER.setNotification(Integer.valueOf(data.get(FieldConstant.USER_NOTIFICATION)));
        }
        String str = data.get(FieldConstant.USER_FOLLOW);
        if(str != null && !str.equals("")) {
            String[] followed = str.split("-");
            List<String> list = new ArrayList<>();
            Collections.addAll(list, followed);
            USER.setFollowedUser(list);
        } else {
            USER.setFollowedUser(new ArrayList<String>());
        }
    }

    public static User getCurrentUser() {
        return USER;
    }

    public static boolean isLogin(Context context) {
        return SpfsUtils.readBoolean(context, SpfsUtils.USER, "loginFlag", false);
    }

    public static boolean isIMLogin(Context context) {
        return SpfsUtils.readBoolean(context, SpfsUtils.USER, "loginFlag_IM", false);
    }

    public static void clear(Context context){
        USER = null;
        // clearState cookies
        HttpUtils.clearCookies();
        // remove login flag
        SpfsUtils.clear(context, SpfsUtils.CACHE);
        SpfsUtils.clear(context, SpfsUtils.USER);
        // clearState local cache.
        FileCacheUtil.clear(context, FileCacheUtil.CONTENT_LIST_CACHE);
        FileCacheUtil.clear(context, FileCacheUtil.MESSAGE_LIST_CACHE);
        FileCacheUtil.clear(context, FileCacheUtil.MESSAGE_ITEM_CACHE);
        // clearState the local database.
        ((App)context.getApplicationContext()).getDaoSession().getContactDao().deleteAll();
    }
}
