package com.donutcn.memo.entity;

import com.donutcn.memo.type.PublishType;
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
    private String newMsgCount;

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

    public String getNewMsgCount() {
        return newMsgCount;
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
