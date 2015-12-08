package com.example.messengerpigeon.miniClasses;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kirill2 on 16.11.2015.
 */
public class dialog {
    public int Id;
    public String Name;
    public String Login;

    public dialog(JSONObject fr) throws JSONException {
        Name = fr.isNull("name") ? "" : fr.getString("name");
        Id = fr.getInt("id");
        Login=fr.get("login").toString();
    }

    public String getName(){
        if(Name.equals("")) return Login;
        return Name;
    }
}
