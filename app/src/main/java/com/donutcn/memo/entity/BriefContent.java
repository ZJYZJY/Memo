package com.donutcn.memo.entity;

import com.donutcn.memo.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/1.
 */

public class BriefContent {

    @Expose
    @SerializedName(value = "article_id")
    private String id;

    @Expose
    @SerializedName(value = "user_id")
    private String userId;

    @Expose
    @SerializedName(value = "name")
    private String name;

    @Expose
    @SerializedName(value = "head_portrait")
    private String userIcon;

    @Expose
    @SerializedName(value = "url")
    private String url;

    @Expose
    @SerializedName(value = "title")
    private String title;

    @Expose
    @SerializedName(value = "create_time")
    private String time;

    @Expose
    @SerializedName(value = "content")
    private String content;

    @Expose
    @SerializedName(value = "type")
    private String type;

    @Expose
    @SerializedName(value = "is_private")
    private int isPrivate;

    @Expose
    @SerializedName(value = "read_total")
    private int readCount;

    @Expose
    @SerializedName(value = "praise")
    private int upVote;

    @Expose
    @SerializedName(value = "comment_total")
    private int comment;

    @Expose
    @SerializedName(value = "url1")
    private String imgUrl0;

    @Expose
    @SerializedName(value = "url2")
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

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("article_id", id);
            json.put("head_portrait", userIcon);
            json.put("user_id", userId);
            json.put("name", name);
            json.put("url", url);
            json.put("title", title);
            json.put("create_time", time);
            json.put("content", content);
            json.put("type", type);
            json.put("is_private", isPrivate);
            json.put("read_total", readCount);
            json.put("praise", upVote);
            json.put("comment_total", comment);
            json.put("url1", imgUrl0);
            json.put("url2", imgUrl1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}