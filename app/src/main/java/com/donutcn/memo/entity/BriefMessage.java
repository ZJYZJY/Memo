package com.donutcn.memo.entity;

import com.donutcn.memo.constant.FieldConstant;
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
    @SerializedName(FieldConstant.MESSAGE_ID)
    private String id;

    /**
     * if message type is private letter, 'type' will return icon url of usr.
     * else 'type' is {@link com.donutcn.memo.type.PublishType}
     */
    @Expose
    @SerializedName(FieldConstant.MESSAGE_TYPE)
    private String type;

    @Expose
    @SerializedName(FieldConstant.MESSAGE_TITLE)
    private String title;

    @Expose
    @SerializedName(FieldConstant.MESSAGE_SUB_TITLE)
    private String subTitle;

    @Expose
    @SerializedName(FieldConstant.MESSAGE_TIME)
    private String time;

    @Expose
    @SerializedName(FieldConstant.MESSAGE_DATE)
    private String date;

    @Expose
    @SerializedName(FieldConstant.MESSAGE_COUNT)
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
            json.put(FieldConstant.MESSAGE_ID, id);
            json.put(FieldConstant.MESSAGE_TYPE, type);
            json.put(FieldConstant.MESSAGE_TITLE, title);
            json.put(FieldConstant.MESSAGE_SUB_TITLE, subTitle);
            json.put(FieldConstant.MESSAGE_TIME, time);
            json.put(FieldConstant.MESSAGE_DATE, date);
            json.put(FieldConstant.MESSAGE_COUNT, newMsgCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
