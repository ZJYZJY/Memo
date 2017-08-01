package com.donutcn.memo.entity;

import com.donutcn.memo.base.Response;
import com.google.gson.annotations.Expose;

import org.json.JSONObject;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/1.
 */

public class ContentResponse extends Response<JSONObject> {

    @Expose
    private JSONObject data;

    public ContentResponse(String res) {
        super(res);
    }

    @Override
    public JSONObject getData() {
        return data;
    }
}
