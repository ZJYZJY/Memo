package com.donutcn.memo.entity;

import com.donutcn.memo.base.Response;
import com.google.gson.annotations.Expose;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/1.
 */

public class ArrayResponse<E> extends Response<List<E>> {

    @Expose
    private List<E> data;

    @Override
    public List<E> getData() {
        return data;
    }

    public int size(){
        return data == null ? 0 : data.size();
    }

    @Override
    public String toString() {
        JSONArray array = new JSONArray();
        if(data != null) {
            for (E elem : data) {
                array.put(((BriefContent)elem).toJson());
            }
        }
        return array.toString();
    }
}
