package com.donutcn.memo.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.commons.models.IUser;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/26.
 */

public class ChatMessage implements IMessage {

    private long id;
    private String text;
    private String timeString;
    private MessageType type;
    private IUser user;
    private String mediaFilePath;
    private long duration;
    private String progress;

    public ChatMessage(String text, MessageType type, IUser user) {
        this.text = text;
        this.type = type;
        this.user = user;
        timeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        this.id = UUID.randomUUID().getLeastSignificantBits();
    }

    public ChatMessage(String text, MessageType type, IUser user, long timeStamp) {
        this.text = text;
        this.type = type;
        this.user = user;
        Date data = new Date(timeStamp);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.timeString = df.format(data);
        this.id = UUID.randomUUID().getLeastSignificantBits();
    }

    @Override
    public String getMsgId() {
        return String.valueOf(id);
    }

    @Override
    public IUser getFromUser() {
        return user;
    }

    public void setUserInfo(IUser user) {
        this.user = user;
    }

    public void setMediaFilePath(String path) {
        this.mediaFilePath = path;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    @Override
    public String getProgress() {
        return progress;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    @Override
    public String getTimeString() {
        return timeString;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public MessageStatus getMessageStatus() {
        return MessageStatus.SEND_SUCCEED;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getMediaFilePath() {
        return mediaFilePath;
    }
}