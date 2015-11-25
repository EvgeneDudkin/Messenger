package com.example.messengerpigeon.jsonServerRequests;

import com.example.messengerpigeon.miniClasses.dialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Дочерний класс, запрос авторизации
 */
public class authRequest extends jsonServerRequests {

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
    public authRequest() {

    }

    public static String MyLogin="";

    /**
     * Конструктор
     * @param login логин
     * @param pass пароль
     */
    public authRequest(String login, String pass) throws JSONException {
        MyLogin=login;
        JSONObject obj = new JSONObject();
        JSONObject auth = new JSONObject();
        auth.put("login", login);
        auth.put("pass", pass);
        obj.put("auth", auth);
        strRequest = obj.toString();
        jsonRequest = obj;
    }

    /**
     * Метод создания запроса
     * @param login логин
     * @param pass пароль
     */
    public void createRequest(String login, String pass) throws JSONException {
        MyLogin=login;
        JSONObject obj = new JSONObject();
        JSONObject auth = new JSONObject();
        auth.put("login", login);
        auth.put("pass", pass);
        obj.put("auth", auth);
        strRequest = obj.toString();
        jsonRequest = obj;
    }

    /**
     * override Обработчик ответа сервера
     * @param input Строка, которую вернул сервер
     */
    public void responseHandler(String input) {
        try {
            JSONObject ret = new JSONObject(input);
            token = ret.get("token").toString();
            response = ret.get("response").toString();
            response = Objects.equals(response, "OK") ? response : response.substring(6);
            JSONArray jsonDialogs = ret.getJSONArray("dialogs");
            dialogs = new dialog[jsonDialogs.length()];
            for (int i = 0; i < jsonDialogs.length(); i++) {
                dialogs[i] = new dialog(jsonDialogs.getJSONObject(i));
            }
        } catch (Exception ignored) {
            token = "";
            response = "j1";
            dialogs = null;
        }
    }

    /**
     * override Обработчик ошибок.
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
     * @return токен
     */
    public static String getToken() {
        return token;
    }

    /**
     * Геттер реакции сервера
     * @return реакция сервера
     */
    public String getResponse() {
        return response;
    }

    /**
     * Геттер списка друзей
     * @return список друзей
     */
    public static dialog[] getDialogs() {
        return dialogs;
    }

    public static String getMyLogin(){return MyLogin;}
}
