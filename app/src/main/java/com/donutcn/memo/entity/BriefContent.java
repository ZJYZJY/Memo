package com.donutcn.memo.entity;

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
    @SerializedName(value = "name")
    private String name;

    @Expose
    @SerializedName(value = "head_portrait")
    private String userIcon;

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

    public BriefContent(JSONObject json) throws JSONException {
        this.id = json.getString("article_id");
        this.userIcon = json.getString("head_portrait");
        this.name = json.getString("name");
        this.title = json.getString("title");
        this.time = json.getString("create_time");
        this.content = json.getString("content");
        this.type = json.getString("type");
        this.readCount = json.getInt("read_total");
        this.upVote = json.getInt("praise");
        this.comment = json.getInt("comment_total");
        this.imgUrl0 = json.getString("url1");
        this.imgUrl1 = json.getString("url2");
    }

    public String getId() {
        return id;
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
        return imgUrl0;
    }

    public String getImage1() {
        return imgUrl1;
    }
}