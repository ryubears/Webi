package com.yehyunryu.android.webi;

/**
 * Created by Yehyun Ryu on 1/28/2018.
 */

public class User {
    private String name;
    private String profileUrl;

    public User() {}

    public User(String name, String profileUrl) {
        this.name = name;
        this.profileUrl = profileUrl;
    }

    public String getName() {
        return name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
