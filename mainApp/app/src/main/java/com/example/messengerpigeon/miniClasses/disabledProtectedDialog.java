package com.example.messengerpigeon.miniClasses;

import android.content.SharedPreferences;

import com.example.messengerpigeon.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kirill2 on 16.11.2015.
 */


public class disabledProtectedDialog {
    public int Id;
    public String Name;
    public String Login;
    public String Status;

    public disabledProtectedDialog(JSONObject fr) throws JSONException {
        Name = fr.isNull("name") ? "" : fr.getString("name");
        Id = fr.getInt("id");
        Login = fr.get("login").toString();
        Status = fr.get("status").toString();

    }

    public String getName() {
        if (Name.equals("")) return Login;
        return Name;
    }

    public String getStatus() {
        return Status;
    }
}
