package com.donutcn.memo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.donutcn.memo.entity.BriefMessage;
import com.donutcn.memo.entity.ChatMessage;
import com.donutcn.memo.entity.ChatUser;
import com.donutcn.memo.event.ChangeRedDotEvent;
import com.donutcn.memo.utils.LogUtil;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.NetUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static cn.jiguang.imui.commons.models.IMessage.MessageType.RECEIVE_TEXT;

/**
 * com.donutcn.memo
 * Created by 73958 on 2017/8/27.
 */

public class IMService extends Service {

    private BriefMessage mMessage;

    @Override
    public void onCreate() {
        super.onCreate();
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        EMClient.getInstance().addConnectionListener(mConnectionListener);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().removeConnectionListener(mConnectionListener);
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            mMessage = new BriefMessage();
            mMessage.setNewMsgCount(messages.size());
            //收到消息
            for (final EMMessage msg : messages) {
                switch (msg.getType()) {
                    case TXT:
                        final String message = ((EMTextMessageBody) msg.getBody()).getMessage();
                        LogUtil.d("收到消息：" + message);
                        // put avatar url in type
                        String avatar = msg.getStringAttribute("avatar", "");
                        String name = msg.getStringAttribute("name", "");
                        mMessage.setType(avatar);
                        mMessage.setTitle(name);
                        mMessage.setSubTitle(message);
                        mMessage.setId(msg.getUserName());
                        mMessage.setTime(msg.getMsgTime());

                        EventBus.getDefault().postSticky(new ChangeRedDotEvent(1, 0));
                        EventBus.getDefault().postSticky(mMessage);
                        ChatUser user = new ChatUser(msg.getUserName(), mMessage.getTitle(), mMessage.getType());
                        EventBus.getDefault().post(new ChatMessage(message, RECEIVE_TEXT, user, msg.getMsgTime()));
                        break;
                }
            }

        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    public EMConnectionListener mConnectionListener = new EMConnectionListener() {

        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            if (error == EMError.USER_REMOVED) {
                LogUtil.e("显示帐号已经被移除");
            } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                LogUtil.e("显示帐号在其他设备登录");
            } else {
                if (NetUtils.hasNetwork(getApplicationContext())) {
                    LogUtil.e("连接不到聊天服务器");
                } else {
                    LogUtil.e("当前网络不可用，请检查网络设置");
                }
            }
        }
    };
}
