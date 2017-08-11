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

    /** Not Found . */
    private static final int NOT_FOUND = 404;

    public boolean isOk() {
        return code == SUCCESS;
    }

    public boolean unAuthorized(){
        return code == UNAUTHORIZED;
    }

    public boolean isFail(){
        return code == FAIL;
    }

    public boolean notFound(){
        return code == NOT_FOUND;
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
