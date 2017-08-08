package com.donutcn.memo.entity;

import com.donutcn.memo.utils.UserStatus;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/7/28.
 */

public class User {

    private String username;
    private String phoneNum;
    private int loginType;
    private String gender;
    private String iconUrl;

    private String openId;
    private String name;

    public User(String phoneNum) {
        this.loginType = UserStatus.PHONE_LOGIN;
        this.phoneNum = phoneNum;
    }

    public User(String openId, String name, String gender, String iconUrl){
        this.loginType = UserStatus.WECHAT_LOGIN;
        this.openId = openId;
        this.name = name;
        this.name = name;
        this.gender = gender;
        this.iconUrl = iconUrl;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
