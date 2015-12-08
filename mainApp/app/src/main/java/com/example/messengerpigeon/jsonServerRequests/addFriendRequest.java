package com.example.messengerpigeon.jsonServerRequests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by Пользователь on 01.12.2015.
 */
public class addFriendRequest extends jsonServerRequests {

    /**
     * Токен, который вернул сервер
     */
    private String token = "";
    /**
     * Список друзей
     */
    public static String answer = null;

    /**
     * Пустой конструктор
     */
    public addFriendRequest() {

    }

    /**
     *
     * @param token
     * @param searchPattern
     * @throws JSONException
     */
    public void createRequest(String token, String searchPattern) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONObject req = new JSONObject();
        req.put("token", token);
        req.put("idRecipient", searchPattern);
        obj.put("friendRequest", req);
        strRequest = obj.toString();
        jsonRequest = obj;
    }



    /**
     * override Обработчик ответа сервера
     * @param input Строка, которую вернул сервер
     */
    public  void responseHandler(String input) {
        try {
            JSONObject ret = new JSONObject(input);
            response = ret.get("response").toString();
            response = Objects.equals(response, "OK") ? response : response.substring(6);
        } catch (Exception ignored) {
            token = "";
            response = "j1";
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
    public String getToken() {
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
}
