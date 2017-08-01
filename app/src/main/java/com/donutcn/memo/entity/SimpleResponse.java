package com.donutcn.memo.entity;

import com.donutcn.memo.base.Response;
import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/7/31.
 */

public class SimpleResponse extends Response<JSONObject> {

    @Expose
    private JSONObject data;

    public SimpleResponse(String res) throws JSONException {
        super(res);
        data = getData();
    }

    @Override
    public JSONObject getData() {
        return data;
    }

    public String getField(String key){
        if(data == null)
            return "";
        try {
            return data.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
