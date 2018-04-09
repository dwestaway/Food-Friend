package com.foodfriend.foodfriend;

/**
 * Created by Dan on 15/03/2018.
 */

public class Message {

    private String content;
    private String sentToName;
    private String time; //also used to hold imageUrl, this prevents having to create a second Message object

    public Message() {

    }

    public Message(String content, String sentToName, String time) {
        this.content = content;
        this.sentToName = sentToName;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSentToName() {
        return sentToName;
    }

    public void setSentToName(String username) {
        this.sentToName = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
