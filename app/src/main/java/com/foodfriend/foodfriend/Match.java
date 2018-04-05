package com.foodfriend.foodfriend;

/**
 * Created by Dan on 03/04/2018.
 */

public class Match {
    private String image;
    private String name;
    private String poi;

    public Match(String image, String name, String poi) {
        this.image = image;
        this.name = name;
        this.poi = poi;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }
}
