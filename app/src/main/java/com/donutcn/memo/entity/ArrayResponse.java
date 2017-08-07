package com.donutcn.memo.entity;

import com.donutcn.memo.base.Response;
import com.google.gson.annotations.Expose;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/1.
 */

public class ArrayResponse<E> extends Response<ArrayList<E>> {

    @Expose
    private ArrayList<E> data;

    public ArrayResponse(String res) throws JSONException {
        super(res);
    }

    @Override
    public ArrayList<E> getData() {
        return data;
    }

    public int size(){
        return data == null ? 0 : data.size();
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.put("code", getCode());
            json.put("message", getMessage());
            JSONArray array = new JSONArray();
            if(data != null) {
                for (E elem : data) {
                    array.put(elem.toString());
                }
            }
            json.put("data", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
