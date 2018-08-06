package com.example.benedictlutab.sidelinetg.models;

/**
 * Created by Benedict Lutab on 8/6/2018.
 */

public class Offer
{
    private String task_id, tasker_id, profile_picture, first_name, last_name, rating, reviews, amount, message;

    public Offer(String task_id, String tasker_id, String profile_picture, String first_name, String last_name, String rating, String reviews,  String amount,  String message)
    {
        this.task_id = task_id;
        this.tasker_id = tasker_id;
        this.profile_picture = profile_picture;
        this.first_name = first_name;
        this.last_name = last_name;
        this.rating = rating;
        this.reviews = reviews;
        this.amount = amount;
        this.message = message;
    }

    public String getTask_id() {
        return task_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getRating() {
        return rating;
    }

    public String getReviews() {
        return reviews;
    }

    public String getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public String getTasker_id() {
        return tasker_id;
    }

    public String getProfile_picture() {
        return profile_picture;
    }
}
