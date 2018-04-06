package com.foodfriend.foodfriend;

/**
 * Created by Dan on 15/03/2018.
 */

public class Message {

    private String content;
    private String username;
    private String time; //also used to hold imageUrl

    public Message() {

    }

    public Message(String content, String username, String time) {
        this.content = content;
        this.username = username;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
