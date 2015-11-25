package com.example.messengerpigeon.jsonServerRequests;

import com.example.messengerpigeon.miniClasses.dialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import com.example.messengerpigeon.miniClasses.message;
/**
 * Created by egor on 25.11.2015.
 */
public class messageRequest extends jsonServerRequests {

    /**
     * Токен, который вернул сервер
     */
    private String token = "";
    private String login = "";
    /**
     * Список друзей
     */
    public static message[] messages = null;

    /**
     * Пустой конструктор
     */
    public messageRequest() {

    }

    /**
     *
     * @param token
     * @param dialogId
     * @param messageCount
     * @throws JSONException
     */
    public void createRequest(String token, int dialogId, int messageCount) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONObject req = new JSONObject();
        req.put("token", token);
        req.put("dialogId", dialogId);
        req.put("messageCount", messageCount);
        obj.put("lastNmsg", req);
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

            JSONArray jsonMessage = ret.getJSONArray("messages");
            messages = new message[jsonMessage.length()];
            for (int i = 0; i < jsonMessage.length(); i++) {
                messages[i] = new message(jsonMessage.getJSONObject(i));
            }
        } catch (Exception ignored) {
            token = "";
            response = "j1";
            messages = null;
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
    public String getMessage(int i) {
        if(i<0 || i>messages.length)
            return "";
        return messages[i].text;
    }
    /**
     * Геттер списка друзей
     * @return список друзей
     */
    public static message[] getMessages() {
        return messages;
    }
}