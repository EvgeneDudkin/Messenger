package com.example.egor.pigeonmes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class regActivity extends Activity {

    public void RegBtn_onClick(View view) throws InterruptedException {
        JSONObject query = new JSONObject();
        JSONObject reg = new JSONObject();
        EditText loginText = (EditText) findViewById(R.id.loginText);
        EditText passText = (EditText) findViewById(R.id.passText);
        EditText pass2Text = (EditText) findViewById(R.id.pass2Text);
        if(!Objects.equals(pass2Text.getText().toString(), passText.getText().toString())) {
            msgOkBox.MsgOkBox("Ошибка", "Пароли не совпадают", regActivity.this);
            return;
        }

        try {
            reg.put("login", loginText.getText().toString());
            reg.put("pass", passText.getText().toString());
            query.put("reg", reg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ClientThread ct = new ClientThread(query.toString());
        Thread th = new Thread(ct);
        th.start();
        th.join();
        switch (ct.ret) {
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
                msgOkBox.MsgOkBox("Ошибка 3", "Такой пользователь уже есть", regActivity.this);
                break;
            case "Error 4":
                msgOkBox.MsgOkBox("Ошибка 4", "Ошибка запроса к БД", regActivity.this);
                break;
            case "Error 5":
                msgOkBox.MsgOkBox("Ошибка 5", "Ошибка передачи данных", regActivity.this);
                break;
            case "Error 6":
                msgOkBox.MsgOkBox("Ошибка 6", "Ошибка запроса к БД", regActivity.this);
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
    }
}
