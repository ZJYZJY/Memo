package com.donutcn.memo.entity;

import com.donutcn.memo.base.Response;
import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/7/31.
 */

public class SimpleResponse extends Response {

    @Expose
    private JSONObject data;

    public SimpleResponse(String result) throws JSONException {
        super(result);
        if(getData() != null){
            data = (JSONObject) getData();
        }
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
