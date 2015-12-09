package com.example.messengerpigeon.Activity_entry_and_reg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.messengerpigeon.Activity_Navigation;
import com.example.messengerpigeon.Encryption.jsonCrypt;
import com.example.messengerpigeon.LoginPasswordValidator;
import com.example.messengerpigeon.MainActivity;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonServerRequests.regRequest;
import com.example.messengerpigeon.jsonServerRequests.userAlreadyExistsException;
import com.example.messengerpigeon.serverInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import java.util.Objects;

/**
 * Created by Пользователь on 16.11.2015.
 */
public class Activity_reg extends AppCompatActivity{

    private Toolbar toolbar;
    private Button button_entry;
    private EditText text_login;
    private EditText text_password1;
    private EditText text_password2;
    private EditText text_profile_first_name;
    private EditText text_profile_second_name;
    View focus=null;

    LoginPasswordValidator loginPasswordValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        button_entry=(Button)findViewById(R.id.button_entry);
        button_entry.setOnClickListener(onClickListenermain);

        text_login=(EditText)findViewById(R.id.text_login);
        text_password1=(EditText)findViewById(R.id.text_password1);
        text_password2=(EditText)findViewById(R.id.text_password2);
        text_profile_first_name=(EditText)findViewById(R.id.text_profile_first_name);
        text_profile_second_name=(EditText)findViewById(R.id.text_profile_second_name);




        /*Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.SetStatusBarColor();*/

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
    private boolean checkName(String name, String secondName){
        if(name.length()<1){
            text_profile_first_name.setError("Введите имя");
            focus=text_profile_first_name;
            return false;
        }
        else if(secondName.length()<1){
            text_profile_second_name.setError("Введите фамилию");
            focus=text_profile_second_name;
            return false;
        }
        else
            return true;
    }
    private boolean checkPass(String pass1, String pass2) {
        loginPasswordValidator = new LoginPasswordValidator();
        if (loginPasswordValidator.validatePassword(pass1)) {
            if (!Objects.equals(pass1, pass2)) {
                text_password2.setError("Пароли не совпадают");
                focus= text_password2;
                return false;
            }
        } else {
            if (pass1.length() < 5) {
                text_password1.setError("Пароль должен быть длинее 4 символов");
                focus= text_password1;
                return false;
            } else {
                text_password1.setError("Пароль может состоять только из букв английского алфавита и цифр, а также должен быть длинее 4 символов");
                focus= text_password1;
                return false;
            }
        }
        return true;
    }

    View.OnClickListener onClickListenermain = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            text_login.setError(null);
            text_password1.setError(null);
            text_password2.setError(null);
            text_profile_first_name.setError(null);
            text_profile_second_name.setError(null);
            switch (v.getId()) {
                case R.id.button_entry:
                    RegTask at = new RegTask();
                    String login = text_login.getText().toString();
                    String pass1 = text_password1.getText().toString();
                    String pass2 = text_password2.getText().toString();
                    String firstName = text_profile_first_name.getText().toString();
                    String secondName = text_profile_second_name.getText().toString();
                    loginPasswordValidator = new LoginPasswordValidator();
                    if (!checkLogin(login)||!checkPass(pass1,pass2) || !(checkName(firstName,secondName))) {
                       focus.requestFocus();
                    } else {
                        at.execute(login, pass1,firstName,secondName);
                    }

                    break;
            }
        }
    };
    public class RegTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;
        private regRequest regReq = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            regReq = new regRequest();
        }

        @Override
        protected void onPostExecute(final String ret) {
            try {
                System.out.println(ret);
                regReq.responseHandler(ret);
                regReq.errorHandler();
                Intent intentEntry = new Intent(Activity_reg.this, Activity_entry.class);
                Activity_reg.this.startActivity(intentEntry);
                finish();
            } catch (userAlreadyExistsException e) {
                /*
                * TODO: ��� ������ ���� ����� ������������ �� ������
                * */
                text_login.setError("Пользователь уже существует");
            } catch (Exception e) {
                /*
                * TODO: ���� ����� ������ ������
                * */
                text_login.setError("Ошибка регистрации");
            }

        }

        @Override
        protected String doInBackground(String... data) {
            try {
                regReq.createRequest(data[0], data[1], data[2],data[3]);
                InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                System.out.println(serverAddr);
                socket = new Socket(serverAddr, serverInfo.getPort());
                System.out.println(regReq.get_Request());
                return sendAndListen(regReq.get_Request());
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        //���������� ������ � ���� ������
        public String sendAndListen(String text) {
            try {

                /*
                DataOutputStream dos = new DataOutputStream(
                        socket.getOutputStream());
                dos.write(text.getBytes(),0,text.length());
                dos.flush();

                //jsonCrypt.Send(socket, text);

                DataInputStream dis = new DataInputStream(socket.getInputStream());

                //���� ������ ������.
                //TODO: ���������� ���
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[] = new byte[1024];
                int s = dis.read(buffer);
                baos.write(buffer, 0, s);
                byte result[] = baos.toByteArray();
                return new String(result, "UTF-8");
                */

                jsonCrypt.Send(socket, text);

                return jsonCrypt.Get(socket);

            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }

        }
    }


    @Override
    public void onBackPressed() {
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Регистрация не завершена! Выйти?");

        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentMainActivity = new Intent(Activity_reg.this, MainActivity.class);
                Activity_reg.this.startActivity(intentMainActivity);
                finish();
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        quitDialog.show();
    }
}