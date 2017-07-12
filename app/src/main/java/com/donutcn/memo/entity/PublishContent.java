package com.donutcn.memo.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import com.donutcn.memo.R;
import com.donutcn.memo.type.PublishType;

import java.util.ArrayList;

/**
 * com.donutcn.memo.entity
 * Created by 73958 on 2017/7/5.
 */

public class PublishContent implements Parcelable {

    /**
     * content type
     */
    private PublishType type;

    /**
     * whether publish by user himself
     */
    private boolean isMyPublish;

    /**
     * publish username
     */
    private String userName;

    /**
     * publish user icon
     */
    private String userIcon;

    /**
     * layout type
     */
    private int layoutType;

    /**
     * publish time
     */
    private String publishTime;

    /**
     * content title
     */
    private String title;

    /**
     * content
     */
    private String content;

    /**
     * picture urls
     */
    private ArrayList<String> pictures;

    /**
     * up vote count
     */
    private int upVoteCount;

    /**
     * browse count
     */
    private int browseCount;

    public PublishContent(PublishType type, String userName, String userIcon, String publishTime, String title, String content,
                          int upVoteCount, int browseCount) {
        this.type = type;
        this.userName = userName;
        this.userIcon = userIcon;
        this.publishTime = publishTime;
        this.title = title;
        this.content = content;
        this.upVoteCount = upVoteCount;
        this.browseCount = browseCount;
        this.layoutType = 0;
    }

    public PublishContent(PublishType type, String userName, String userIcon, String publishTime, String title, String content,
                          int upVoteCount, int browseCount, ArrayList<String> pictures) {
        this.type = type;
        this.userName = userName;
        this.userIcon = userIcon;
        this.publishTime = publishTime;
        this.title = title;
        this.content = content;
        this.upVoteCount = upVoteCount;
        this.browseCount = browseCount;
        this.pictures = pictures;
        this.layoutType = 1;
    }

    /**
     * set icon for different content type.
     * @param type content type.
     */
    @DrawableRes
    public int getTypeIcon(PublishType type){
        switch (type){
            case ARTICLE:return R.drawable.type_article;
            case ALBUM:return R.drawable.type_album;
            case ACTIVITY:return R.drawable.type_activity;
            case VOTE:return R.drawable.type_vote;
            case RESERVE:return R.drawable.type_reserve;
            case RECRUIT:return R.drawable.type_recruit;
            default:return R.drawable.type_article;
        }
    }

    /**
     * set Background Resource for different content type.
     * @param type content type.
     */
    @DrawableRes
    public int getTypeBackground(PublishType type){
        switch (type){
            case ARTICLE:
            case ALBUM:return R.drawable.bg_green;
            case ACTIVITY:
            case VOTE:
            case RESERVE:
            case RECRUIT:return R.drawable.bg_blue;
            default:return R.drawable.bg_green;
        }
    }

    public PublishType getType() {
        return type;
    }

    public void setType(PublishType type) {
        this.type = type;
    }

    public boolean isMyPublish() {
        return isMyPublish;
    }

    public void setMyPublish(boolean myPublish) {
        this.isMyPublish = myPublish;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public int getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
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

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

    public int getUpVoteCount() {
        return upVoteCount;
    }

    public void setUpVoteCount(int upVoteCount) {
        this.upVoteCount = upVoteCount;
    }

    public int getBrowseCount() {
        return browseCount;
    }

    public void setBrowseCount(int browseCount) {
        this.browseCount = browseCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeByte(this.isMyPublish ? (byte) 1 : (byte) 0);
        dest.writeString(this.userName);
        dest.writeString(this.userIcon);
        dest.writeInt(this.layoutType);
        dest.writeString(this.publishTime);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeStringList(this.pictures);
        dest.writeInt(this.upVoteCount);
        dest.writeInt(this.browseCount);
    }

    protected PublishContent(Parcel in) {
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : PublishType.values()[tmpType];
        this.isMyPublish = in.readByte() != 0;
        this.userName = in.readString();
        this.userIcon = in.readString();
        this.layoutType = in.readInt();
        this.publishTime = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.pictures = in.createStringArrayList();
        this.upVoteCount = in.readInt();
        this.browseCount = in.readInt();
    }

    public static final Creator<PublishContent> CREATOR = new Creator<PublishContent>() {
        @Override
        public PublishContent createFromParcel(Parcel source) {
            return new PublishContent(source);
        }

        @Override
        public PublishContent[] newArray(int size) {
            return new PublishContent[size];
        }
    };
}
