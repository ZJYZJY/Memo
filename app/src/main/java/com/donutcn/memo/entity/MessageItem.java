package com.donutcn.memo.entity;

import com.donutcn.memo.constant.FieldConfig;
import com.donutcn.memo.utils.StringUtil;
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
    @SerializedName(FieldConfig.REPLY_ID)
    private String id;

    @Expose
    @SerializedName(FieldConfig.USER_ICON_URL)
    private String iconUrl;

    @Expose
    @SerializedName(FieldConfig.USER_NICKNAME)
    private String name;

    @Expose
    @SerializedName(FieldConfig.REPLY_TIME)
    private String time;

    @Expose
    @SerializedName(FieldConfig.REPLY_REAL_NAME)
    private String realName;

    @Expose
    @SerializedName(FieldConfig.REPLY_PHONE)
    private String phone;

    @Expose
    @SerializedName(FieldConfig.REPLY_EMAIL)
    private String email;

    @Expose
    @SerializedName(FieldConfig.REPLY_WE_CHAT)
    private String weChat;

    @Expose
    @SerializedName(FieldConfig.REPLY_RESUME)
    private String resume;

    @Expose
    @SerializedName(FieldConfig.REPLY_RESUME_URL)
    private String resumeUrl;

    @Expose
    @SerializedName(FieldConfig.REPLY_COMMENT)
    private String comment;

    @Expose
    @SerializedName(FieldConfig.REPLY_VOTE)
    private String vote;

    @Expose
    @SerializedName(FieldConfig.REPLY_ANSWER)
    private String answer;

    private long timeStamp;

    public String getId() {
        return id;
    }

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

    public String getResumeUrl() {
        return resumeUrl;
    }

    public String getComment() {
        return comment;
    }

    public String getVote() {
        return vote;
    }

    public String getAnswer() {
        return answer;
    }

    public long getTimeStamp() {
        // transfer 'ms' to 's'.
        timeStamp =  StringUtil.string2Date(time).getTime() / 1000;
        return timeStamp;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put(FieldConfig.REPLY_ID, id);
            json.put(FieldConfig.USER_ICON_URL, iconUrl);
            json.put(FieldConfig.USER_NICKNAME, name);
            json.put(FieldConfig.REPLY_TIME, time);
            json.put(FieldConfig.REPLY_REAL_NAME, realName);
            json.put(FieldConfig.REPLY_PHONE, phone);
            json.put(FieldConfig.REPLY_EMAIL, email);
            json.put(FieldConfig.REPLY_WE_CHAT, weChat);
            json.put(FieldConfig.REPLY_RESUME, resume);
            json.put(FieldConfig.REPLY_RESUME_URL, resumeUrl);
            json.put(FieldConfig.REPLY_COMMENT, comment);
            json.put(FieldConfig.REPLY_VOTE, vote);
            json.put(FieldConfig.REPLY_ANSWER, answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
