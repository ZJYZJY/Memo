package com.donutcn.memo.entity;

import com.donutcn.memo.constant.FieldConstant;
import com.donutcn.memo.interfaces.Jsonify;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/9/28.
 */

public class Template extends Jsonify {

    @Expose
    @SerializedName(FieldConstant.TEMPLATE_ID)
    public String templateId;

    @Expose
    @SerializedName(FieldConstant.TEMPLATE_TITLE)
    public String title;

    @Expose
    @SerializedName(FieldConstant.TEMPLATE_CONTENT)
    public String content;

    @Expose
    @SerializedName(FieldConstant.TEMPLATE_SHORT_CUT)
    public String shortCut;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getShortCut() {
        return shortCut;
    }

    public void setShortCut(String shortCut) {
        this.shortCut = shortCut;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }
}
