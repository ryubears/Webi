package com.yehyunryu.android.webi;

/**
 * Created by Yehyun Ryu on 1/27/2018.
 */

public class ChatMessage {
    private long timeStamp;
    private long userId;
    private String name;
    private String text;
    private String profile;

    public ChatMessage(long timeStamp, long userId, String name, String text, String profile) {
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.name = name;
        this.text = text;
        this.profile = profile;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public long getUserId() {
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
