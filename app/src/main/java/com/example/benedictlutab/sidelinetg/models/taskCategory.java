package com.example.benedictlutab.sidelinetg.models;

/**
 * Created by Benedict Lutab on 7/19/2018.
 */

public class taskCategory
{
    private String task_category_id, name, minimum_payment, task_category_img;
    public taskCategory(String task_category_id, String name, String minimum_payment, String task_category_img)
    {
        this.task_category_id = task_category_id;
        this.name = name;
        this.minimum_payment = minimum_payment;
        this.task_category_img = task_category_img;
    }

    public String getTask_category_id() {
        return task_category_id;
    }

    public String getName() {
        return name;
    }

    public String getMinimum_payment() {
        return minimum_payment;
    }

    public String getTask_category_img() {
        return task_category_img;
    }
}
