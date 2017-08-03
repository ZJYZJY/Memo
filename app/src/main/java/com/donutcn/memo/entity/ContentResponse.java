package com.donutcn.memo.entity;

import com.donutcn.memo.base.Response;
import com.donutcn.memo.type.PublishType;
import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;

import java.util.LinkedHashMap;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/1.
 */

public class ContentResponse extends Response<LinkedHashMap> {

    @Expose
    private LinkedHashMap data;

    public ContentResponse(String res) {
        super(res);
    }

    @Override
    public LinkedHashMap getData() {
        return data;
    }

    private LinkedTreeMap getContent() {
        return (LinkedTreeMap) data.get("article");
    }

    public String getTitle() {
        return (String) getContent().get("title");
    }

    public String getContentStr() {
        return (String) getContent().get("content");
    }

    public PublishType getType() {
        return PublishType.getType((String) getContent().get("type"));
    }

    public String getAuthor() {
        return (String) getContent().get("name");
    }

    public String getDate() {
        String date = (String) getContent().get("create_time");
        return date.substring(0, 10);
    }

    public String getReadCount() {
        return (String) getContent().get("read_total");
    }

    public String getUpvote() {
        return (String) getContent().get("praise");
    }

    public String getCommentCount() {
        return (String) getContent().get("comment_total");
    }

    @Override
    public String toString() {
        if(data == null)
            return super.toString();
        else
            return super.toString() + data.toString();
    }
}
