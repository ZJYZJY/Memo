package com.donutcn.memo.listener;

/**
 * com.donutcn.memo.listener
 * Created by 73958 on 2017/7/19.
 */

public interface OnReceiveNewMessagesListener {

    /**
     * Receive new message listener.
     * @param msgCount unread message count.
     * @param msgType new message type.
     */
    void onReceiveNewMessage(int msgCount, int msgType);
}
