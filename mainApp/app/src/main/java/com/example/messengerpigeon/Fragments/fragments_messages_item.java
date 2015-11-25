package com.example.messengerpigeon.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.messengerpigeon.Activity_Navigation;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonServerRequests.authRequest;
import com.example.messengerpigeon.jsonServerRequests.messageRequest;
import com.example.messengerpigeon.jsonServerRequests.userNotFoundException;
import com.example.messengerpigeon.serverInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by egor on 20.11.2015.
 */
public class fragments_messages_item extends Fragment {
    authRequest authReq= new authRequest();
    View v=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_dialog, container, false);
        TextView tt=(TextView)v.findViewById(R.id.dialogText);

        AuthTask at = new AuthTask();
        at.execute(authReq.getToken(), "1","20");
        return v;
    }
    public class AuthTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;
        private messageRequest mesReq=null;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mesReq=new messageRequest();
            System.out.println("1");
        }

        @Override
        protected void onPostExecute(final String ret) {
            TextView tt=(TextView)v.findViewById(R.id.dialogText);
            tt.setText(ret);
            System.out.println(ret);

        }

        @Override
        protected String doInBackground(String... data) {
            try {
                mesReq.createRequest(data[0],Integer.parseInt(data[1]),Integer.parseInt(data[2]));
                InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                System.out.println(serverAddr);
                socket = new Socket(serverAddr, serverInfo.getPort());
                return sendAndListen(mesReq.get_Request());
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
