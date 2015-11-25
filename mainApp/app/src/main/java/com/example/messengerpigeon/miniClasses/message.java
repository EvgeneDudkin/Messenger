package com.example.messengerpigeon.miniClasses;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by egor on 25.11.2015.
 */
public class message {
    public int senderId;
    public String login;
    public String text;
    public Date date;

    public message(JSONObject fr) throws JSONException {
        login = fr.isNull("login") ? "" : fr.getString("login");
        senderId = fr.getInt("senderId");
        text = fr.isNull("text") ? "" : fr.getString("text");
        //TODO:
        //make date handler
    }
}
