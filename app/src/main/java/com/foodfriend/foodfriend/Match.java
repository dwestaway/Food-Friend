package com.foodfriend.foodfriend;

/**
 * Created by Dan on 03/04/2018.
 */

public class Match {
    private String image;
    private String name;


    public Match(String image, String name) {
        this.image = image;
        this.name = name;

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


}
