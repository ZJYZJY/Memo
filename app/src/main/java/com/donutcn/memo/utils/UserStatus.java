package com.donutcn.memo.utils;

import android.content.Context;
import android.content.Intent;

import com.donutcn.memo.activity.LoginActivity;
import com.donutcn.memo.entity.User;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/28.
 */

public class UserStatus {

    private static User USER = null;

    private static void setCurrentUser(String phoneNum) {
        USER = new User(phoneNum);
    }

    public static User getCurrentUser() {
        return USER;
    }

    public static void login(Context context, String phoneNumber) {
        SpfsUtils.write(context, SpfsUtils.USER, "loginFlag", true);
        SpfsUtils.write(context, SpfsUtils.USER, "phoneNumber", phoneNumber);
        setCurrentUser(phoneNumber);
    }

    public static void logout(Context context, String phone) {
        USER = null;
        HttpUtils.clearCookies(phone);
        SpfsUtils.remove(context, SpfsUtils.USER, "loginFlag");
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("showSplash", false);
        context.startActivity(intent);
    }

    public static boolean checkLoginState(Context context) {
        return SpfsUtils.readBoolean(context, SpfsUtils.USER, "loginFlag", false);
    }
}
