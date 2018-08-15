package com.example.benedictlutab.sidelinetg.models;

/**
 * Created by Benedict Lutab on 8/13/2018.
 */

public class ChatRoom
{

    String task_id, tasker_id, task_giver_id, profile_picture, first_name, last_name, title;

    public ChatRoom(String task_id, String tasker_id, String task_giver_id, String profile_picture, String first_name ,String last_name ,String title)
    {
        this.task_id = task_id;
        this.tasker_id = tasker_id;
        this.task_giver_id = task_giver_id;
        this.profile_picture = profile_picture;
        this.first_name = first_name;
        this.last_name = last_name;
        this.title = title;
    }

    public String getTask_id() {
        return task_id;
    }

    public String getTasker_id() {
        return tasker_id;
    }

    public String getTask_giver_id() {
        return task_giver_id;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getTitle() {
        return title;
    }
}
