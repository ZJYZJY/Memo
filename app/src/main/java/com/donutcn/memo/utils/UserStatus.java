package com.donutcn.memo.utils;

import android.content.Context;
import android.content.Intent;

import com.donutcn.memo.activity.LoginActivity;
import com.donutcn.memo.entity.User;

import java.util.Map;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/28.
 */

public class UserStatus {

    public static int PHONE_LOGIN = 1000;
    public static int WECHAT_LOGIN = 2000;
    private static User USER = null;

    private static void setCurrentUser(String phoneNum) {
        USER = new User(phoneNum);
    }

    private static void setCurrentUser(String openId, String name, String gender, String iconUrl) {
        USER = new User(openId, name, gender, iconUrl);
    }

    public static User getCurrentUser() {
        return USER;
    }

    public static void login(Context context, int loginType, Map<String, String> data) {
        SpfsUtils.write(context, SpfsUtils.USER, "loginFlag", true);
        if(loginType == PHONE_LOGIN){
            SpfsUtils.write(context, SpfsUtils.USER, "phoneNumber", data.get("phone"));
            setCurrentUser(data.get("phone"));
        }else if(loginType == WECHAT_LOGIN){
            setCurrentUser(data.get("openid"), data.get("name"), data.get("gender"), data.get("iconUrl"));
        }
    }

    public static void logout(Context context, String phone) {
        USER = null;
        // clear cookies
        HttpUtils.clearCookies(phone);
        // remove login flag
        SpfsUtils.remove(context, SpfsUtils.USER, "loginFlag");
        // clear local cache.
        FileCacheUtil.clear(context, FileCacheUtil.docCache);
        // go back to LoginActivity
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("showSplash", false);
        context.startActivity(intent);
    }

    public static boolean ifRequestLogin(Context context, String message) {
        if(!isLogin(context)){
            if(message != null){
                ToastUtil.show(context, message);
            }
            // go back to LoginActivity
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("showSplash", false);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    public static boolean isLogin(Context context) {
        return SpfsUtils.readBoolean(context, SpfsUtils.USER, "loginFlag", false);
    }
}
