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
 * Created by 73958 on 2017/8/1.
 */

public class BriefContent extends Jsonify{

    @Expose
    @SerializedName(FieldConstant.CONTENT_ID)
    private String id;

    @Expose
    @SerializedName(FieldConstant.USER_ID)
    private String userId;

    @Expose
    @SerializedName(FieldConstant.USER_NICKNAME)
    private String name;

    @Expose
    @SerializedName(FieldConstant.USER_ICON_URL)
    private String userIcon;

    @Expose
    @SerializedName(FieldConstant.CONTENT_URL)
    private String url;

    @Expose
    @SerializedName(FieldConstant.CONTENT_TITLE)
    private String title;

    @Expose
    @SerializedName(FieldConstant.CONTENT_TIME)
    private String time;

    @Expose
    @SerializedName(FieldConstant.CONTENT)
    private String content;

    @Expose
    @SerializedName(FieldConstant.CONTENT_TYPE)
    private String type;

    @Expose
    @SerializedName(FieldConstant.CONTENT_RIGHTS)
    private int isPrivate;

    @Expose
    @SerializedName(FieldConstant.CONTENT_READ_COUNT)
    private int readCount;

    @Expose
    @SerializedName(FieldConstant.CONTENT_UP_VOTE_COUNT)
    private int upVote;

    @Expose
    @SerializedName(FieldConstant.CONTENT_COMM_COUNT)
    private int comment;

    @Expose
    @SerializedName(FieldConstant.CONTENT_IMG_URL1)
    private String imgUrl0;

    @Expose
    @SerializedName(FieldConstant.CONTENT_IMG_URL2)
    private String imgUrl1;

    private long timeStamp;

//    public BriefContent(JSONObject json) throws JSONException {
//        this.id = json.getString("article_id");
//        this.userIcon = json.getString("head_portrait");
//        this.name = json.getString("name");
//        this.title = json.getString("title");
//        this.time = json.getString("create_time");
//        this.content = json.getString("content");
//        this.type = json.getString("type");
//        this.readCount = json.getInt("read_total");
//        this.upVote = json.getInt("praise");
//        this.comment = json.getInt("comment_total");
//        this.imgUrl0 = json.getString("url1");
//        this.imgUrl1 = json.getString("url2");
//    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUrl() {
        return url;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public boolean isPrivate() {
        return isPrivate == 1;
    }

    public String getReadCount() {
        return String.valueOf(readCount);
    }

    public String getUpVote() {
        return String.valueOf(upVote);
    }

    public String getComment() {
        return String.valueOf(comment);
    }

    public String getImage0() {
        if(imgUrl0 != null)
            return imgUrl0;
        else
            return "";
    }

    public String getImage1() {
        if(imgUrl1 != null)
            return imgUrl1;
        else
            return "";
    }

    public long getTimeStamp() {
        // transfer 'ms' to 's'.
        timeStamp =  StringUtil.string2Date(time).getTime() / 1000;
        return timeStamp;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        return obj instanceof BriefContent && ((BriefContent) obj).getId().equals(this.id);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put(FieldConstant.CONTENT_ID, id);
            json.put(FieldConstant.USER_ICON_URL, userIcon);
            json.put(FieldConstant.USER_ID, userId);
            json.put(FieldConstant.USER_NICKNAME, name);
            json.put(FieldConstant.CONTENT_URL, url);
            json.put(FieldConstant.CONTENT_TITLE, title);
            json.put(FieldConstant.CONTENT_TIME, time);
            json.put(FieldConstant.CONTENT, content);
            json.put(FieldConstant.CONTENT_TYPE, type);
            json.put(FieldConstant.CONTENT_RIGHTS, isPrivate);
            json.put(FieldConstant.CONTENT_READ_COUNT, readCount);
            json.put(FieldConstant.CONTENT_UP_VOTE_COUNT, upVote);
            json.put(FieldConstant.CONTENT_COMM_COUNT, comment);
            json.put(FieldConstant.CONTENT_IMG_URL1, imgUrl0);
            json.put(FieldConstant.CONTENT_IMG_URL2, imgUrl1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}