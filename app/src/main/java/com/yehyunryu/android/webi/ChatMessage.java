package com.yehyunryu.android.webi;

/**
 * Copyright 2018 Yehyun Ryu, Nikhil Shahi, Issac Gullickson, Joshua Palm

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

public class ChatMessage {
    private long timeStamp;
    private String userId;
    private String name;
    private String text;
    private String profile;

    public ChatMessage() {}

    public ChatMessage(long timeStamp, String text) {
        this.timeStamp = timeStamp;
        this.text = text;
    }

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
