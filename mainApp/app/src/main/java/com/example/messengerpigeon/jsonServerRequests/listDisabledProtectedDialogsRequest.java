package com.example.messengerpigeon.jsonServerRequests;

import com.example.messengerpigeon.miniClasses.disabledProtectedDialog;
import com.example.messengerpigeon.miniClasses.protectedDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by Пользователь on 08.12.2015.
 */
public class listDisabledProtectedDialogsRequest extends jsonServerRequests {
    /**
     * Токен, который вернул сервер
     */
    private static String token = "";
    /**
     * Список друзей
     */
    public static disabledProtectedDialog[] disabledProtectedDialogs = null;

    /**
     * Пустой конструктор
     */
    public listDisabledProtectedDialogsRequest() {

    }

    public static String MyLogin = "";

    /**
     * Конструктор
     */
    public listDisabledProtectedDialogsRequest(String token) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONObject auth = new JSONObject();
        auth.put("token", token);
        obj.put("dialogsLPD", auth);
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
        obj.put("dialogsLPD", auth);
        strRequest = obj.toString();
        jsonRequest = obj;
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
            disabledProtectedDialogs = new disabledProtectedDialog[jsonDialogs.length()];
            for (int i = 0; i < jsonDialogs.length(); i++) {
                disabledProtectedDialogs[i] = new disabledProtectedDialog(jsonDialogs.getJSONObject(i));
            }
        } catch (Exception ignored) {
            token = "";
            response = "j1";
            disabledProtectedDialogs = null;
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
    public static disabledProtectedDialog[] getProtectedDialogs() {
        return disabledProtectedDialogs;
    }

    public final static String getMyLogin() {
        return MyLogin;
    }
}
