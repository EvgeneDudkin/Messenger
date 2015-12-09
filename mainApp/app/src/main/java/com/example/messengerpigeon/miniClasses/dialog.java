package com.example.messengerpigeon.miniClasses;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.messengerpigeon.MainActivity;

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
        if(fr.has("publicKey1")) {
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
    }

    public String getName(){
        if(Name.equals("")) return Login;
        return Name;
    }
}
