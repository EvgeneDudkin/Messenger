package com.example.messengerpigeon.miniClasses;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        String dateStr=fr.isNull("datatime")?"":fr.getString("datatime");
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.ENGLISH);
        try {
             date = format.parse(dateStr);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        //TODO:
        //make date handler
    }
}
