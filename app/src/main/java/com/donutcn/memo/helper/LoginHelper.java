package com.donutcn.memo.helper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.donutcn.memo.activity.LoginActivity;
import com.donutcn.memo.event.LoginStateEvent;
import com.donutcn.memo.utils.SpfsUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import static com.donutcn.memo.utils.UserStatus.PHONE_LOGIN;
import static com.donutcn.memo.utils.UserStatus.WECHAT_LOGIN;
import static com.donutcn.memo.utils.UserStatus.getCurrentUser;

/**
 * com.donutcn.memo.helper
 * Created by 73958 on 2017/8/8.
 */

public class LoginHelper {

    public static void login(Context context, int loginType, Map<String, String> data) {
        SpfsUtils.write(context, SpfsUtils.USER, "loginFlag", true);
        Log.d("login_info", data.toString());
        if(loginType == PHONE_LOGIN){
            SpfsUtils.write(context, SpfsUtils.USER, "phoneNumber", data.get("phone"));
            SpfsUtils.write(context, SpfsUtils.USER, "iconurl", data.get("iconurl"));
            UserStatus.setCurrentUser(data.get("phone"), data.get("iconurl"));
        }else if(loginType == WECHAT_LOGIN){
            UserStatus.setCurrentUser(data.get("openid"), data.get("name"), data.get("gender"), data.get("iconurl"));
        }
        EventBus.getDefault().postSticky(new LoginStateEvent(true, getCurrentUser()));
    }

    public static void logout(Context context) {
        UserStatus.clear(context);
        EventBus.getDefault().postSticky(new LoginStateEvent(false, null));
        // go back to LoginActivity
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("showSplash", false);
        context.startActivity(intent);
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
}
