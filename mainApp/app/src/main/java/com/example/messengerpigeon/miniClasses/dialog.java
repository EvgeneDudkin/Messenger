package com.example.messengerpigeon.miniClasses;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.messengerpigeon.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Kirill2 on 16.11.2015.
 */


public class dialog implements Comparable<dialog> {
    public int Id;
    public String Name;
    public String Login;
    public Date date;
    public String LastMsg;

    public dialog(JSONObject fr) throws JSONException {
        Name = fr.isNull("name") ? "" : fr.getString("name");
        Id = fr.getInt("id");
        Login = fr.get("login").toString();
        if (fr.has("publicKey1")) {
            String APP_PREFERENCES__PUBLIK_KEY1_COUNTER = "DIALOG_KEY1_" + Id;
            String publicKey = fr.getString("publicKey1");
            String APP_PREFERENCES__PUBLIK_KEY2_COUNTER = "DIALOG_KEY2_" + Id;
            String publicKey2 = fr.getString("publicKey2");

            System.out.println("HELOOOOOOOOOOOOOOOOOOOOOOOOOO");

            SharedPreferences.Editor editor = MainActivity.mSettings.edit();
            editor.putString(APP_PREFERENCES__PUBLIK_KEY1_COUNTER, publicKey);
            editor.apply();

            System.out.println("HELOOOOOOOOOOOOOOOOOOOOOOOOOO2");

            editor.putString(APP_PREFERENCES__PUBLIK_KEY2_COUNTER, publicKey2);
            editor.apply();
        }
        String dateStr = fr.isNull("date") ? "" : fr.getString("date");
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.ENGLISH);
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LastMsg = fr.get("msg").toString();
    }

    public String getName() {
        if (Name.equals("")) return Login;
        return Name;
    }

    public String getDate(){
        Date now=new Date();
        if (date.getTime() + (1000 * 60 * 60 * 24) - (1000 * 60 * 60 * 4) > now.getTime()) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(date);
        }
        else{
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM");
            return sdf.format(date);
        }
    }

    public String getLastMsg(){
        if (Objects.equals(LastMsg, "null")){
            System.out.println("pusto");
            return "Нет сообщений";
        }
        else {
            return LastMsg;
        }
    }

    public int compareTo(dialog o) {
        return o.date.getTime() > this.date.getTime() ? 1 : o.date.getTime() == this.date.getTime() ? 0 : -1;
    }
}
