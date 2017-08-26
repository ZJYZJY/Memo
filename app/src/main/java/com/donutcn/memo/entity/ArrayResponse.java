package com.donutcn.memo.entity;

import com.donutcn.memo.base.Response;
import com.donutcn.memo.interfaces.Jsonify;
import com.google.gson.annotations.Expose;

import org.json.JSONArray;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/1.
 */

public class ArrayResponse<E extends Jsonify> extends Response<List<E>> {

    @Expose
    private LinkedHashMap info;

    @Expose
    private List<E> data;

    @Override
    public List<E> getData() {
        return data;
    }

    public LinkedHashMap getInfo() {
        return info;
    }

    public int size(){
        return data == null ? 0 : data.size();
    }

    /**
     * used for user's content list cache.
     * @return string array of user content list.
     */
    @Override
    public String toString() {
        JSONArray array = new JSONArray();
        if (data != null) {
            for (E elem : data) {
                array.put(elem.toJson());
            }
        }
        return array.toString();
    }
}
