package com.example.messengerpigeon.jsonServerRequests;

import com.example.messengerpigeon.miniClasses.dialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by Пользователь on 08.12.2015.
 */
public class listDialogsRequest extends jsonServerRequests {
    /**
     * Токен, который вернул сервер
     */
    private static String token = "";
    /**
     * Список друзей
     */
    public static dialog[] dialogs = null;

    /**
     * Пустой конструктор
     */
    public listDialogsRequest() {

    }

    public static String MyLogin = "";

    /**
     * Конструктор
     */
    public listDialogsRequest(String token) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONObject auth = new JSONObject();
        auth.put("token", token);
        obj.put("dialogsL", auth);
        strRequest = obj.toString();
        jsonRequest = obj;
    }

    /**
     * Метод создания запроса
     */
    public void createRequest(String token) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONObject auth = new JSONObject();
        auth.put("token", token);
        obj.put("dialogsL", auth);
        strRequest = obj.toString();
        jsonRequest = obj;
    }


    public void createProtectedRequest(String token) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONObject auth = new JSONObject();
        auth.put("token", token);
        obj.put("dialogsL", auth);
        strProtectedRequest = obj.toString();
        jsonProtectedRequest = obj;
    }


    /**
     * override Обработчик ответа сервера
     *
     * @param input Строка, которую вернул сервер
     */
    public void responseHandler(String input) {
        try {
            JSONObject ret = new JSONObject(input);
            response = ret.get("response").toString();

            response = Objects.equals(response, "OK") ? response : response.substring(6);
            JSONArray jsonDialogs = ret.getJSONArray("dialogs");
            dialogs = new dialog[jsonDialogs.length()];
            for (int i = 0; i < jsonDialogs.length(); i++) {
                dialogs[i] = new dialog(jsonDialogs.getJSONObject(i));
            }
            List<dialog> dialogList = Arrays.asList(dialogs);
            Collections.sort(dialogList);
            dialogList.toArray(dialogs);
        } catch (Exception ignored) {
            token = "";
            response = "j1";
            dialogs = null;
        }
    }

    /**
     * override Обработчик ошибок.
     *
     * @throws Exception
     */
    public void errorHandler() throws Exception {
        super.errorHandler();
        switch (response) {
            case "a8":
                throw new Exception("login or pass = null. Error a8");
            case "a4":
                throw new Exception("database query is bad :(. Error a4");
            case "a3":
                throw new userNotFoundException("user not found. Error a3");
            case "a6":
                throw new Exception("database query is bad :(. Error a6");
            case "a7":
                throw new Exception("database query is bad :(. Error a7");
            case "a5":
                throw new Exception("token does not generate. Error a5");
        }
    }

    /**
     * Геттер токена
     *
     * @return токен
     */
    public static String getToken() {
        return token;
    }

    /**
     * Геттер реакции сервера
     *
     * @return реакция сервера
     */
    public String getResponse() {
        return response;
    }

    /**
     * Геттер списка друзей
     *
     * @return список друзей
     */
    public static dialog[] getDialogs() {
        return dialogs;
    }

    public final static String getMyLogin() {
        return MyLogin;
    }
}
