package com.donutcn.memo.base;

import com.donutcn.memo.utils.HttpUtils;
import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * com.donutcn.memo.base
 * Created by 73958 on 2017/7/31.
 */

public abstract class Response {

    private JSONObject result;

    /**
     * http request state code.
     */
    @Expose
    private int code;

    /**
     * http request response message.
     */
    @Expose
    private String message;

    /**
     * extra data field.
     */
    private Object data;

    public Response(String res) {
        try {
            this.result = new JSONObject(res);
            this.code = result.getInt("code");
            this.message = result.getString("message");
            this.data = result.get("data");
        } catch (JSONException e) {
            this.code = HttpUtils.FAIL;
            this.message = "null";
            e.printStackTrace();
        }
    }

    public boolean isOk() {
        return code == HttpUtils.SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public Object getData(){
        return data;
    }
}
