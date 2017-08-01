package com.donutcn.memo.entity;

import com.donutcn.memo.base.Response;
import com.google.gson.annotations.Expose;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/1.
 */

public class ArrayResponse extends Response {

    @Expose
    private JSONArray data;

    public ArrayResponse(String res) {
        super(res);
        if(getData() != null){
            data = (JSONArray) getData();
        }
    }

    public JSONObject get(int index) throws JSONException {
        return (ContentList) data.get(index);
    }

    class ContentList extends JSONObject {

        @Expose
        private JSONObject json;
        private String title;
        private String time;
        private String content;
        private String type;
        private int upVote;
        private int comment;
        private String imgUrl0;
        private String imgUrl1;

        public ContentList(JSONObject json) throws JSONException {
            this.json = json;
            this.title = json.getString("title");
            this.time = json.getString("create_time");
            this.content = json.getString("content");
            this.type = json.getString("type");
            this.upVote = json.getInt("praise");
            this.comment = json.getInt("comment_total");
            this.imgUrl0 = json.getString("url1");
            this.imgUrl1 = json.getString("url2");
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

        public int getUpVote() {
            return upVote;
        }

        public int getComment() {
            return comment;
        }

        public String getImage0() {
            return imgUrl0;
        }

        public String getImage1() {
            return imgUrl1;
        }
    }
}
