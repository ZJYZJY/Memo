package com.donutcn.memo.type;

import java.util.ArrayList;

/**
 * com.donutcn.memo.type
 * Created by 73958 on 2017/7/11.
 */

public enum PublishType {

    ARTICLE("文章", "评论"),
    ALBUM("相册", "评论"),
    ACTIVITY("活动", "消息"),
    VOTE("投票", "消息"),
    RECRUIT("招聘", "消息"),
    QA("问答", "消息"),
    RESERVE("预订", "消息"),
    SALE("二手", "消息");

    private String mType;
    private String mReply;

    PublishType(String type, String reply){
        this.mType = type;
        this.mReply = reply;
    }

    /**
     * Get the String[] for all PublishType.
     */
    public static String[] toStringArray(){
        ArrayList<String> types = new ArrayList<>();
        for(PublishType type : PublishType.values()){
            types.add(type.toString());
        }
        String[] array = new String[types.size()];
        return types.toArray(array);
    }

    /**
     * Get the enum type of the string
     * @param type type string
     * @return PublishType
     */
    public static PublishType getType(String type){
        for(PublishType item : PublishType.values()){
            if(item.toString().equals(type))
                return item;
        }
        return null;
    }

    public String getReply(){
        return this.mReply;
    }

    @Override
    public String toString() {
        return this.mType;
    }
}
