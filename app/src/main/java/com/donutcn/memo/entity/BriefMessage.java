package com.donutcn.memo.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/12.
 */

public class BriefMessage {

    @Expose
    @SerializedName(value = "id")
    private String id;

    /**
     * if message type is private letter, 'type' will return icon url of usr.
     * else 'type' is {@link com.donutcn.memo.type.PublishType}
     */
    @Expose
    @SerializedName(value = "type")
    private String type;

    @Expose
    @SerializedName(value = "title")
    private String title;

    @Expose
    @SerializedName(value = "sub_title")
    private String subTitle;

    @Expose
    @SerializedName(value = "msg_count")
    private int newMsgCount;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public int getNewMsgCount() {
        return newMsgCount;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setNewMsgCount(int newMsgCount) {
        this.newMsgCount = newMsgCount;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("type", type);
            json.put("title", title);
            json.put("sub_title", subTitle);
            json.put("msg_count", newMsgCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
