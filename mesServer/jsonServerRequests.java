/**
 * Created by Kirill2 on 15.11.2015.
 */

import org.json.*;

import java.util.Objects;

/**
 * ���������� ���������� ������������ � ��
 */
class userNotFoundException extends Exception {
    public userNotFoundException() {
    }

    public userNotFoundException(String msg) {
        super(msg);
    }
}

/**
 * ���������� ���� ������������ ��� ����������
 */
class userAlreadyExistsException extends Exception {
    public userAlreadyExistsException() {
    }

    public userAlreadyExistsException(String msg) {
        super(msg);
    }
}

/**
 * ������������ ����� ���� ��������
 */
class jsonServerRequests {
    /**
     * ������ "������" �������. ������ ��� ������, ������� ��������� ������� ������� �� ��� ������.
     */
    protected String response = "";
    /**
     * ������ �������
     */
    protected String strRequest = "";
    /**
     * JSON ������ �������
     */
    protected JSONObject jsonRequest = null;

    /**
     * (����������� �����)
     * ���������� ������ �������
     * @param input ������, ������� ������ ������
     */
    protected void responseHandler( String input) {}

    /**
     * ������ ��������� ������ �������
     * @return ������ �������
     */
    public String get_Request() {
        return strRequest;
    }

    /**
     * ������ ��������� JSON ������� �������
     * @return JSON ������ �������
     */
    public JSONObject get_Request_json() {
        return jsonRequest;
    }

    /**
     * ������������ ����� ��������� ������
     * ���� ������ ����� ����������, ����� �������� ����������
     * TODO: ��������� ����������� ������
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
 * �������� �����, ������ �����������
 */
class authRequest extends jsonServerRequests {

    /**
     * �����, ������� ������ ������
     */
    private String token = "";
    /**
     * ������ ������
     */
    private dialog[] dialogs = null;

    /**
     * ������ �����������
     */
    public authRequest() {

    }

    /**
     * �����������
     * @param login �����
     * @param pass ������
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
     * ����� �������� �������
     * @param login �����
     * @param pass ������
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
     * override ���������� ������ �������
     * @param input ������, ������� ������ ������
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
     * override ���������� ������.
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
     * ������ ������
     * @return �����
     */
    public String getToken() {
        return token;
    }

    /**
     * ������ ������� �������
     * @return ������� �������
     */
    public String getResponse() {
        return response;
    }

    /**
     * ������ ������ ������
     * @return ������ ������
     */
    public dialog[] getDialogs() {
        return dialogs;
    }
}

/**
 * �������� �����, ������ �����������
 */
class regRequest extends jsonServerRequests {

    private String token = "";

    /**
     * ������ �����������
     */
    public regRequest() {

    }

    /**
     * ����������� ������� �����������
     * @param login �����
     * @param pass  ������
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
     * ����� ������� �������� ������ �����������
     * @param login �����
     * @param pass ������
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
     * override ���������� ������ �������
     * @param input ������, ������� ������ ������
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
     * override ���������� ������.
     * Error r5: � ������� ���� login ���� pass = null
     * Error r4: ������ � ������� � ��. ������� ���������� ����� � ����� login
     * Error r3: ����� ����� ��� ����
     * Error r6: ������ �� ������� � ��. ������� login/pass � users
     * Error r7: ������ � ������� � ��. ������� id �� ������ ������
     * Error r9: ������ � ������� � ��. ������� id �� ������
     * Error r8: ������ �� ������� � ��. ������� ������
     * Error a5: ������ ��������� ������
     * @throws Exception
     */
    public void errorHandler() throws Exception {
        super.errorHandler();
        switch (response) {
            case "r5":
                throw new Exception("� ������� ���� login ���� pass = null. Error r5");
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
     * ������ ������
     * @return �����
     */
    public String getToken() {
        return token;
    }

    /**
     * ������ ������� �������
     * @return ������� �������
     */
    public String getResponse() {
        return response;
    }
}
