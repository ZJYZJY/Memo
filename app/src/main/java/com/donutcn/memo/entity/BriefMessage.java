package com.donutcn.memo.entity;

import com.donutcn.memo.constant.FieldConfig;
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
    @SerializedName(FieldConfig.MESSAGE_ID)
    private String id;

    /**
     * if message type is private letter, 'type' will return icon url of usr.
     * else 'type' is {@link com.donutcn.memo.type.PublishType}
     */
    @Expose
    @SerializedName(FieldConfig.MESSAGE_TYPE)
    private String type;

    @Expose
    @SerializedName(FieldConfig.MESSAGE_TITLE)
    private String title;

    @Expose
    @SerializedName(FieldConfig.MESSAGE_SUB_TITLE)
    private String subTitle;

    @Expose
    @SerializedName(FieldConfig.MESSAGE_TIME)
    private String time;

    @Expose
    @SerializedName(FieldConfig.MESSAGE_DATE)
    private String date;

    @Expose
    @SerializedName(FieldConfig.MESSAGE_COUNT)
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

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
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
            json.put(FieldConfig.MESSAGE_ID, id);
            json.put(FieldConfig.MESSAGE_TYPE, type);
            json.put(FieldConfig.MESSAGE_TITLE, title);
            json.put(FieldConfig.MESSAGE_SUB_TITLE, subTitle);
            json.put(FieldConfig.MESSAGE_TIME, time);
            json.put(FieldConfig.MESSAGE_DATE, date);
            json.put(FieldConfig.MESSAGE_COUNT, newMsgCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
