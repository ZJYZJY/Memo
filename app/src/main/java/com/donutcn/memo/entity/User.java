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
    private int notification;
    private List<String> followedUser;

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId != null)
            this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        if (openId != null)
            this.openId = openId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null)
            this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username != null)
            this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone != null)
            this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null)
            this.email = email;
    }

    public String getGender() {
        if (gender == null || gender.equals(""))
            return null;
        else
            return gender;
    }

    public void setGender(String gender) {
        if (gender != null)
            this.gender = gender;
    }

    public String getIconUrl() {
        if (iconUrl == null || iconUrl.equals(""))
            return null;
        else
            return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        if (iconUrl != null)
            this.iconUrl = iconUrl;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int enable) {
        this.notification = enable;
    }

    public String getSignature() {
        if (signature == null || signature.equals(""))
            return null;
        else
            return signature;
    }

    public void setSignature(String signature) {
        if (signature != null)
            this.signature = signature;
    }

    public List<String> getFollowedUser() {
        return followedUser;
    }

    public void setFollowedUser(List<String> followedUser) {
        this.followedUser = followedUser;
    }

    public void follow(Context context, String userId, boolean follow) {
        if (follow) {
            getFollowedUser().add(userId);
            String str = SpfsUtils.readString(
                    context.getApplicationContext(), SpfsUtils.USER, "follow", "");
            SpfsUtils.write(context, SpfsUtils.USER, "follow", str + userId + "-");
            LogUtil.d("followed_cache", "followed:" + str + userId + "-");
        } else {
            getFollowedUser().remove(userId);
            List<String> list = getFollowedUser();
            StringBuilder builder = new StringBuilder("");
            for(String s : list){
                builder.append(s).append("-");
            }
            SpfsUtils.write(context, SpfsUtils.USER, "follow", builder.toString());
            LogUtil.d("followed_cache", "followed:" + builder.toString());
        }
    }
}
