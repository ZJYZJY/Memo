package com.donutcn.memo.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/13.
 */

public class MessageItem {

    @Expose
    @SerializedName(value = "reply_id")
    private String id;

    @Expose
    @SerializedName(value = "icon")
    private String iconUrl;

    @Expose
    @SerializedName(value = "name")
    private String name;

    @Expose
    @SerializedName(value = "time")
    private String time;

    @Expose
    @SerializedName(value = "type")
    private String realName;

    @Expose
    @SerializedName(value = "phone")
    private String phone;

    @Expose
    @SerializedName(value = "email")
    private String email;

    @Expose
    @SerializedName(value = "wechat")
    private String weChat;

    @Expose
    @SerializedName(value = "resume")
    private String resume;

    public String getIconUrl() {
        return iconUrl;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getRealName() {
        return realName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getWeChat() {
        return weChat;
    }

    public String getResume() {
        return resume;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("reply_id", id);
            json.put("iconUrl", iconUrl);
            json.put("name", name);
            json.put("time", time);
            json.put("realName", realName);
            json.put("phone", phone);
            json.put("email", email);
            json.put("weChat", weChat);
            json.put("resume", resume);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
