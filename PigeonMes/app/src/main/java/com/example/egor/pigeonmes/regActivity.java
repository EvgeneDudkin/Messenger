package com.example.egor.pigeonmes;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class regActivity extends Activity {
    MyTask mt;
    ProgressBar prBar = null;
    EditText loginText = null;
    EditText passText = null;
    EditText pass2Text = null;
    View fucusView = null;

    public void RegBtn_onClick(View view) throws InterruptedException {
        loginText.setError(null);
        passText.setError(null);
        pass2Text.setError(null);
        JSONObject query = new JSONObject();
        JSONObject reg = new JSONObject();
        loginText.setText(loginText.getText().toString().trim());
        if(!checkLogin() || !checkPass()) {
            fucusView.requestFocus();
            return;
        }
        try {
            reg.put("login", loginText.getText().toString());
            reg.put("pass", passText.getText().toString());
            query.put("reg", reg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mt = new MyTask();
        mt.execute(query.toString());

    }

    private boolean checkPass() {
        String s = passText.getText().toString().trim();
        String s2 = pass2Text.getText().toString().trim();
        if(s.length() < 5) {
            passText.setError("Пароль должен быть длинее 4 символов");
            fucusView = passText;
            return false;
        }
        if(!Objects.equals(s,s2)) {
            pass2Text.setError("Пароли не совпадают");
            fucusView = pass2Text;
            return false;
        }
        //TODO: Сделать проверку на корректность пароля(A-Za-z0-9)
        return true;
    }

    private boolean checkLogin() {
        String s = loginText.getText().toString().trim();
        if(s.equalsIgnoreCase("")){
            loginText.setError("Поле не может быть пустым");
            fucusView = loginText;
            return false;
        }
        else if(s.length() < 5) {
            loginText.setError("Логин должен быть не меньше 4 символов");
            fucusView = loginText;
            return false;
        }
        //TODO: Сделать проверку на корректность login'a (A-Za-zА-Яа-я0-9)

        return true;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        prBar = (ProgressBar) findViewById(R.id.progressBar);
        loginText = (EditText) findViewById(R.id.loginText);
        passText = (EditText) findViewById(R.id.passText);
        pass2Text = (EditText) findViewById(R.id.pass2Text);
        loginText.setError(null);
        passText.setError(null);
        pass2Text.setError(null);
    }
    public class MyTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            prBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(final String ret) {
            switch (ret) {
                case "OK": Intent intentApp = new Intent(regActivity.this,
                        emptyActivity.class);
                    regActivity.this.startActivity(intentApp);
                    break;
                case "Error 1":
                    msgOkBox.MsgOkBox("Ошибка 1", "Ошибка передачи данных", regActivity.this);
                    break;
                case "Error 2":
                    msgOkBox.MsgOkBox("Ошибка 2", "Ошибка передачи данных", regActivity.this);
                    break;
                case "Error 3":
                    //msgOkBox.MsgOkBox("Ошибка 3", "Такой login уже существует", regActivity.this);
                    loginText.setError("Такой логин уже существует");
                    fucusView = loginText;
                    break;
                case "Error 4":
                    msgOkBox.MsgOkBox("Ошибка 4", "Ошибка доступа к БД", regActivity.this);
                    break;
                case "Error 5":
                    msgOkBox.MsgOkBox("Ошибка 5", "Ошибка передачи данных", regActivity.this);
                    break;
                case "Error 6":
                    msgOkBox.MsgOkBox("Ошибка 6", "Ошибка доступа к БД", regActivity.this);
                    break;
                case "Error 12":
                    msgOkBox.MsgOkBox("Ошибка 12", "Ошибка подключения к серверу", regActivity.this);
                    break;
                case "Error 13":
                    msgOkBox.MsgOkBox("Ошибка 13", "Ошибка ввода/вывода", regActivity.this);
                    break;
                case "Error 11":
                    msgOkBox.MsgOkBox("Ошибка 11", "Ошибка чтения ответа", regActivity.this);
                    break;
                case "Error 10":
                    msgOkBox.MsgOkBox("Ошибка 10", "Ошибка", regActivity.this);
                    break;
            }
            prBar.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... jsonStr) {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                System.out.println(serverAddr);
                socket = new Socket(serverAddr, serverInfo.getPort());
                return sendAndListen(jsonStr[0]);
            } catch (UnknownHostException e1) {
                return "Error 12";
            } catch (IOException e1) {
                return "Error 13";
            }
        }

        //Отправляем строку и ждем ответа
        public String sendAndListen(String text) {
            try {
                DataOutputStream dos = new DataOutputStream(
                        socket.getOutputStream());
                dos.writeUTF(text);
                dos.flush();
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                //Блок чтения ответа.
                //TODO: Пошаманить тут
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[] = new byte[1024];
                int s=dis.read(buffer);
                baos.write(buffer, 0, s);
                byte result[] = baos.toByteArray();
                return new String(result, "UTF-8");

            } catch (EOFException e) {
                e.printStackTrace();
                return "Error 11";
            }catch (Exception e) {
                e.printStackTrace();
                return "Error 10";
            }

        }


    }
}
