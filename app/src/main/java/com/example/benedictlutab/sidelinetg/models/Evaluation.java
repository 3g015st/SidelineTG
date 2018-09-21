package com.example.benedictlutab.sidelinetg.models;

/**
 * Created by Benedict Lutab on 8/24/2018.
 */

public class Evaluation
{
    String full_name, profile_picture, date_time_sent, review, rating, title;

    public Evaluation(String full_name, String profile_picture, String review, String date_time_sent, String rating, String title)
    {
        this.full_name = full_name;
        this.profile_picture = profile_picture;
        this.review = review;
        this.rating = rating;
        this.title = title;
        this.date_time_sent = date_time_sent;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public String getDate_time_sent() {
        return date_time_sent;
    }

    public String getReview() {
        return review;
    }

    public String getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }
}
