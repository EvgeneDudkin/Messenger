package com.example.egor.pigeonmes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class authActivity extends Activity {

    public void AuthBtn_onClick(View view) throws InterruptedException {
        JSONObject query = new JSONObject();
        JSONObject auth = new JSONObject();
        EditText loginText = (EditText) findViewById(R.id.loginText);
        EditText passText = (EditText) findViewById(R.id.passText);
        try {
            auth.put("login", loginText.getText().toString());
            auth.put("pass", passText.getText().toString());
            query.put("auth", auth);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ClientThread ct = new ClientThread(query.toString());
        Thread th = new Thread(ct);
        th.start();
        th.join();
        switch (ct.ret) {
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
                msgOkBox.MsgOkBox("Ошибка 4", "Ошибка запроса к БД", authActivity.this);
                break;
        }

    }

    public static void feedBack(String s) {

        System.out.println(s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
    }
}
