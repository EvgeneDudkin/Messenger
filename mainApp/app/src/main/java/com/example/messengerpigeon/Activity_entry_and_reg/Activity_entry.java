package com.example.messengerpigeon.Activity_entry_and_reg;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;


import com.example.messengerpigeon.Activity_Navigation;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.serverInfo;
import com.example.messengerpigeon.jsonServerRequests.authRequest;
import com.example.messengerpigeon.jsonServerRequests.userNotFoundException;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Activity_entry extends AppCompatActivity{

    private Toolbar toolbar;
    private Button button_entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        button_entry=(Button)findViewById(R.id.button_entry);
        button_entry.setOnClickListener(onClickListenermain);

        initToolbar();
    }

    private void initToolbar() {
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
    }

    View.OnClickListener onClickListenermain = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_entry:
                    AuthTask at = new AuthTask();
                    at.execute("qwerty", "qwerty");
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
            System.out.println("1");
        }

        @Override
        protected void onPostExecute(final String ret) {
            try {
                System.out.println(ret);
                authReq.responseHandler(ret);
                authReq.errorHandler();
            }
            catch (userNotFoundException e) {
                /*
                * TODO: ◊“Œ ƒ≈À¿“‹ ≈—À» “¿ Œ… œŒÀ‹«Œ¬¿“≈À‹ Õ≈ Õ¿…ƒ≈Õ
                * */
            }
            catch (Exception e) {
                /*
                * TODO: ≈—À» Àﬁ¡¿ﬂ ƒ–”√¿ﬂ Œÿ»¡ ¿
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

        //ŒÚÔ‡‚ÎˇÂÏ ÒÚÓÍÛ Ë Ê‰ÂÏ ÓÚ‚ÂÚ‡
        public String sendAndListen(String text) {
            try {
                DataOutputStream dos = new DataOutputStream(
                        socket.getOutputStream());
                dos.writeUTF(text);
                dos.flush();
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                //¡ÎÓÍ ˜ÚÂÌËˇ ÓÚ‚ÂÚ‡.
                //TODO: œÓ¯‡Ï‡ÌËÚ¸ ÚÛÚ
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
