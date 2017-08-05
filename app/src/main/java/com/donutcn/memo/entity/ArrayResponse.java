package com.donutcn.memo.entity;

import com.donutcn.memo.base.Response;
import com.google.gson.annotations.Expose;

import org.json.JSONException;

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
        if(data == null)
            return super.toString();
        else
            return super.toString() + data.toString();
    }
}
