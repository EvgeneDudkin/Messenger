package com.example.messengerpigeon.Fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.messengerpigeon.Activity_Navigation;
import com.example.messengerpigeon.Encryption.Pair;
import com.example.messengerpigeon.Encryption.RSACrypt;
import com.example.messengerpigeon.Encryption.jsonCrypt;
import com.example.messengerpigeon.MainActivity;
import com.example.messengerpigeon.Messages_Item.ProtectedMessagesAdapter;
import com.example.messengerpigeon.Messages_Item.ProtectedMessagesItem;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonServerRequests.acceptProtectedDialogRequest;
import com.example.messengerpigeon.jsonServerRequests.authRequest;
import com.example.messengerpigeon.jsonServerRequests.listDisabledProtectedDialogsRequest;
import com.example.messengerpigeon.miniClasses.disabledProtectedDialog;
import com.example.messengerpigeon.serverInfo;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Пользователь on 09.12.2015.
 */
public class fragment_pr_mess_2 extends Fragment {
    int countDialog;
    ListView listViewMessage;
    List<Fragment> listFragmentMess;
    List<ProtectedMessagesItem> listMessItem;
    private static FragmentManager fragmentManager;
    listDisabledProtectedDialogsRequest req = new listDisabledProtectedDialogsRequest();
    View view;
    String flag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_protected_messages, container, false);
        Activity_Navigation.i=0;
        AuthTask at = new AuthTask();
        flag="list";
        at.execute(authRequest.getToken(),"list");
        return view;
    }

    private void initListDialog() {
        final disabledProtectedDialog[] dialog = req.getProtectedDialogs();
        countDialog = dialog.length;

        listViewMessage = (ListView) view.findViewById(R.id.list_protected_messages);

        listMessItem = new ArrayList<ProtectedMessagesItem>();
        for (int i = 0; i < countDialog; i++) {
            listMessItem.add(new ProtectedMessagesItem(dialog[i].getName(), R.mipmap.ic_account, dialog[i].Login, dialog[i].Status));
        }

        ProtectedMessagesAdapter messagesListAdapter = new ProtectedMessagesAdapter(getActivity(), R.layout.item_messages, listMessItem);
        listViewMessage.setAdapter(messagesListAdapter);

        listFragmentMess = new ArrayList<Fragment>();
        for (int i = 0; i < countDialog; i++) {
            Bundle arg = new Bundle();
            arg.putByte("dialogId", (byte) dialog[i].Id);
            fragments_messages_item it = new fragments_messages_item();
            it.setArguments(arg);
            listFragmentMess.add(it);
        }

        listViewMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dialog[position].Status.equals("false")) {
                    AuthTask at = new AuthTask();
                    flag="click";
                    at.execute(authRequest.getToken(),"click",dialog[position].Name, String.valueOf(dialog[position].Id));
                    /*Activity_Navigation.i = 2;
                    fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_content, listFragmentMess.get(position)).addToBackStack("2").commit();
                    Activity_Navigation.toolbar.setTitle(dialog[position].getName());*/
                }
            }
        });

    }
    public class AuthTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;
        private listDisabledProtectedDialogsRequest prReq=null;
        private acceptProtectedDialogRequest acceptProtectedDialogRequest=null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prReq = new listDisabledProtectedDialogsRequest();
            acceptProtectedDialogRequest=new acceptProtectedDialogRequest();
        }

        @Override
        protected void onPostExecute(final String ret) {
            if(flag.equals("list")) {
                try {
                    socket.close();
                    System.out.println(ret);
                    prReq.responseHandler(ret);
                    prReq.errorHandler();
                    initListDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    socket.close();
                    System.out.println(ret);
                    acceptProtectedDialogRequest.responseHandler(ret);
                    acceptProtectedDialogRequest.errorHandler();
                    if(acceptProtectedDialogRequest.getResponse().equals("OK")){
                        Activity_Navigation.fragmentManager.beginTransaction().replace(R.id.main_content, new fragment_pr_mess_1()).commit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected String doInBackground(String... data) {
            if(data[1].equals("list")) {
                try {
                    prReq.createRequest(data[0]);
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    System.out.println(serverAddr);
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    System.out.println(prReq.get_Request());
                    return sendAndListen(prReq.get_Request());
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            }
            else{
                try {
                    Pair publicKey = new Pair();
                    BigInteger privateKey = RSACrypt.generateKeys(publicKey, 32);
                    String APP_PREFERENCES_MY_PRIVATE_KEY = "DIALOG_KEY1_" + data[3];
                    SharedPreferences.Editor editor = MainActivity.mSettings.edit();
                    editor.putString(APP_PREFERENCES_MY_PRIVATE_KEY, privateKey.toString());
                    editor.apply();

                    acceptProtectedDialogRequest.createRequest(data[0],data[2],data[3],publicKey.x.toString(),publicKey.y.toString());
                    InetAddress serverAddr = InetAddress.getByName(serverInfo.getIP());
                    System.out.println(serverAddr);
                    socket = new Socket(serverAddr, serverInfo.getPort());
                    System.out.println(acceptProtectedDialogRequest.get_Request());
                    return sendAndListen(acceptProtectedDialogRequest.get_Request());
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            }
        }

        //���������� ������ � ���� ������
        public String sendAndListen(String text) {
            try {

                jsonCrypt.Send(socket, text);

                return jsonCrypt.Get(socket);

            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }

        }

    }
}
