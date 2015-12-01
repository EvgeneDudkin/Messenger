package com.example.messengerpigeon.jsonRequest;

import android.app.Dialog;
import android.provider.ContactsContract;

import com.example.messengerpigeon.jsonServerRequests.userAlreadyExistsException;
import com.example.messengerpigeon.miniClasses.dialog;
import com.example.messengerpigeon.miniClasses.friend;
import com.example.messengerpigeon.miniClasses.message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.Date;

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

    /**
     * геттер полей
     * @param fieldName имя поля
     * @return если найдет такое поле, то возвращает его, если нет, то null :(
     */
    public Object Get(String fieldName) {
        for (int i = 0; i < fields.length; i++)
            if (Objects.equals(fieldName, fieldsName[i]))
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

    //delete this useless code
    /*public jsonRequest(String type, Object... data) throws Exception {
        switch (type) {
            case "reg":
                regRequest(data);
                break;
            case "auth":
                authRequest(data);
                break;
            case "dl":
                dlRequest(data);
                break;
            default:
                return;
        }
        this.type = type;
    }*/


    private boolean responseHandler(String input) {
        if (Objects.equals(type, "")) {
            return false;
        }
        switch (type) {
            case "reg":
                return regResponseHandler(input);
            case "auth":
                return authResponseHandler(input);
            case "dl":
                return dlResponseHandler(input);
            case "lnm":
                return lnmResponseHandler(input);
            case "fl":
                return flResponseHandler(input);
            case "fs":
                return fsResponseHandler(input);
            case "sm":
                return smResponseHandler(input);
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
                break;
            case "auth":
                authErrorHandler();
                break;
            case "dl":
                dlErrorHandler();
                break;
            case "lnm":
                lnmErrorHandler();
                break;
            case "fl":
                flErrorHandler();
                break;
            case "fs":
                fsErrorHandler();
                break;
            case "sm":
                fsErrorHandler();
                break;

        }
    }

    //region send msg pls
    private void smErrorHandler() throws Exception {
        switch (response) {
            case "OK":
                return;
            default:
                throw new Exception(response);
        }
    }

    /**
     * @param token     токен
     * @throws Exception
     */
    public String sendMsgRequest(String token, int dialogId, String  msg) throws Exception {
        JSONObject obj = new JSONObject();
        JSONObject friendsS = new JSONObject();
        friendsS.put("token", token);
        friendsS.put("dialogId", dialogId);
        friendsS.put("msg", msg);
        obj.put("sendMsg", friendsS);
        type = "sm";
        request = obj;
        return strRequest = obj.toString();
    }

    /**
     * fieldsName = {"friends"}
     *
     * @param input input string KappaRoss
     * @return true - OK, false - NEOK
     */
    private boolean smResponseHandler(String input) {
        try {
            JSONObject ret = new JSONObject(input);

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

    //region friends search
    private void fsErrorHandler() throws Exception {
        switch (response) {
            case "OK":
                return;
            default:
                throw new Exception(response);
        }
    }

    /**
     * @param token     токен
     * @throws Exception
     */
    public String friendsSRequest(String token, String searchPattern) throws Exception {
        JSONObject obj = new JSONObject();
        JSONObject friendsS = new JSONObject();
        friendsS.put("token", token);
        friendsS.put("searchPattern", searchPattern);
        obj.put("friendsS", friendsS);
        type = "fs";
        request = obj;
        return strRequest = obj.toString();
    }

    /**
     * fieldsName = {"friends"}
     *
     * @param input input string KappaRoss
     * @return true - OK, false - NEOK
     */
    private boolean fsResponseHandler(String input) {
        try {
            JSONObject ret = new JSONObject(input);

            fields = new Object[1];
            fieldsName = new String[1];

            response = ret.get("response").toString();

            //Режем response при ошибки, потому что "Error " в начале нам не нужно
            response = Objects.equals(response, "OK") ? response : response.substring(6);

            JSONArray jsonDialogs = ret.getJSONArray("friends");
            friend[] friends = new friend[jsonDialogs.length()];
            for (int i = 0; i < jsonDialogs.length(); i++) {
                friends[i] = new friend(jsonDialogs.getJSONObject(i));
            }
            fields[1] = friends;
            fieldsName[1] = "friends";

            return true;
        } catch (Exception ignored) {
            fields = null;
            fieldsName = null;
            response = "j1";
            return false;
        }
    }
    //endregion

    //region friends List
    private void flErrorHandler() throws Exception {
        switch (response) {
            case "OK":
                return;
            default:
                throw new Exception(response);
        }
    }

    /**
     * @param token     токен
     * @throws Exception
     */
    public String friendsLRequest(String token) throws Exception {
        JSONObject obj = new JSONObject();
        JSONObject friendsL = new JSONObject();
        friendsL.put("token", token);
        obj.put("friendsL", friendsL);
        type = "fl";
        request = obj;
        return strRequest = obj.toString();
    }

    /**
     * fieldsName = {"friends"}
     *
     * @param input input string KappaRoss
     * @return true - OK, false - NEOK
     */
    private boolean flResponseHandler(String input) {
        try {
            JSONObject ret = new JSONObject(input);

            fields = new Object[1];
            fieldsName = new String[1];

            response = ret.get("response").toString();

            //Режем response при ошибки, потому что "Error " в начале нам не нужно
            response = Objects.equals(response, "OK") ? response : response.substring(6);

            JSONArray jsonDialogs = ret.getJSONArray("friends");
            friend[] friends = new friend[jsonDialogs.length()];
            for (int i = 0; i < jsonDialogs.length(); i++) {
                friends[i] = new friend(jsonDialogs.getJSONObject(i));
            }
            fields[1] = friends;
            fieldsName[1] = "friends";

            return true;
        } catch (Exception ignored) {
            fields = null;
            fieldsName = null;
            response = "j1";
            return false;
        }
    }
    //endregion

    //region Last N Messages
    private void lnmErrorHandler() throws Exception {
        switch (response) {
            case "OK":
                return;
            default:
                throw new Exception(response);
        }
    }

    /**
     * Последние N сообщений
     * <p/>
     * Если dateStart & idStart == null, то реквесто просто на последние N сообщений
     *
     * @param token     токен
     * @param dialogId  Идентификатор диалога
     * @param msgCount  кол-во сообщений
     * @param dateStart с какого времени начинать, можно ставить null
     * @param idStart   с какого сообщения начинать, -1 если null
     * @throws Exception
     */
    public String lastNmsgRequest(String token, int dialogId, int msgCount, Date dateStart, int idStart) throws Exception {
        JSONObject obj = new JSONObject();
        JSONObject lastNmsg = new JSONObject();
        lastNmsg.put("token", token);
        lastNmsg.put("dialogId", dialogId);
        lastNmsg.put("messageCount", msgCount);
        if (dateStart != null) {
            DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss zzz", Locale.ENGLISH);
            lastNmsg.put("dateStart", df.format(dateStart));
        } else if (idStart != -1) {
            lastNmsg.put("idStart", idStart);
        }
        obj.put("lastNmsg", lastNmsg);
        type = "lnm";
        request = obj;
        return strRequest = obj.toString();
    }

    /**
     * fieldsName = {"messages"}
     *
     * @param input input string KappaRoss
     * @return true - OK, false - NEOK
     */
    private boolean lnmResponseHandler(String input) {
        try {
            JSONObject ret = new JSONObject(input);

            fields = new Object[1];
            fieldsName = new String[1];

            response = ret.get("response").toString();

            //Режем response при ошибки, потому что "Error " в начале нам не нужно
            response = Objects.equals(response, "OK") ? response : response.substring(6);

            JSONArray jsonDialogs = ret.getJSONArray("messages");
            message[] messages = new message[jsonDialogs.length()];
            for (int i = 0; i < jsonDialogs.length(); i++) {
                messages[i] = new message(jsonDialogs.getJSONObject(i));
            }
            fields[1] = messages;
            fieldsName[1] = "messages";

            return true;
        } catch (Exception ignored) {
            fields = null;
            fieldsName = null;
            response = "j1";
            return false;
        }
    }
    //endregion

    //region Dialog List TODO НЕ ГОТОВО СДЕЛАТЬ НАДО АААААААААААА
    private void dlErrorHandler() throws Exception {
        switch (response) {
            case "OK":
                return;
            default:
                throw new Exception(response);
        }
    }

    public void dlRequest(String token) throws Exception {
        JSONObject obj = new JSONObject();
        JSONObject dialogsL = new JSONObject();
        dialogsL.put("token", token);
        obj.put("dialogsL", dialogsL);
        type = "dl";
        strRequest = obj.toString();
        request = obj;
    }

    private boolean dlResponseHandler(String input) {
        try {
            JSONObject ret = new JSONObject(input);

            fields = new Object[1];
            fieldsName = new String[1];

            response = ret.get("response").toString();

            //Режем response при ошибки, потому что "Error " в начале нам не нужно
            response = Objects.equals(response, "OK") ? response : response.substring(6);

            JSONArray jsonDialogs = ret.getJSONArray("dialogs");
            dialog[] dialogs = new dialog[jsonDialogs.length()];
            for (int i = 0; i < jsonDialogs.length(); i++) {
                dialogs[i] = new dialog(jsonDialogs.getJSONObject(i));
            }
            fields[1] = dialogs;
            fieldsName[1] = "dialogs";

            return true;
        } catch (Exception ignored) {
            fields = null;
            fieldsName = null;
            response = "j1";
            return false;
        }
    }
    //endregion

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

    public String regRequest(String login, String pass, String fName, String lName) throws Exception {
        JSONObject obj = new JSONObject();
        JSONObject reg = new JSONObject();
        reg.put("login", login);
        reg.put("pass", pass);
        reg.put("firstName", fName);
        reg.put("lastName", lName);
        obj.put("reg", reg);
        type = "reg";
        request = obj;
        return strRequest = obj.toString();
    }

    /**
     * fieldsName = {"token"}
     *
     * @param input input string KappaRoss
     * @return true - OK, false - NEOK
     */
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

    //region Authorisation
    private void authErrorHandler() throws Exception {
        switch (response) {
            case "OK":
                return;
            default:
                throw new Exception(response);
        }
    }

    public String authRequest(String login, String pass) throws Exception {
        JSONObject obj = new JSONObject();
        JSONObject auth = new JSONObject();
        auth.put("login", login);
        auth.put("pass", pass);
        obj.put("auth", auth);
        type = "auth";
        request = obj;
        return strRequest = obj.toString();
    }

    /**
     * fieldsName = {"token", "dialogs"}
     *
     * @param input input string KappaRoss
     * @return true - OK, false - NEOK
     */
    private boolean authResponseHandler(String input) {
        try {
            JSONObject ret = new JSONObject(input);

            fields = new Object[2];
            fieldsName = new String[2];

            fields[0] = ret.get("token").toString();
            fieldsName[0] = "token";

            response = ret.get("response").toString();

            //Режем response при ошибки, потому что "Error " в начале нам не нужно
            response = Objects.equals(response, "OK") ? response : response.substring(6);

            JSONArray jsonDialogs = ret.getJSONArray("dialogs");
            dialog[] dialogs = new dialog[jsonDialogs.length()];
            for (int i = 0; i < jsonDialogs.length(); i++) {
                dialogs[i] = new dialog(jsonDialogs.getJSONObject(i));
            }
            fields[1] = dialogs;
            fieldsName[1] = "dialogs";

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
