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

    private static User USER = null;

    private static int loginType;

    public static void setCurrentUser(Map<String, String> data) {
        if (USER == null)
            USER = new User();
        USER.setUserId(data.get(FieldConstant.USER_ID));
        USER.setOpenId(data.get(FieldConstant.USER_OPEN_ID));
        USER.setName(data.get(FieldConstant.USER_NICKNAME));
        USER.setGender(data.get(FieldConstant.USER_GENDER));
        USER.setIconUrl(data.get(FieldConstant.USER_ICON_URL));
        USER.setPhone(data.get(FieldConstant.USER_PHONE));
        USER.setUsername(data.get(FieldConstant.USER_NAME));
        USER.setSignature(data.get(FieldConstant.USER_SIGNATURE));
        USER.setEmail(data.get(FieldConstant.USER_EMAIL));
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
        // clear cookies
        HttpUtils.clearCookies();
        // remove login flag
        SpfsUtils.clear(context, SpfsUtils.CACHE);
        SpfsUtils.clear(context, SpfsUtils.USER);
        // clear local cache.
        FileCacheUtil.clear(context, FileCacheUtil.CONTENT_LIST_CACHE);
        FileCacheUtil.clear(context, FileCacheUtil.MESSAGE_LIST_CACHE);
        // clear the local database.
        ((App)context.getApplicationContext()).getDaoSession().getContactDao().deleteAll();
    }
}
