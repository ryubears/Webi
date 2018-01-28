package com.yehyunryu.android.webi;

/**
 * Created by Yehyun Ryu on 1/27/2018.
 */

public class ChatMessage {
    private long timeStamp;
    private String userId;
    private String name;
    private String text;
    private String profile;

    public ChatMessage() {}

    public ChatMessage(long timeStamp, String userId, String name, String text, String profile) {
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.name = name;
        this.text = text;
        this.profile = profile;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getProfile() {
        return profile;
    }
}
