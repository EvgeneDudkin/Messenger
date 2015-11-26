package com.example.messengerpigeon.miniClasses;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kirill2 on 16.11.2015.
 */
public class friend {
    public int Id;
    public String Login;
    public String FirstName;
    public String LastName;

    public friend(JSONObject fr) throws JSONException {
        Login = fr.getString("login");
        FirstName = fr.isNull("firstName") ? "" : fr.getString("firstName");
        LastName = fr.isNull("lastName") ? "" :fr.getString("lastName");
        Id = fr.getInt("id");
    }
}
