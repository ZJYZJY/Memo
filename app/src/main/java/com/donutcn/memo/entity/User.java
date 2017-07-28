package com.donutcn.memo.entity;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/7/28.
 */

public class User {

    private String username;
    private String phoneNum;
    private String gender;
    private String iconUrl;

    public User(String phoneNum) {
        this.phoneNum = phoneNum;
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
