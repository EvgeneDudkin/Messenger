package com.example.messengerpigeon.Fragments;

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
import com.example.messengerpigeon.Encryption.jsonCrypt;
import com.example.messengerpigeon.Messages_Item.MessagesListAdapter;
import com.example.messengerpigeon.Messages_Item.Messages_Item;
import com.example.messengerpigeon.R;
import com.example.messengerpigeon.jsonServerRequests.authRequest;
import com.example.messengerpigeon.jsonServerRequests.listProtectedDialogsRequest;
import com.example.messengerpigeon.miniClasses.protectedDialog;
import com.example.messengerpigeon.serverInfo;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Пользователь on 09.12.2015.
 */
public class fragment_pr_mess_1 extends Fragment {

    int countDialog;
    ListView listViewMessage;
    List<Fragment> listFragmentMess;
    List<Messages_Item> listMessItem;
    private static FragmentManager fragmentManager;
    listProtectedDialogsRequest req = new listProtectedDialogsRequest();
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_protected_messages, container, false);
        Activity_Navigation.i=0;
        AuthTask at = new AuthTask();
        at.execute(authRequest.getToken());
        return view;
    }

    private void initListDialog() {
        final protectedDialog[] dialog = req.getProtectedDialogs();
        countDialog = dialog.length;

        listViewMessage = (ListView) view.findViewById(R.id.list_protected_messages);

        listMessItem = new ArrayList<Messages_Item>();
        for (int i = 0; i < countDialog; i++) {
            listMessItem.add(new Messages_Item(dialog[i].getName(), R.mipmap.ic_account, dialog[i].Login));
        }

        MessagesListAdapter messagesListAdapter = new MessagesListAdapter(getActivity(),
                R.layout.item_messages, listMessItem);
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

                Activity_Navigation.i = 5;
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_content, listFragmentMess.get(position)).addToBackStack("5").commit();
                Activity_Navigation.toolbar.setTitle(dialog[position].getName());
            }
        });
    }
    public static void protected_mess_backButtonWasPressed() {
        Activity_Navigation.i = 0;
        Activity_Navigation.toolbar.setTitle("Защищенные диалоги");
        fragmentManager.popBackStack();
    }


    public class AuthTask extends AsyncTask<String, Void, String> {
        private Socket socket = null;
        private listProtectedDialogsRequest prReq=null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prReq = new listProtectedDialogsRequest();
        }

        @Override
        protected void onPostExecute(final String ret) {
            try {
                socket.close();
                System.out.println(ret);
                prReq.responseHandler(ret);
                prReq.errorHandler();
                initListDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... data) {
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
