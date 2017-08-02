package com.donutcn.memo.entity;

import com.donutcn.memo.base.Response;
import com.google.gson.annotations.Expose;

import java.util.LinkedHashMap;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/7/31.
 */

public class SimpleResponse extends Response<LinkedHashMap> {

    @Expose
    private LinkedHashMap data;

    public SimpleResponse(String res) {
        super(res);
    }

    @Override
    public LinkedHashMap getData() {
        return data;
    }

    public  <T> T getField(String key){
        if(data == null)
            return null;
        return (T) data.get(key);
    }
}
