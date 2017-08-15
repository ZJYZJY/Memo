package com.donutcn.memo.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/3.
 */

@Entity
public class Contact {

    @Id
    @Expose
    @SerializedName("user_id")
    private String userId; //id

    private String displayName;//姓名

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("head_portrait")
    private String avatar;

    @Expose
    @SerializedName("username")
    private String phoneNum; // 电话号码
    private String sortKey; // 排序用的
    private String lookUpKey;

    @Generated(hash = 1689543450)
    public Contact(String userId, String displayName, String name, String avatar,
            String phoneNum, String sortKey, String lookUpKey) {
        this.userId = userId;
        this.displayName = displayName;
        this.name = name;
        this.avatar = avatar;
        this.phoneNum = phoneNum;
        this.sortKey = sortKey;
        this.lookUpKey = lookUpKey;
    }

    @Generated(hash = 672515148)
    public Contact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public String getLookUpKey() {
        return lookUpKey;
    }

    public void setLookUpKey(String lookUpKey) {
        this.lookUpKey = lookUpKey;
    }
}
