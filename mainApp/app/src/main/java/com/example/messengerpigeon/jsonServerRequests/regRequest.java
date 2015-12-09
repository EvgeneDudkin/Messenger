package com.example.messengerpigeon.jsonServerRequests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Дочерний класс, запрос регистрации
 */
public class regRequest extends jsonServerRequests {

    private String token = "";

    /**
     * Пустой конструктор
     */
    public regRequest() {

    }

    /**
     * Конструктор запроса регистрации
     * @param login логин
     * @param pass  пароль
     */
    public regRequest(String login, String pass, String firstName, String secondName) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONObject reg = new JSONObject();
        reg.put("login", login);
        reg.put("pass", pass);
        reg.put("firstName",firstName);
        reg.put("lastName",secondName);
        obj.put("reg", reg);
        strRequest = obj.toString();
        jsonRequest = obj;
    }

    /**
     * Метод который собирает запрос регистрации
     * @param login логин
     * @param pass пароль
     */
    public void createRequest(String login, String pass, String firstName, String secondName) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONObject reg = new JSONObject();
        reg.put("login", login);
        reg.put("pass", pass);
        reg.put("firstName",firstName);
        reg.put("lastName",secondName);
        obj.put("reg", reg);
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
        } catch (Exception ignored) {
            token = "";
            response = "j1";
        }
    }

    /**
     * override Обработчик ошибок.
     * Error r5: в запросе либо login либо pass = null
     * Error r4: Ошибка в запросе к БД. Запроса количества полей с таким login
     * Error r3: Такой логин уже есть
     * Error r6: Ошибка во вставке в БД. Вставка login/pass в users
     * Error r7: Ошибка в запросе к БД. Запроса id по логину паролю
     * Error r9: Ошибка в запросе к БД. Запроса id по токену
     * Error r8: Ошибка во вставке в БД. Вставка токена
     * Error a5: Ошибка генерации токена
     * @throws Exception
     */
    public void errorHandler() throws Exception {
        super.errorHandler();
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

    /**
     * геттер токена
     * @return токен
     */
    public String getToken() {
        return token;
    }

    /**
     * геттер реакции сервера
     * @return реакция сервера
     */
    public String getResponse() {
        return response;
    }
}
