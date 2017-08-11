package com.donutcn.memo.entity;

import android.content.Context;

import com.donutcn.memo.utils.LogUtil;
import com.donutcn.memo.utils.SpfsUtils;

import java.util.List;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/7/28.
 */

public class User {

    private String userId;
    private String openId;
    private String name;
    private String username;
    private String phone;
    private String email;
    private String gender;
    private String iconUrl;
    private String signature;
    private List<String> followedUser;

    public User(){}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public List<String> getFollowedUser() {
        return followedUser;
    }

    public void setFollowedUser(List<String> followedUser) {
        this.followedUser = followedUser;
    }

    public void follow(Context context, String userId, boolean follow){
        if(follow){
            getFollowedUser().add(userId);
            String str = SpfsUtils.readString(
                    context.getApplicationContext(), SpfsUtils.USER, "follow", "");
            SpfsUtils.write(context, SpfsUtils.USER, "follow", str + userId + "-");
            LogUtil.d("followed_cache", "followed:" + str + userId + "-");
        }else {
            getFollowedUser().remove(userId);
            String str = SpfsUtils.readString(
                    context.getApplicationContext(), SpfsUtils.USER, "follow", "");
            SpfsUtils.write(context, SpfsUtils.USER, "follow",
                    str.substring(0, str.length() - userId.length() - 1));
            LogUtil.d("followed_cache", "followed:" + str.substring(0, str.length() - userId.length() - 1));
        }
    }
}
