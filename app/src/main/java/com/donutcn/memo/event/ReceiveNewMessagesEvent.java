package com.donutcn.memo.event;

/**
 * com.donutcn.memo.event
 * Created by 73958 on 2017/7/23.
 */

public class ReceiveNewMessagesEvent {

    private int messagePos;
    private int messageCount;

    public ReceiveNewMessagesEvent(int messagePos, int messageCount) {
        this.messagePos = messagePos;
        this.messageCount = messageCount;
    }

    public int getMessagePos() {
        return messagePos;
    }

    public int getMessageCount() {
        return messageCount;
    }
}
