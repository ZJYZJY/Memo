package com.donutcn.memo.helper;

import android.content.Context;
import android.content.Intent;

import com.donutcn.memo.activity.LoginActivity;
import com.donutcn.memo.event.LoginStateEvent;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.SpfsUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import static com.donutcn.memo.utils.UserStatus.getCurrentUser;

/**
 * com.donutcn.memo.helper
 * Created by 73958 on 2017/8/8.
 */

public class LoginHelper {

    public static void login(Context context, int loginType, Map<String, String> data) {
        SpfsUtils.write(context, SpfsUtils.USER, "loginFlag", true);
        LogUtil.d("login_info", data.toString());
        SpfsUtils.write(context, SpfsUtils.USER, "login_type", loginType);

        writeUserPreference(context, data);
        UserStatus.setCurrentUser(data);
        // post login event
        EventBus.getDefault().postSticky(new LoginStateEvent(true, getCurrentUser()));
    }

    public static void logout(Context context) {
        UserStatus.clear(context);
        // post logout event
        EventBus.getDefault().postSticky(new LoginStateEvent(false, null));
        // go back to LoginActivity
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("showSplash", false);
        context.startActivity(intent);
    }

    public static void autoLogin(Context context) {
        int loginType = SpfsUtils.readInt(context.getApplicationContext(),
                SpfsUtils.USER, "login_type", UserStatus.PHONE_LOGIN);
        Map<String, String> data = readUserPreference(context);
        login(context.getApplicationContext(), loginType, data);
    }

    public static void syncUserInfo(Context context, Map<String, String> data){
        writeUserPreference(context, data);
        UserStatus.setCurrentUser(data);
        EventBus.getDefault().postSticky(new LoginStateEvent(true, getCurrentUser()));
    }

    public static boolean ifRequestLogin(Context context, String message) {
        if(!UserStatus.isLogin(context)){
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

    private static void writeUserPreference(Context context, Map<String, String> data){
        SpfsUtils.write(context, SpfsUtils.USER, "userId", data.get("user_id"));
        SpfsUtils.write(context, SpfsUtils.USER, "name", data.get("name"));
        SpfsUtils.write(context, SpfsUtils.USER, "gender", data.get("sex"));
        SpfsUtils.write(context, SpfsUtils.USER, "email", data.get("email"));
        SpfsUtils.write(context, SpfsUtils.USER, "iconUrl", data.get("head_portrait"));
        SpfsUtils.write(context, SpfsUtils.USER, "phone", data.get("tel_number"));
        SpfsUtils.write(context, SpfsUtils.USER, "username", data.get("username"));
        SpfsUtils.write(context, SpfsUtils.USER, "signature", data.get("self_introduction"));
        SpfsUtils.write(context, SpfsUtils.USER, "follow", data.get("follow"));
    }

    private static Map<String, String> readUserPreference(Context context){
        Map<String, String> data = new HashMap<>();
        data.put("user_id", SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "userId", ""));
        data.put("name", SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "name", ""));
        data.put("sex", SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "gender", ""));
        data.put("email", SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "email", ""));
        data.put("head_portrait", SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "iconUrl", ""));
        data.put("tel_number", SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "phone", ""));
        data.put("username", SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "username", ""));
        data.put("self_introduction", SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "signature", ""));
        data.put("follow", SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "follow", ""));
        return data;
    }
}
