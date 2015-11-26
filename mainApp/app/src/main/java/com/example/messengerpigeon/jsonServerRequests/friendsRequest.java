package com.example.messengerpigeon.jsonServerRequests;

import com.example.messengerpigeon.miniClasses.friend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Дочерний класс, запрос авторизации
 */
public class friendsRequest extends jsonServerRequests {

    /**
     * Токен, который вернул сервер
     */
    private static String token = "";
    /**
     * Список друзей
     */
    public static friend[] friends = null;


    /**
     * Конструктор
     */
    public friendsRequest() throws JSONException {
        String token = authRequest.getToken();
        JSONObject obj = new JSONObject();
        JSONObject fr = new JSONObject();
        fr.put("token",token);
        obj.put("friendsL",fr);
        strRequest = obj.toString();
        jsonRequest = obj;
    }

    /**
     * Метод создания запроса
     */
    public void createRequest() throws JSONException {
        String token = authRequest.getToken();
        JSONObject obj = new JSONObject();
        JSONObject fr = new JSONObject();
        fr.put("token",token);
        obj.put("friendsL",fr);
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
            response = ret.get("response").toString();
            response = Objects.equals(response, "OK") ? response : response.substring(6);
            JSONArray jsonDialogs = ret.getJSONArray("friends");
            friends = new friend[jsonDialogs.length()];
            for (int i = 0; i < jsonDialogs.length(); i++) {
                friends[i] = new friend(jsonDialogs.getJSONObject(i));
            }
        } catch (Exception ignored) {
            token = "";
            response = "j1";
            friends = null;
            ignored.printStackTrace();
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
    public static friend[] getListFriends() {
        return friends;
    }

}
