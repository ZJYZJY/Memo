package com.donutcn.memo.entity;

import com.donutcn.memo.constant.FieldConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/8/22.
 */

public class SyncInfoPackage {

    @Expose
    private int type;

    @Expose
    @SerializedName(FieldConfig.USER_ID)
    private String userId;

    public int getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }
}
