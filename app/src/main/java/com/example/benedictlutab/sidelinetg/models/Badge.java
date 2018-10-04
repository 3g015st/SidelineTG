package com.example.benedictlutab.sidelinetg.models;

/**
 * Created by Benedict Lutab on 10/4/2018.
 */

public class Badge
{
    private String badge_id, name, description, image;

    public Badge(String badge_id, String name, String description, String image) {
        this.badge_id = badge_id;
        this.image = image;
        this.name = name;
        this.description = description;
    }

    public String getBadge_id() {
        return badge_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }
}
