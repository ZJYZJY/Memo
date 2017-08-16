package com.donutcn.memo.receiver;

import android.content.Context;
import android.content.Intent;

import com.donutcn.memo.activity.MainActivity;
import com.donutcn.memo.utils.LogUtil;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import static com.donutcn.memo.type.PushType.NEW_CONTENT;
import static com.donutcn.memo.type.PushType.NEW_MESSAGE;
import static com.donutcn.memo.type.PushType.SYNC_INFO;

public class MemoPushReceiver extends XGPushBaseReceiver {

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    /**
     * pass through message(without notification) receive listener.
     * @param context context
     * @param pushMsg message from server
     */
    @Override
    public void onTextMessage(Context context, XGPushTextMessage pushMsg) {
        LogUtil.d(pushMsg.toString());
        String title = pushMsg.getTitle();
        if(title.equals(NEW_CONTENT)){

        } else if(title.equals(NEW_MESSAGE)){

        } else if(title.equals(SYNC_INFO)){
            String content = pushMsg.getContent();

        }
    }

    /**
     * notification item click listener.
     * @param context context
     * @param xgPushClickedResult message from server
     */
    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    /**
     * notification item receive listener.
     * @param context context
     * @param xgPushShowedResult message from server
     */
    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
        LogUtil.d(xgPushShowedResult.toString());
    }
}
