package com.example.messengerpigeon.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messengerpigeon.Activity_Navigation;
import com.example.messengerpigeon.Encryption.jsonCrypt;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonServerRequests.addFriendRequest;
import com.example.messengerpigeon.jsonServerRequests.authRequest;
import com.example.messengerpigeon.jsonServerRequests.createNewDialogRequest;
import com.example.messengerpigeon.miniClasses.friend;
import com.example.messengerpigeon.serverInfo;

import java.net.InetAddress;
import java.net.Socket;

public class fragment_profile extends Fragment {

    TextView profile_login;
    TextView profile_name;
    friend frProfile;
    Button button1;
    Button button2;
    Button button3;
    authRequest authReq = new authRequest();
    int idReq = 0;
    int status;

    public fragment_profile(friend i, int st) {
        this.frProfile = i;
        this.status = st;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_profile, container, false);

        profile_login = (TextView) v.findViewById(R.id.text_profile_login);
        profile_name = (TextView) v.findViewById(R.id.text_profile_name);

        button1 = (Button) v.findViewById(R.id.button1);
        button2 = (Button) v.findViewById(R.id.button2);
        button3 = (Button) v.findViewById(R.id.button3);

        button1.setOnClickListener(onClickListenermain);
        button2.setOnClickListener(onClickListenermain);
        button3.setOnClickListener(onClickListenermain);

        profile_login.setText(frProfile.Login);
        profile_name.setText(frProfile.FirstName + " " + frProfile.LastName);

        //if(frProfile.Id принадлежит списку друзей)

        switch (status) {
            case 0:
                button1.setText("Удалить из друзей");
                button2.setText("Создать диалог");
                button3.setText("Защищенный диалог");
                break;
            case 1:
                button1.setText("Запрос отправлен");
                button1.setEnabled(false);
                button2.setVisibility(getView().INVISIBLE);
                button3.setVisibility(getView().INVISIBLE);
                break;
            case -1:
                button1.setText("Принять заявку");
                button2.setVisibility(getView().INVISIBLE);
                button3.setVisibility(getView().INVISIBLE);
                break;
            case 2:
                button1.setText("Добавить в друзья");
                button2.setVisibility(getView().INVISIBLE);
                button3.setVisibility(getView().INVISIBLE);
                break;
        }

        return v;
    }

    View.OnClickListener onClickListenermain = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button1:
                    if (button1.getText().equals("Добавить в друзья")) {
                        AuthTask at = new AuthTask();
                        at.execute("add", authReq.getToken(), Integer.toString(frProfile.Id));
                    } else {
                        if (button1.getText().equals("Принять заявку")) {
                            AuthTask at = new AuthTask();
                            at.execute("add", authReq.getToken(),Integer.toString(frProfile.Id));
                        } else {
                            if (button1.getText().equals("Удалить из друзей")) {
                                //AuthTask at = new AuthTask();
                                //at.execute("delete", authReq.getToken(),Integer.toString(frProfile.Id));
                            }
                        }
                    }
                    break;
                case R.id.button2://создать диалог
                    System.out.println("button_2_click");
                    AuthTask at = new AuthTask();
                    at.execute("createDialog", authReq.getToken(),"", Integer.toString(frProfile.Id));
                    break;
            }
        }
    };

    public class AuthTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;
        private addFriendRequest addFriendRequest = null;
        private createNewDialogRequest createNewDialogRequest = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(final String ret) {
            try {
                System.out.println(ret);
                socket.close();
                System.out.println(idReq);
                switch (idReq) {
                    case 0:
                        break;
                    case 1:
                        addFriendRequest.responseHandler(ret);
                        if (addFriendRequest.getResponse().equals("OK")) {
                            Activity_Navigation.fragmentManager.beginTransaction().replace(R.id.main_content, new fragments_navigation_item_friends()).commit();
                            Activity_Navigation.toolbar.setTitle("Друзья");
                            Toast toastPass = Toast.makeText(getActivity(), "Запрос отправлен", Toast.LENGTH_LONG);
                            toastPass.setGravity(Gravity.CENTER, 0, -90);
                            toastPass.show();
                        } else {
                            Toast toastPass = Toast.makeText(getActivity(), ret.toString(), Toast.LENGTH_LONG);
                            toastPass.setGravity(Gravity.CENTER, 0, -90);
                            toastPass.show();
                        }
                        break;
                    case 2:
                        createNewDialogRequest.responseHandler(ret);
                        System.out.println(createNewDialogRequest.getResponse());
                        if (createNewDialogRequest.getResponse().equals("OK")) {
                            Activity_Navigation.fragmentManager.beginTransaction().replace(R.id.main_content, new fragments_navigation_item_messages()).commit();
                            Activity_Navigation.toolbar.setTitle("Сообщения");
                            Toast toastPass = Toast.makeText(getActivity(), "Диалог создан", Toast.LENGTH_LONG);
                            toastPass.setGravity(Gravity.CENTER, 0, -90);
                            toastPass.show();
                        } else {
                            Toast toastPass = Toast.makeText(getActivity(), ret.toString(), Toast.LENGTH_LONG);
                            toastPass.setGravity(Gravity.CENTER, 0, -90);
                            toastPass.show();
                        }
                        break;
                }

            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... data) {
            try {
                if (data[0].equals("add")) {
                    idReq = 1;
                    addFriendRequest = new addFriendRequest();
                    addFriendRequest.createRequest(data[1], data[2]);
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    System.out.println(serverAddr);
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    return sendAndListen(addFriendRequest.get_Request());
                } else {
                    if (data[0].equals("createDialog")) {
                        idReq = 2;
                        createNewDialogRequest = new createNewDialogRequest();
                        createNewDialogRequest.createRequest(data[1],data[2], data[3]);
                        InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                        System.out.println(serverAddr);
                        socket = new Socket(serverAddr, serverInfo.getPort());
                        return sendAndListen(createNewDialogRequest.get_Request());
                    } else {
                        return "";
                    }
                }
              /*else
                if(data[0].equals("delete")) {
                    searchFriends.createRequest(data[1],data[2]);
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    System.out.println(serverAddr);
                    socket = new Socket(serverAddr, serverInfo.getPort());
                   return sendAndListen("");
                }*/
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        //���������� ������ � ���� ������
        public String sendAndListen(String text) {
            try {

                /*
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(text);
                dos.flush();
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                //���� ������ ������.
                //TODO: ���������� ���
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[] = new byte[55555];
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
}
