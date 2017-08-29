package com.donutcn.memo.entity;

import com.donutcn.memo.constant.FieldConstant;
import com.donutcn.memo.interfaces.Jsonify;
import com.donutcn.memo.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/13.
 */

public class MessageItem extends Jsonify {

    @Expose
    @SerializedName(FieldConstant.REPLY_ID)
    private String messageId;

    @Expose
    @SerializedName(FieldConstant.COMMENT_ID)
    private String commentId;

    @Expose
    @SerializedName(FieldConstant.USER_ICON_URL)
    private String iconUrl;

    @Expose
    @SerializedName(FieldConstant.USER_NICKNAME)
    private String name;

    @Expose
    @SerializedName(FieldConstant.REPLY_TIME)
    private String time;

    @Expose
    @SerializedName(FieldConstant.REPLY_REAL_NAME)
    private String realName;

    @Expose
    @SerializedName(FieldConstant.REPLY_PHONE)
    private String phone;

    @Expose
    @SerializedName(FieldConstant.REPLY_EMAIL)
    private String email;

    @Expose
    @SerializedName(FieldConstant.REPLY_WE_CHAT)
    private String weChat;

    @Expose
    @SerializedName(FieldConstant.REPLY_RESUME)
    private String resume;

    @Expose
    @SerializedName(FieldConstant.REPLY_COMMENT)
    private String comment;

    @Expose
    @SerializedName(FieldConstant.REPLY_VOTE)
    private String vote;

    @Expose
    @SerializedName(FieldConstant.REPLY_ANSWER)
    private String answer;

    private long timeStamp;

    public boolean isMessage(){
        return !(messageId == null || "".equals(messageId));
    }

    public String getMessageId() {
        return messageId;
    }

    public String getCommentId() {
        return commentId;
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

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put(FieldConstant.REPLY_ID, messageId);
            json.put(FieldConstant.COMMENT_ID, commentId);
            json.put(FieldConstant.USER_ICON_URL, iconUrl);
            json.put(FieldConstant.USER_NICKNAME, name);
            json.put(FieldConstant.REPLY_TIME, time);
            json.put(FieldConstant.REPLY_REAL_NAME, realName);
            json.put(FieldConstant.REPLY_PHONE, phone);
            json.put(FieldConstant.REPLY_EMAIL, email);
            json.put(FieldConstant.REPLY_WE_CHAT, weChat);
            json.put(FieldConstant.REPLY_RESUME, resume);
            json.put(FieldConstant.REPLY_COMMENT, comment);
            json.put(FieldConstant.REPLY_VOTE, vote);
            json.put(FieldConstant.REPLY_ANSWER, answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
