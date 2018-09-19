package com.example.benedictlutab.sidelinetg.models;

/**
 * Created by Benedict Lutab on 7/30/2018.
 */

public class Skill
{
    private String skill_id, name;

    public Skill(String skill_id, String name)
    {
        this.skill_id = skill_id;
        this.name = name;
    }

    public String getSkill_id() {
        return skill_id;
    }

    public String getName() {
        return name;
    }
}
