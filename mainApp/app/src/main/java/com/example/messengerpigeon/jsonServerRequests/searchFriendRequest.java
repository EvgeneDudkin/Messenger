package com.example.messengerpigeon.jsonServerRequests;

import com.example.messengerpigeon.miniClasses.friend;
import com.example.messengerpigeon.miniClasses.message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;

/**
 * Created by Евгений on 27.11.2015.
 */
public class searchFriendRequest extends jsonServerRequests {

    /**
     * Токен, который вернул сервер
     */
    private String token = "";
    /**
     * Список друзей
     */
    public static friend[] friends = null;

    /**
     * Пустой конструктор
     */
    public searchFriendRequest() {

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
        req.put("searchPattern", searchPattern);
        obj.put("friendsS", req);
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

            JSONArray jsonFriends = ret.getJSONArray("friends");
            friends = new friend[jsonFriends.length()];
            for (int i = 0; i < jsonFriends.length(); i++) {
                friends[i] = new friend(jsonFriends.getJSONObject(i));
            }
        } catch (Exception ignored) {
            token = "";
            response = "j1";
            friends = null;
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
    public String getLogin(int i) {
        if(i<0 || i>=friends.length)
            return "";
        return friends[i].Login;
    }
    /**
     * Геттер списка друзей
     * @return список друзей
     */
    public static friend[] getFriends() {
        return friends;
    }
}