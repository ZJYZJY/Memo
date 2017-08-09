package com.donutcn.memo.utils;

import android.content.Context;
import android.content.Intent;

import com.donutcn.memo.App;
import com.donutcn.memo.ContactDao;
import com.donutcn.memo.DaoSession;
import com.donutcn.memo.activity.LoginActivity;
import com.donutcn.memo.entity.User;
import com.donutcn.memo.event.LoginStateEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/28.
 */

public class UserStatus {

    public static int PHONE_LOGIN = 1000;
    public static int WECHAT_LOGIN = 2000;

    private static User USER = null;

    public static void setCurrentUser(String phoneNum, String iconUrl) {
        USER = new User(phoneNum, iconUrl);
    }

    public static void setCurrentUser(String openId, String name, String gender, String iconUrl) {
        USER = new User(openId, name, gender, iconUrl);
    }

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
