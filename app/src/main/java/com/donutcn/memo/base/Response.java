package com.donutcn.memo.base;

import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * com.donutcn.memo.base
 * Created by 73958 on 2017/7/31.
 */

public abstract class Response<T> {

    /** http request state code. */
    @Expose
    private int code;

    /** http request response message. */
    @Expose
    private String message;

    /** Successful request. */
    private static final int SUCCESS = 200;

    /** Bad Request. */
    private static final int FAIL = 400;

    /** Unauthorized request. */
    private static final int UNAUTHORIZED = 401;

    public Response(String res) {
        try {
            JSONObject result = new JSONObject(res);
            this.code = result.getInt("code");
            this.message = result.getString("message");
        } catch (JSONException e) {
            this.code = FAIL;
            this.message = "null";
            e.printStackTrace();
        }
    }

    public boolean isOk() {
        return code == SUCCESS;
    }

    public boolean unAuthorized(){
        return code == UNAUTHORIZED;
    }

    public int getCode(){
        return code;
    }

    public String getMessage() {
        return message;
    }

    public abstract T getData();

    @Override
    public String toString() {
        return "code => " + code + "\n" + "message => " + message + "\n";
    }
}
