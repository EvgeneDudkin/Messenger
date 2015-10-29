package com.example.egor.pigeonmes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.tv.TvContract;
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

public class authActivity extends Activity {
    MyTask mt;
    ProgressBar prBar = null;
    EditText loginText = null;
    EditText passText = null;
    public void AuthBtn_onClick(View view) throws InterruptedException {
        JSONObject query = new JSONObject();
        JSONObject auth = new JSONObject();
        try {
            auth.put("login", loginText.getText().toString());
            auth.put("pass", passText.getText().toString());
            query.put("auth", auth);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mt = new MyTask();
        mt.execute(query.toString());

    }

    public static void feedBack(String s) {

        System.out.println(s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        loginText = (EditText) findViewById(R.id.loginText);
        passText = (EditText) findViewById(R.id.passText);
        loginText.setError(null);
        passText.setError(null);
        prBar = (ProgressBar) findViewById(R.id.progressBar1);
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
                case "OK": Intent intentApp = new Intent(authActivity.this,
                        emptyActivity.class);
                    authActivity.this.startActivity(intentApp);
                    break;
                case "Error 1":
                    msgOkBox.MsgOkBox("Ошибка 1", "Ошибка передачи данных", authActivity.this);
                    break;
                case "Error 2":
                    msgOkBox.MsgOkBox("Ошибка 2", "Ошибка передачи данных", authActivity.this);
                    break;
                case "Error 3":
                    msgOkBox.MsgOkBox("Ошибка 3", "Логин или пароль введены неверно", authActivity.this);
                    break;
                case "Error 4":
                    msgOkBox.MsgOkBox("Ошибка 3", "Ошибка доступа к БД", authActivity.this);
                    break;
                case "Error 12":
                    msgOkBox.MsgOkBox("Ошибка 12", "Ошибка подключения к серверу", authActivity.this);
                    break;
                case "Error 13":
                    msgOkBox.MsgOkBox("Ошибка 13", "Ошибка ввода/вывода", authActivity.this);
                    break;
                case "Error 11":
                    msgOkBox.MsgOkBox("Ошибка 11", "Ошибка чтения ответа", authActivity.this);
                    break;
                case "Error 10":
                    msgOkBox.MsgOkBox("Ошибка 10", "Ошибка", authActivity.this);
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
