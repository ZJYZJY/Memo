package com.donutcn.memo.utils;

import android.content.Context;

import com.donutcn.memo.App;
import com.donutcn.memo.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
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
        USER.setUserId(data.get("user_id"));
        USER.setOpenId(data.get("openid"));
        USER.setName(data.get("name"));
        USER.setGender(data.get("sex"));
        USER.setIconUrl(data.get("head_portrait"));
        USER.setPhone(data.get("tel_number"));
        USER.setUsername(data.get("username"));
        USER.setSignature(data.get("self_introduction"));
        USER.setEmail(data.get("email"));
        String str = data.get("concern");
        if(str != null && !str.equals("")) {
            USER.setFollowedUser(Arrays.asList(str.split("-")));
        } else {
            USER.setFollowedUser(new ArrayList<String>());
        }
    }

//    public static void setCurrentUser(Map<String, String> data) {
//        if (USER == null)
//            USER = new User();
//        USER.setOpenId(data.get("openid"));
//        USER.setName(data.get("name"));
//        USER.setGender(data.get("gender"));
//        USER.setIconUrl(data.get("iconurl"));
//        USER.setPhone(data.get("phone"));
//        USER.setUsername(data.get("username"));
//        USER.setSignature(data.get("signature"));
//        USER.setEmail(data.get("email"));
//    }

    public static User getCurrentUser() {
        return USER;
    }

    public static boolean isLogin(Context context) {
        return SpfsUtils.readBoolean(context, SpfsUtils.USER, "loginFlag", false);
    }

    public static void clear(Context context){
        USER = null;
        // clear cookies
        HttpUtils.clearCookies();
        // remove login flag
        SpfsUtils.clear(context, SpfsUtils.CACHE);
        SpfsUtils.clear(context, SpfsUtils.USER);
        // clear local cache.
        FileCacheUtil.clear(context, FileCacheUtil.docCache);
        // clear the local database.
        ((App)context.getApplicationContext()).getDaoSession().getContactDao().deleteAll();
    }
}
