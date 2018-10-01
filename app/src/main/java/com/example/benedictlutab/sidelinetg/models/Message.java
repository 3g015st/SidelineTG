package com.example.benedictlutab.sidelinetg.models;

/**
 * Created by Benedict Lutab on 9/30/2018.
 */

public class Message
{
    private String sender_id, message, date_sent;

    public Message(String message, String sender_id, String date_sent) {
        this.sender_id = sender_id;
        this.message = message;
        this.date_sent = date_sent;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getMessage() {
        return message;
    }

    public String getDate_sent() {
        return date_sent;
    }
}
