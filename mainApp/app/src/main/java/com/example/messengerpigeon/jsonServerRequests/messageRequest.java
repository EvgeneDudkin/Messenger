package com.example.messengerpigeon.jsonServerRequests;

import com.example.messengerpigeon.miniClasses.message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
/**
 * Created by egor on 25.11.2015.
 */
public class messageRequest extends jsonServerRequests {

    /**
     * Токен, который вернул сервер
     */
    private enum requestType{
        LIST,
        SENDMSG
    }
    private String token = "";
    private String login = "";
    private String msgListRequest="";
    private String msgSendRequest="";
    private  requestType reqType=requestType.LIST;

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
    public void messageListRequest(String token, int dialogId, int messageCount) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONObject req = new JSONObject();
        req.put("token", token);
        req.put("dialogId", dialogId);
        req.put("messageCount", messageCount);
        obj.put("lastNmsg", req);
        msgListRequest = obj.toString();
        jsonRequest = obj;
    }
    public void sendMessageRequest(String token, int dialogId, String msg) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONObject req = new JSONObject();
        req.put("token", token);
        req.put("dialogId", dialogId);
        req.put("msg", msg);
        obj.put("sendMsg", req);
        System.out.println(token + ", " + dialogId + ", " + msg);
        msgSendRequest = obj.toString();
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
            if(response.equals("OK")) {
                if (ret.has("messages"))
                {
                    JSONArray jsonMessage = ret.getJSONArray("messages");
                    messages = new message[jsonMessage.length()];
                    for (int i = 0; i < jsonMessage.length(); i++) {
                        messages[i] = new message(jsonMessage.getJSONObject(i));
                    }
                    reqType=requestType.LIST;
                }
                else
                {
                    reqType=requestType.SENDMSG;
                }
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
    public String getRequestType()
    {
        if(reqType==requestType.LIST)
            return "list";
        if(reqType==requestType.SENDMSG)
            return "send";
        return "";
    }
    /**
     * Геттер токена
     * @return токен
     */
    public String getToken() {
        return token;
    }
    public String getMsgListRequest() {
        return msgListRequest;
    }
    public String getMsgSendRequest() {
        return msgSendRequest;
    }
    /**
     * Геттер реакции сервера
     * @return реакция сервера
     */
    public String getResponse() {
        return response;
    }
    public String getMessage(int i) {
        if(i<0 || i>=messages.length)
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