/**
 * Created by Kirill2 on 15.11.2015.
 */

import org.json.*;

import java.util.Objects;

/**
 * Исключение отсутствия пользователя в БД
 */
class userNotFoundException extends Exception {
    public userNotFoundException() {
    }

    public userNotFoundException(String msg) {
        super(msg);
    }
}

/**
 * Исключение если пользователь уже существует
 */
class userAlreadyExistsException extends Exception {
    public userAlreadyExistsException() {
    }

    public userAlreadyExistsException(String msg) {
        super(msg);
    }
}

/**
 * Родительский класс всех запросов
 */
class jsonServerRequests {
    /**
     * Строка "ответа" сервера. Точнее это строка, которая обозначет реакцию сервера на наш запрос.
     */
    protected String response = "";
    /**
     * Строка запроса
     */
    protected String strRequest = "";
    /**
     * JSON объект запроса
     */
    protected JSONObject jsonRequest = null;

    /**
     * (виртуальный метод)
     * Обработчик ответа сервера
     * @param input Строка, которую вернул сервер
     */
    protected void responseHandler( String input) {}

    /**
     * Геттер получения строки запроса
     * @return строка запроса
     */
    public String get_Request() {
        return strRequest;
    }

    /**
     * Геттер получения JSON объекта запроса
     * @return JSON объект запроса
     */
    public JSONObject get_Request_json() {
        return jsonRequest;
    }

    /**
     * Родительский метод обработки ошибок
     * Если ошибка будет обработана, будет вызвавно исключение
     * TODO: обработка неизвестных ошибок
     * @throws Exception
     */
    protected void errorHandler() throws Exception {
        switch (response) {
            case "":
                throw new Exception("response not found");
            case "j1":
                throw new Exception("event handler :(");
        }
    }

}

/**
 * Дочерний класс, запрос авторизации
 */
class authRequest extends jsonServerRequests {

    /**
     * Токен, который вернул сервер
     */
    private String token = "";
    /**
     * Список друзей
     */
    private dialog[] dialogs = null;

    /**
     * Пустой конструктор
     */
    public authRequest() {

    }

    /**
     * Конструктор
     * @param login логин
     * @param pass пароль
     */
    public authRequest(String login, String pass) {
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
    public void createRequest(String login, String pass) {
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
    public dialog[] getDialogs() {
        return dialogs;
    }
}

/**
 * Дочерний класс, запрос регистрации
 */
class regRequest extends jsonServerRequests {

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
    public regRequest(String login, String pass) {
        JSONObject obj = new JSONObject();
        JSONObject reg = new JSONObject();
        reg.put("login", login);
        reg.put("pass", pass);
        obj.put("reg", reg);
        strRequest = obj.toString();
        jsonRequest = obj;
    }

    /**
     * Метод который собирает запрос регистрации
     * @param login логин
     * @param pass пароль
     */
    public void createRequest(String login, String pass) {
        JSONObject obj = new JSONObject();
        JSONObject reg = new JSONObject();
        reg.put("login", login);
        reg.put("pass", pass);
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
