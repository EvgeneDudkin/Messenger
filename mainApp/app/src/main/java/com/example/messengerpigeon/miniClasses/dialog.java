package com.example.messengerpigeon.miniClasses;

import com.example.messengerpigeon.Messages_Item.Messages_Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kirill2 on 16.11.2015.
 */
public class dialog implements Comparable<dialog>{
    public int Id;
    public String Name;
    public String Login;
    public Date date;

    public dialog(JSONObject fr) throws JSONException {
        Name = fr.isNull("name") ? "" : fr.getString("name");
        Id = fr.getInt("id");
        Login=fr.get("login").toString();
        String dateStr = fr.isNull("date") ? "" : fr.getString("date");
        DateFormat format = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.ENGLISH);
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getName(){
        if(Name.equals("")) return Login;
        return Name;
    }

    public int compareTo(dialog o){
        return o.date.getTime()>this.date.getTime()?1:o.date.getTime()==this.date.getTime()?0:-1;
    }

}
