package com.example.benedictlutab.sidelinetg.models;

/**
 * Created by Benedict Lutab on 7/26/2018.
 */

public class Task
{
    private String task_id, title, image_one, date_time_end, address, task_fee, status, category, date_completed;

    public Task(String task_id, String title, String image_one, String date_time_end, String address, String task_fee, String status)
    {
        this.task_id = task_id;
        this.title = title;
        this.image_one = image_one;
        this.date_time_end = date_time_end;
        this.address = address;
        this.task_fee = task_fee;
        this.status = status;
    }

    public Task(String task_id, String title, String category, String date_completed)
    {
        this.task_id = task_id;
        this.title = title;
        this.category = category;
        this.date_completed = date_completed;
    }

    public String getTask_id() {
        return task_id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage_one() {
        return image_one;
    }

    public String getDate_time_end() {
        return date_time_end;
    }

    public String getAddress() {
        return address;
    }

    public String getTask_fee() {
        return task_fee;
    }

    public String getStatus() {
        return status;
    }

    public String getCategory() {
        return category;
    }

    public String getDate_completed() {
        return date_completed;
    }
}
