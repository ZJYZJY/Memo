package com.donutcn.memo.entity;

import cn.jiguang.imui.commons.models.IUser;


public class ChatUser implements IUser {

    private String userId;
    private String username;
    private String name;
    private String avatar;

    public ChatUser(String userId, String name, String avatar) {
        this.userId = userId;
        this.name = name;
        this.avatar = avatar;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getAvatarFilePath() {
        return avatar;
    }
}
