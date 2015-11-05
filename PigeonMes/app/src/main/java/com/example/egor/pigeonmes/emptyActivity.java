package com.example.egor.pigeonmes;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
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

public class emptyActivity extends Activity {

    MyTask mt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        JSONObject query = new JSONObject();
        JSONObject friends = new JSONObject();

        try {
            friends.put("login", "qwerty");
            query.put("friends", friends);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mt = new MyTask();
        mt.execute(query.toString());
    }

    public class MyTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final String ret) {
            System.out.println(ret);
            //msgOkBox.MsgOkBox(ret, ret, emptyActivity.this);
            JSONObject jo = null;
            String s = "";
            try {
                jo = new JSONObject(ret);
                JSONArray arr = jo.getJSONArray("friends");
                for (int i = 0; i < arr.length(); i++) {
                    s += arr.getString(i) + "\n";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            TextView tv = (TextView) findViewById(R.id.textView);
            tv.setText(s);
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
