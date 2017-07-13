package com.donutcn.memo.type;

import java.util.ArrayList;

/**
 * com.donutcn.memo.type
 * Created by 73958 on 2017/7/11.
 */

public enum PublishType {

    ARTICLE("文章"),
    ALBUM("相册"),
    ACTIVITY("活动"),
    VOTE("投票"),
    RECRUIT("招聘"),
    QA("问答"),
    RESERVE("预订"),
    SALE("二手");

    private String mType;

    PublishType(String type){
        this.mType = type;
    }

    public static String[] toStringArray(){
        ArrayList<String> types = new ArrayList<>();
        for(PublishType type : PublishType.values()){
            types.add(type.toString());
        }
        String[] array = new String[types.size()];
        return types.toArray(array);
    }

    public static PublishType getType(String type){
        for(PublishType item : PublishType.values()){
            if(item.toString() == type)
                return item;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.mType;
    }
}
