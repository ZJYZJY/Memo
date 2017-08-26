package com.donutcn.memo.entity;

import com.donutcn.memo.constant.FieldConfig;
import com.donutcn.memo.interfaces.Jsonify;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/12.
 */

public class BriefMessage extends Jsonify {

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

    public BriefMessage(){}

    public BriefMessage(String id, String type, String title, String subTitle, String time, int newMsgCount) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
        this.time = time;
        this.newMsgCount = newMsgCount;
    }

    public BriefMessage(String id, String type, String title, String subTitle, long time, int newMsgCount) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
        setTime(time);
        this.newMsgCount = newMsgCount;
    }

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
        if(subTitle != null)
            this.subTitle = subTitle;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        if(type != null)
            this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        if(time != null)
            this.time = time;
    }

    public void setTime(long timeStamp) {
        Date data = new Date(timeStamp);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.time = df.format(data);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNewMsgCount(int newMsgCount) {
        this.newMsgCount = newMsgCount;
    }

    @Override
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
