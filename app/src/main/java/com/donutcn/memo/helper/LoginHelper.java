package com.donutcn.memo.helper;

import android.content.Context;
import android.content.Intent;

import com.donutcn.memo.activity.LoginActivity;
import com.donutcn.memo.constant.FieldConfig;
import com.donutcn.memo.event.LoginStateEvent;
import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.SpfsUtils;
import com.donutcn.memo.utils.ToastUtil;
import com.donutcn.memo.utils.UserStatus;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.tencent.android.tpush.XGPushManager;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import static com.donutcn.memo.utils.UserStatus.getCurrentUser;

/**
 * com.donutcn.memo.helper
 * Created by 73958 on 2017/8/8.
 */

public class LoginHelper {

    public static void login(final Context context, int loginType, final Map<String, String> data) {
        SpfsUtils.write(context, SpfsUtils.USER, "loginFlag", true);
        SpfsUtils.write(context, SpfsUtils.USER, "login_type", loginType);
//        EMClient.getInstance().loginWithToken(data.get(FieldConfig.USER_NAME),
//                data.get(FieldConfig.USER_IM_TOKEN), new EMCallBack() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onError(int code, String error) {
//                ToastUtil.show(context, error + "(" + code + ")");
//            }
//
//            @Override
//            public void onProgress(int progress, String status) {
//            }
//        });
        EMClient.getInstance().login(data.get(FieldConfig.USER_NAME),
                "12345678", new EMCallBack() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(int code, String error) {
//                        ToastUtil.show(context, error + "(" + code + ")");
                        LogUtil.e(error + "(" + code + ")");
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }
                });
//        if(data != null){
            LogUtil.d("login_info", data.toString());
            writeUserPreference(context, data);
            UserStatus.setCurrentUser(data);
//        }
        // post login event
        EventBus.getDefault().postSticky(new LoginStateEvent(LoginStateEvent.LOGIN, getCurrentUser()));
    }

    public static void logout(Context context) {
        XGPushManager.registerPush(context, "*");
        EMClient.getInstance().logout(false);
        UserStatus.clear(context);
        // post logout event
        EventBus.getDefault().postSticky(new LoginStateEvent(LoginStateEvent.LOGOUT, null));
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

    public static int loginType(Context context){
        return SpfsUtils.readInt(context.getApplicationContext(),
                SpfsUtils.USER, "login_type", UserStatus.PHONE_LOGIN);
    }

    public static void syncUserInfo(Context context, Map<String, String> data){
        writeUserPreference(context, data);
        UserStatus.setCurrentUser(data);
        EventBus.getDefault().postSticky(new LoginStateEvent(LoginStateEvent.SYNC, getCurrentUser()));
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
        SpfsUtils.write(context, SpfsUtils.USER, "userId", data.get(FieldConfig.USER_ID));
        SpfsUtils.write(context, SpfsUtils.USER, "name", data.get(FieldConfig.USER_NICKNAME));
        SpfsUtils.write(context, SpfsUtils.USER, "gender", data.get(FieldConfig.USER_GENDER));
        SpfsUtils.write(context, SpfsUtils.USER, "email", data.get(FieldConfig.USER_EMAIL));
        SpfsUtils.write(context, SpfsUtils.USER, "iconUrl", data.get(FieldConfig.USER_ICON_URL));
        SpfsUtils.write(context, SpfsUtils.USER, "phone", data.get(FieldConfig.USER_PHONE));
        SpfsUtils.write(context, SpfsUtils.USER, "username", data.get(FieldConfig.USER_NAME));
        SpfsUtils.write(context, SpfsUtils.USER, "signature", data.get(FieldConfig.USER_SIGNATURE));
        SpfsUtils.write(context, SpfsUtils.USER, "follow", data.get(FieldConfig.USER_FOLLOW));
        SpfsUtils.write(context, SpfsUtils.USER, "token", data.get(FieldConfig.USER_IM_TOKEN));
    }

    private static Map<String, String> readUserPreference(Context context){
        Map<String, String> data = new HashMap<>();
        data.put(FieldConfig.USER_ID, SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "userId", ""));
        data.put(FieldConfig.USER_NICKNAME, SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "name", ""));
        data.put(FieldConfig.USER_GENDER, SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "gender", ""));
        data.put(FieldConfig.USER_EMAIL, SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "email", ""));
        data.put(FieldConfig.USER_ICON_URL, SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "iconUrl", ""));
        data.put(FieldConfig.USER_PHONE, SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "phone", ""));
        data.put(FieldConfig.USER_NAME, SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "username", ""));
        data.put(FieldConfig.USER_SIGNATURE, SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "signature", ""));
        data.put(FieldConfig.USER_FOLLOW, SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "follow", ""));
        data.put(FieldConfig.USER_IM_TOKEN, SpfsUtils.readString(
                context.getApplicationContext(), SpfsUtils.USER, "token", ""));
        return data;
    }
}
