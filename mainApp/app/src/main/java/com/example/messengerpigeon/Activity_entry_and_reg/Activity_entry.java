package com.example.messengerpigeon.Activity_entry_and_reg;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.messengerpigeon.Activity_Navigation;
import com.example.messengerpigeon.LoginPasswordValidator;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonServerRequests.authRequest;
import com.example.messengerpigeon.jsonServerRequests.userNotFoundException;
import com.example.messengerpigeon.serverInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

public class Activity_entry extends AppCompatActivity{

    private Toolbar toolbar;
    private Button button_entry;
    private EditText text_login;
    private EditText text_password;

    View focus=null;

    LoginPasswordValidator loginPasswordValidator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        button_entry=(Button)findViewById(R.id.button_entry);
        button_entry.setOnClickListener(onClickListenermain);

        text_login=(EditText)findViewById(R.id.text_login);
        text_password=(EditText)findViewById(R.id.text_password);

        initToolbar();
    }

    private void initToolbar() {
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
    }

    private boolean checkLogin(String login) {
        loginPasswordValidator= new LoginPasswordValidator();
        if(!loginPasswordValidator.validateLogin(login)) {
            if (login.length() < 4) {
                text_login.setError("Логин должен быть не меньше 4 символов");
                focus= text_login;
                return false;
            }
            else{
                text_login.setError("Логин может состоять только из букв русского, английского алфавита и цифр");
                focus = text_login;
                return false;
            }
        }
        return true;
    }

    private boolean checkPass(String pass1) {
        loginPasswordValidator = new LoginPasswordValidator();
        if (loginPasswordValidator.validatePassword(pass1)) {
        } else {
            if (pass1.length() < 5) {
                text_password.setError("Пароль должен быть длинее 4 символов");
                focus = text_password;
                return false;
            } else {
                text_password.setError("Пароль может состоять только из букв английского алфавита и цифр, а также должен быть длинее 4 символов");
                focus = text_password;
                return false;
            }
        }
        return true;
    }

    View.OnClickListener onClickListenermain = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_entry:
                    AuthTask at = new AuthTask();
                    String login=text_login.getText().toString();
                    String pass=text_password.getText().toString();
                    loginPasswordValidator = new LoginPasswordValidator();
                    if (!checkLogin(login)||!checkPass(pass)) {
                        focus.requestFocus();
                    } else {
                        at.execute(login, pass);
                    }
                    break;
            }
        }
    };
    public class AuthTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;
        private authRequest authReq = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            authReq = new authRequest();
        }

        @Override
        protected void onPostExecute(final String ret) {
            try {
                System.out.println(ret);
                authReq.responseHandler(ret);
                authReq.errorHandler();
                Intent intentEntry = new Intent(Activity_entry.this, Activity_Navigation.class);
                Activity_entry.this.startActivity(intentEntry);
            }
            catch (userNotFoundException e) {
                /*
                * TODO: ��� ������ ���� ����� ������������ �� ������
                * */
            }
            catch (Exception e) {
                /*
                * TODO: ���� ����� ������ ������
                * */
            }

        }

        @Override
        protected String doInBackground(String... data) {
            try {
                authReq.createRequest(data[0], data[1]);
                InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                System.out.println(serverAddr);
                socket = new Socket(serverAddr, serverInfo.getPort());
                System.out.println(authReq.get_Request());
                return sendAndListen(authReq.get_Request());
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        //���������� ������ � ���� ������
        public String sendAndListen(String text) {
            try {
                DataOutputStream dos = new DataOutputStream(
                        socket.getOutputStream());
                dos.writeUTF(text);
                dos.flush();
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                //���� ������ ������.
                //TODO: ���������� ���
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[] = new byte[1024];
                int s=dis.read(buffer);
                baos.write(buffer, 0, s);
                byte result[] = baos.toByteArray();
                return new String(result, "UTF-8");

            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }

        }

    }

}
