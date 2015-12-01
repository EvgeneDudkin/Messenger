package com.example.messengerpigeon.jsonRequest;

import com.example.messengerpigeon.jsonServerRequests.userAlreadyExistsException;
import com.example.messengerpigeon.miniClasses.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by Kirill2 on 26.11.2015.
 */
public class jsonRequest {
    public jsonRequest() {

    }

    private String type = "";
    private String strRequest = "";
    private JSONObject request = null;

    private String response = "";
    private Object[] fields = null;
    private String[] fieldsName = null;

    public Object Get(String fieldName) {
        for (int i = 0; i < fields.length; i++)
            if(Objects.equals(fieldName, fieldsName[i]))
                return fields[i];
        return null;
    }

    public String GetType() {
        return type;
    }

    public String GetStrRequest() {
        return strRequest;
    }

    public JSONObject GetRequest() {
        return request;
    }

    public jsonRequest(String type, Object... data) throws Exception {
        switch (type) {
            case "reg":
                if (data.length < 2) {
                    throw new Exception("Неправильное кол-во объектов");
                } else {
                    type = "reg";
                    String fName = data.length < 3 ? "" : (String) data[2];
                    String lName = data.length < 4 ? "" : (String) data[3];
                    regRequst((String) data[0], (String) data[1], fName, lName);
                }
                break;

        }
    }


    private boolean responseHandler(String input) {
        if (Objects.equals(type, "")) {
            return false;
        }
        switch (type) {
            case "reg":
                return regResponseHandler(input);
            default:
                return false;
        }
    }

    public void ErrorHandler() throws Exception {
        switch (response) {
            case "":
                throw new Exception("response not found");
            case "j1":
                throw new Exception("event handler :(");
        }
        switch (type) {
            case "reg":
                regErrorHandler();
        }
    }

    //region Registration
    private void regErrorHandler() throws Exception {
        switch (response) {
            case "r5":
                throw new Exception("в запросе либо login либо pass = null. Error r5");
            case "r4":
                throw new Exception("database query is bad :(. Error r4");
            case "r3":
                throw new userAlreadyExistsException("this user already exists. Error r3");
            case "r6":
                throw new Exception("database query is bad :(. Error r6");
            case "r7":
                throw new Exception("database query is bad :(. Error r7");
            case "r9":
                throw new Exception("database query is bad :(. Error r9");
            case "r8":
                throw new Exception("database query is bad :(. Error r8");
            case "a5":
                throw new Exception("token does not generate. Error a5");
        }
    }

    private void regRequst(String login, String pass, String fName, String lName) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONObject reg = new JSONObject();
        reg.put("login", login);
        reg.put("pass", pass);
        reg.put("firstName", fName);
        reg.put("lastName", lName);
        obj.put("reg", reg);
        strRequest = obj.toString();
        request = obj;
    }

    private boolean regResponseHandler(String input) {
        try {
            JSONObject ret = new JSONObject(input);

            fields = new Object[1];
            fields[0] = ret.get("token").toString();

            fieldsName = new String[1];
            fieldsName[0] = "token";
            response = ret.get("response").toString();

            //Режем response при ошибки, потому что "Error " в начале нам не нужно
            response = Objects.equals(response, "OK") ? response : response.substring(6);

            return true;
        } catch (Exception ignored) {
            fields = null;
            fieldsName = null;
            response = "j1";
            return false;
        }
    }
    //endregion

}
