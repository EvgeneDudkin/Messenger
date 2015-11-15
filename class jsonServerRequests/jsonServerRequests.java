/**
 * Created by Kirill2 on 15.11.2015.
 */

import org.json.*;

import java.util.Objects;

class jsonServerRequests {
    protected String response = "";
    /**
     * Строка запроса
     */
    protected String strRequest = "";
    protected JSONObject jsonRequest = null;

    public String get_Request() {
        return strRequest;
    }

    public JSONObject get_Request_json() {
        return jsonRequest;
    }

    protected String errorHandler() {
        switch (response) {
            case "":
                return "Ошибка! Запроса еще не было";
            case "j1":
                return "Ошибка обработчика события";
        }
        return "";
    }

}

class authRequest extends jsonServerRequests {

    private String token = "";
    private String response = "";
    private friend[] friends = null;

    public authRequest() {

    }

    public authRequest(String login, String pass) {
        JSONObject obj = new JSONObject();
        JSONObject auth = new JSONObject();
        auth.put("login", login);
        auth.put("pass", pass);
        obj.put("auth", auth);
        strRequest = obj.toString();
        jsonRequest = obj;
    }

    public void createRequest(String login, String pass) {
        JSONObject obj = new JSONObject();
        JSONObject auth = new JSONObject();
        auth.put("login", login);
        auth.put("pass", pass);
        obj.put("auth", auth);
        strRequest = obj.toString();
        jsonRequest = obj;
    }

    public void responseHandler(String input) {
        try {
            JSONObject ret = new JSONObject(input);
            token = ret.get("token").toString();
            response = ret.get("response").toString().substring(7);
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

    public String errorHandler() {
        String sup = super.errorHandler();
        if (!Objects.equals(sup, "")) return sup;
        switch (response) {
            case "a8":
                return "Во входящем запросе либо login, либо pass равны null";
            case "a4":
                return "Ошибка в запросе к БД.";
            case "a3":
                return "Такого пользователя нет.";
            case "a6":
                return "Ошибка в запросе к БД.";
            case "a7":
                return "Ошибка во вставке в БД.";
            case "a5":
                return "Ошибка генерации токена. ";
        }
        return "Неопознаная ошибка";
    }

    public String getToken() {
        return token;
    }

    public String getResponse() {
        return response;
    }

    public friend[] getFriends() {
        return friends;
    }
}

class regRequest extends jsonServerRequests {

    private String token = "";
    private String response = "";

    public regRequest() {

    }

    public regRequest(String login, String pass) {
        JSONObject obj = new JSONObject();
        JSONObject reg = new JSONObject();
        reg.put("login", login);
        reg.put("pass", pass);
        obj.put("auth", reg);
        strRequest = obj.toString();
        jsonRequest = obj;
    }

    public void createRequest(String login, String pass) {
        JSONObject obj = new JSONObject();
        JSONObject auth = new JSONObject();
        auth.put("login", login);
        auth.put("pass", pass);
        obj.put("auth", auth);
        strRequest = obj.toString();
        jsonRequest = obj;
    }

    public void responseHandler(String input) {
        try {
            JSONObject ret = new JSONObject(input);
            token = ret.get("token").toString();
            response = ret.get("response").toString().substring(7);
        } catch (Exception ignored) {
            token = "";
            response = "j1";
        }
    }

    public String errorHandler() {
        String sup = super.errorHandler();
        if (!Objects.equals(sup, "")) return sup;
        switch (response) {
            case "r3":
                return "Такого логин уже есть.";
        }
        return "Неопознаная ошибка";
    }

    public String getToken() {
        return token;
    }

    public String getResponse() {
        return response;
    }
}
