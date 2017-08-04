package com.donutcn.memo.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/3.
 */

public class Contact {

    private int contactId; //id
    private String desplayName;//姓名

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("head_portrait")
    private String avatar;
    private String phoneNum; // 电话号码
    private String sortKey; // 排序用的
    private String lookUpKey;
    private int selected = 0;

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

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getDesplayName() {
        return desplayName;
    }

    public void setDesplayName(String desplayName) {
        this.desplayName = desplayName;
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

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }
}
